package com.bank.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bank.emailer.EmailSender;
import com.bank.util.DbUtil;

public class UserDao 
{
	private static final long STARTING_CARD_NUMBER = 4827159360482175L;
	
	// New User Registration
	public static String registerUser(String firstName, String middleName, String lastName, String email) {
	    try (Connection con = DbUtil.getConnection()) {
	        // 1. Generate new card number
	        String getCardSql = "SELECT MAX(card_number) FROM user";
	        long newCardNumber = STARTING_CARD_NUMBER;
	        
	        try (PreparedStatement stmt = con.prepareStatement(getCardSql);
	             ResultSet rs = stmt.executeQuery()) {
	            if (rs.next() && rs.getLong(1) != 0) {
	                newCardNumber = rs.getLong(1) + 1;
	            }
	        }
	        
	        // 2. Calculate valid till date (5 years ahead)
	        LocalDate today = LocalDate.now();
	        LocalDate validTill = today.plusYears(5).withDayOfMonth(today.lengthOfMonth());
	        Date sqlValidTill = Date.valueOf(validTill);
	        
	        // 3. Insert user into database
	        String insertUser = "INSERT INTO user (first_name, middle_name, last_name, email, card_number, valid_till) VALUES(?, ?, ?, ?, ?, ?)";
	        try (PreparedStatement ps = con.prepareStatement(insertUser)) {
	            ps.setString(1, firstName);
	            ps.setString(2, middleName);
	            ps.setString(3, lastName);
	            ps.setString(4, email);
	            ps.setLong(5, newCardNumber);
	            ps.setDate(6, sqlValidTill);
	            
	            ps.executeUpdate();
	            
	            String accountNosql = "SELECT accountNo FROM user WHERE card_number = " + newCardNumber;
	            long accountNo = 0;
	            try(PreparedStatement accstmt = con.prepareStatement(accountNosql))
	            {
					ResultSet accrs = accstmt.executeQuery();
					if(accrs.next())
					
						accountNo = accrs.getLong(1);
					
					
				} catch (Exception e) {
					// TODO: handle exception
				}
	            // 4. Format response
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
	            return accountNo + "\nCard Number: " + newCardNumber +
	                   "\nValid Till: " + validTill.format(formatter);
	        }
	    } catch (Exception e) {
	        System.err.println("Something went wrong during user registration: " + e.getMessage());
	        return "Registration failed: " + e.getMessage();
	    }
	}
	
	// Check Balance 
	public static int checkBalance(long accountNo)
	{
		int balance = 0;
		String balSql = "SELECT balance FROM user WHERE accountNo = ?";
		
		try(Connection con = DbUtil.getConnection())
		{
			try(PreparedStatement stmt = con.prepareStatement(balSql))
			{
				stmt.setLong(1, accountNo);
				ResultSet rs = stmt.executeQuery();
				
				if(rs.next())
				{
					balance = rs.getInt("balance");
				}
				else
				{
					System.err.println("Account number not found!");
				}
			}
		}
		catch (Exception e) 
		{
			System.err.println("Invalid Account Number !!!");
			// TODO: handle exception
		}
		
		return balance;
	}
	
	
	// Deposit
//	public static String deposit(long accountNo, int amount) 
//	{
//	    try (Connection con = DbUtil.getConnection()) 
//	    {
//	        con.setAutoCommit(false); // Start transaction
//
//	        String updateBalanceSQL = "UPDATE user SET balance = balance + ? WHERE accountNo = ?";
//	        // 1. Update balance
//	        try (PreparedStatement updateStmt = con.prepareStatement(updateBalanceSQL)) 
//	        {
//	            updateStmt.setInt(1, amount);
//	            updateStmt.setLong(2, accountNo);
//	            int rowsUpdated = updateStmt.executeUpdate();
//
//	            if (rowsUpdated == 0) {
//	                con.rollback();
//	                return "Account not found. Deposit failed.";
//	            }
//	        }
//
//	        String getCardNumberSQL = "SELECT card_number FROM user WHERE accountNo = ?";
//	        // 2. Get card_number
//	        String cardNumber = null;
//	        try (PreparedStatement getCardStmt = con.prepareStatement(getCardNumberSQL)) 
//	        {
//	            getCardStmt.setLong(1, accountNo);
//	            ResultSet rs = getCardStmt.executeQuery();
//	            if (rs.next()) 
//	            {
//	                cardNumber = rs.getString("card_number");
//	            } 
//	            else 
//	            {
//	                con.rollback();
//	                return "Card number not found. Deposit failed.";
//	            }
//	        }
//
//	        String insertTransactionSQL = "INSERT INTO transactions(card_number, amount, type) VALUES (?, ?, 'Deposit')";
//	        // 3. Insert transaction
//	        try (PreparedStatement insertStmt = con.prepareStatement(insertTransactionSQL)) 
//	        {
//	            insertStmt.setString(1, cardNumber);
//	            insertStmt.setInt(2, amount);
//	            insertStmt.executeUpdate();
//	        }
//
//	        con.commit(); // Commit transaction
//
//	        EmailSender.sendDepositEmail(email, card, amount, balance);
//
//	        return "Deposit successful.";
//
//	    } 
//	    catch (Exception e) 
//	    {
//	        e.printStackTrace();
//	       return "Error occurred during deposit.";
//	    }
//
//	}
	public static String deposit(long accountNo, int amount) {
	    try (Connection con = DbUtil.getConnection()) {
	        con.setAutoCommit(false); // Start transaction

	        String updateBalanceSQL = "UPDATE user SET balance = balance + ? WHERE accountNo = ?";
	        try (PreparedStatement updateStmt = con.prepareStatement(updateBalanceSQL)) {
	            updateStmt.setInt(1, amount);
	            updateStmt.setLong(2, accountNo);
	            int rowsUpdated = updateStmt.executeUpdate();

	            if (rowsUpdated == 0) {
	                con.rollback();
	                return "Account not found. Deposit failed.";
	            }
	        }

	        String userInfoSQL = "SELECT card_number, email, balance FROM user WHERE accountNo = ?";
	        String cardNumber = null, email = null;
	        double newBalance = 0;

	        try (PreparedStatement getInfoStmt = con.prepareStatement(userInfoSQL)) {
	            getInfoStmt.setLong(1, accountNo);
	            ResultSet rs = getInfoStmt.executeQuery();
	            if (rs.next()) {
	                cardNumber = rs.getString("card_number");
	                email = rs.getString("email");
	                newBalance = rs.getDouble("balance");
	            } else {
	                con.rollback();
	                return "Card number or email not found. Deposit failed.";
	            }
	        }

	        String insertTransactionSQL = "INSERT INTO transactions(card_number, amount, type) VALUES (?, ?, 'Deposit')";
	        try (PreparedStatement insertStmt = con.prepareStatement(insertTransactionSQL)) {
	            insertStmt.setString(1, cardNumber);
	            insertStmt.setInt(2, amount);
	            insertStmt.executeUpdate();
	        }

	        con.commit(); // Commit transaction
	        try 
	        {
	        	EmailSender.sendDepositEmail(email, cardNumber, amount, newBalance);
			} 
	        catch (Exception e) 
	        {
	        	e.printStackTrace();
	        	System.err.println("Email could not sent check internet connection!!!");
			}
	        return "Deposit successful.";
	    } 
	    catch (Exception e) 
	    {
	        e.printStackTrace();
	        return "Error occurred during deposit.";
	    }
	}

	// Withdraw
//	public static String withdraw(long accountNo, int amount)
//	{
//		
//		String updateBalance = "UPDATE user SET balance = balance - ? WHERE accountNo = ?";
//		// update balance
//		try(Connection con = DbUtil.getConnection())
//		{
//			con.setAutoCommit(false);
//			
//			try(PreparedStatement stmt = con.prepareStatement(updateBalance))
//			{
//				stmt.setInt(1, amount);
//				stmt.setLong(2, accountNo);
//				int rowsUpdated = stmt.executeUpdate();
//
//	            if (rowsUpdated == 0) {
//	                con.rollback();
//	                return "Account not found. Deposit failed.";
//	            }
//	            
//	            String getCardNumberSQL = "SELECT card_number FROM user WHERE accountNo = ?";
//		        // 2. Get card_number
//		        String cardNumber = null;
//		        try (PreparedStatement getCardStmt = con.prepareStatement(getCardNumberSQL)) 
//		        {
//		            getCardStmt.setLong(1, accountNo);
//		            ResultSet rs = getCardStmt.executeQuery();
//		            if (rs.next()) 
//		            {
//		                cardNumber = rs.getString("card_number");
//		            } 
//		            else 
//		            {
//		                con.rollback();
//		                return "Card number not found. Withdraw failed.";
//		            }
//		        }
//
//		        String insertTransactionSQL = "INSERT INTO transactions(card_number, amount, type) VALUES (?, ?, 'Withdraw')";
//		        // 3. Insert transaction
//		        try (PreparedStatement insertStmt = con.prepareStatement(insertTransactionSQL)) 
//		        {
//		            insertStmt.setString(1, cardNumber);
//		            insertStmt.setInt(2, amount);
//		            insertStmt.executeUpdate();
//		        }
//
//		        con.commit(); // Commit transaction
//
//		        EmailSender.sendWithdrawalEmail(email, card, amount, newBalance);
//
//		        return "Withdraw successful.";
//				
//			}
//		}
//		catch (Exception e) 
//		{
//			 e.printStackTrace();
//		       return "Error occurred during deposit.";
//		}
//	}
	public static String withdraw(long accountNo, int amount) {
	    try (Connection con = DbUtil.getConnection()) {
	        con.setAutoCommit(false);

	        // Check balance first
	        String checkBalanceSQL = "SELECT balance FROM user WHERE accountNo = ?";
	        double balance = 0;
	        try (PreparedStatement checkStmt = con.prepareStatement(checkBalanceSQL)) {
	            checkStmt.setLong(1, accountNo);
	            ResultSet rs = checkStmt.executeQuery();
	            if (rs.next()) {
	                balance = rs.getDouble("balance");
	                if (balance < amount) {
	                    return "Insufficient balance.";
	                }
	            } else {
	                return "Account not found.";
	            }
	        }

	        // Update balance
	        String updateBalance = "UPDATE user SET balance = balance - ? WHERE accountNo = ?";
	        try (PreparedStatement stmt = con.prepareStatement(updateBalance)) {
	            stmt.setInt(1, amount);
	            stmt.setLong(2, accountNo);
	            int rowsUpdated = stmt.executeUpdate();

	            if (rowsUpdated == 0) {
	                con.rollback();
	                return "Account not found. Withdraw failed.";
	            }
	        }

	        String getCardInfoSQL = "SELECT card_number, email, balance FROM user WHERE accountNo = ?";
	        String cardNumber = null, email = null;
	        double newBalance = 0;

	        try (PreparedStatement getCardStmt = con.prepareStatement(getCardInfoSQL)) {
	            getCardStmt.setLong(1, accountNo);
	            ResultSet rs = getCardStmt.executeQuery();
	            if (rs.next()) {
	                cardNumber = rs.getString("card_number");
	                email = rs.getString("email");
	                newBalance = rs.getDouble("balance");
	            } else {
	                con.rollback();
	                return "User info not found. Withdraw failed.";
	            }
	        }

	        String insertTransactionSQL = "INSERT INTO transactions(card_number, amount, type) VALUES (?, ?, 'Withdraw')";
	        try (PreparedStatement insertStmt = con.prepareStatement(insertTransactionSQL)) {
	            insertStmt.setString(1, cardNumber);
	            insertStmt.setInt(2, amount);
	            insertStmt.executeUpdate();
	        }

	        con.commit();
	        EmailSender.sendWithdrawalEmail(email, cardNumber, amount, newBalance);
	        return "Withdraw successful.";

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Error occurred during withdraw.";
	    }
	}

	// transaction history 
	public static String showTransactions(long accountNo) {
	    StringBuilder output = new StringBuilder();
	    
	    String getCardSql = "SELECT card_number FROM user WHERE accountNo = ?";
	    String transactionSql = "SELECT * FROM transactions WHERE card_number = ? ORDER BY timestamp DESC";

	    try (Connection con = DbUtil.getConnection()) {
	        String cardNumber = null;

	        // Step 1: Get card number from accountNo
	        try (PreparedStatement getCardStmt = con.prepareStatement(getCardSql)) {
	            getCardStmt.setLong(1, accountNo);
	            ResultSet rs = getCardStmt.executeQuery();
	            if (rs.next()) {
	                cardNumber = rs.getString("card_number");
	            } else {
	                return "Account number not found.";
	            }
	        }

	        // Step 2: Use card number to get transactions
	        try (PreparedStatement transStmt = con.prepareStatement(transactionSql)) {
	            transStmt.setString(1, cardNumber);
	            ResultSet rs = transStmt.executeQuery();

	            output.append("Recent Transactions:\n");
	            while (rs.next()) {
	                String type = rs.getString("type");
	                double amount = rs.getDouble("amount");
	                Timestamp time = rs.getTimestamp("timestamp");
	                output.append(String.format("%s: ₹%.2f on %s\n", type, amount, time));
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace(); // optional for debugging
	        return "Error during transaction history retrieval.";
	    }

	    return output.toString();
	}
	
	// New method to fetch transaction history as a list of Transaction objects
    public static List<Transaction> getTransactionHistory(long accountNo) {
        List<Transaction> transactions = new ArrayList();
        String getCardSql = "SELECT card_number FROM user WHERE accountNo = ?";
        String transactionSql = "SELECT type, amount, timestamp FROM transactions WHERE card_number = ? ORDER BY timestamp DESC";

        try (Connection con = DbUtil.getConnection()) {
            // Step 1: Get card number from accountNo
            String cardNumber = null;
            try (PreparedStatement getCardStmt = con.prepareStatement(getCardSql)) {
                getCardStmt.setLong(1, accountNo);
                ResultSet rs = getCardStmt.executeQuery();
                if (rs.next()) {
                    cardNumber = rs.getString("card_number");
                } else {
                    throw new SQLException("Account number not found.");
                }
            }

            // Step 2: Get transactions
            try (PreparedStatement transStmt = con.prepareStatement(transactionSql)) {
                transStmt.setString(1, cardNumber);
                ResultSet rs = transStmt.executeQuery();

                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setType(rs.getString("type"));
                    transaction.setAmount(rs.getDouble("amount"));
                    transaction.setTimestamp(rs.getTimestamp("timestamp"));
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
        }
        return transactions;
    }

    // Inner class to hold transaction data
    public static class Transaction {
        private String type;
        private double amount;
        private Timestamp timestamp;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public Timestamp getTimestamp() { return timestamp; }
        public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    }
	
	
	// reset password
    public static String resetPassword(long accountNo, int oldPin, int newPin) 
    {
        String checksql = "SELECT * FROM user WHERE accountNo = ? AND pin = ?";
        String resetsql = "UPDATE user SET pin = ? WHERE accountNo = ?";
        try (Connection con = DbUtil.getConnection()) {

            try (PreparedStatement checkstmt = con.prepareStatement(checksql)) {
                checkstmt.setLong(1, accountNo);
                checkstmt.setInt(2, oldPin);

                ResultSet rs = checkstmt.executeQuery();
                if (!rs.next()) {
                    return "Invalid card or old pin!";
                }
            }

            try (PreparedStatement resetstmt = con.prepareStatement(resetsql)) {
                resetstmt.setInt(1, newPin);
                resetstmt.setLong(2, accountNo);
                resetstmt.executeUpdate();
            }

            return "PIN reset successful.";

        } catch (Exception e) {
            return "Error during PIN reset.";
        }
    }
    
    // fixed deposit
    public static String createFixedDeposit(long accountNo, double amount, int durationMonths)
    {
    		double interestRate = 6.5;
    		
    		try(Connection con = DbUtil.getConnection())
    		{
    			con.setAutoCommit(false);
    			
    			// 1. check balance
    			String getBalance = "SELECT balance FROM user WHERE accountNo = ?";
    			try(PreparedStatement stmt = con.prepareStatement(getBalance))
    			{
    				stmt.setLong(1, accountNo);
    				
    				ResultSet rs = stmt.executeQuery();
    				
    				if (rs.next()) 
    				{
    					double balance = rs.getDouble("balance");
    					if(balance < amount)
    					{
    						System.err.println("Available Balance : " + balance);
    						System.err.println("Insufficient Balance to create FD !!!");
    					}
					}
    				else
    				{
    					System.err.println("Account not found !!!");
    				}
    			}
    			
    			// 2. deduct  balance 
    			String deductbalance = "UPDATE user SET balance = balance - ? WHERE accountNo = ?";
    			try(PreparedStatement ps = con.prepareStatement(deductbalance))
    			{
    				ps.setDouble(1, amount);
    				ps.setLong(2, accountNo);
    				ps.executeUpdate();
    			}
    			
    			
    			// 3. Insert FD
    			String insertFd = "INSERT INTO fixed_deposits(accountNo, amount, interest_rate, duration_months, start_date, maturity_date, status) VALUES(?, ?, ?, ?, ?, ?, ?)";
    			LocalDate startDate = LocalDate.now();
    	        LocalDate maturityDate = startDate.plusMonths(durationMonths);
    			try(PreparedStatement fdstmt = con.prepareStatement(insertFd))
    			{
    				fdstmt.setLong(1, accountNo);
    				fdstmt.setDouble(2, amount);
    				fdstmt.setDouble(3, interestRate);
    				fdstmt.setInt(4, durationMonths);
    				fdstmt.setDate(5, Date.valueOf(startDate));
    				fdstmt.setDate(6, Date.valueOf(maturityDate));
    				fdstmt.setString(7, "Active");
    				fdstmt.executeUpdate();
    			}
    			
    			con.commit();
    			 return "FD created successfully for amount Rs. " + amount + " for " + durationMonths + " months.";
    			
    		}
    		catch (Exception e) 
    		{
    			e.printStackTrace();
    			return "Failed to create FD";
			}
    }
    
//    public static String matureFixedDeposit(int fdId) {
//        try (Connection con = DbUtil.getConnection()) {
//            con.setAutoCommit(false);
//
//            // 1. Get FD details
//            String fdSQL = "SELECT account_no, amount, interest_rate, duration_months, start_date, maturity_date, status FROM fixed_deposits WHERE fd_id = ?";
//            long accountNo = 0;
//            double amount = 0, rate = 0;
//            int duration = 0;
//            String status = "";
//            Date maturityDate = null;
//
//            try (PreparedStatement ps = con.prepareStatement(fdSQL)) {
//                ps.setInt(1, fdId);
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    accountNo = rs.getLong("account_no");
//                    amount = rs.getDouble("amount");
//                    rate = rs.getDouble("interest_rate");
//                    duration = rs.getInt("duration_months");
//                    maturityDate = rs.getDate("maturity_date");
//                    status = rs.getString("status");
//
//                    if (!status.equalsIgnoreCase("Active")) {
//                        return "FD already matured.";
//                    }
//
//                    if (LocalDate.now().isBefore(maturityDate.toLocalDate())) {
//                        return "FD is not yet matured.";
//                    }
//                } else {
//                    return "FD not found.";
//                }
//            }
//
//            // 2. Calculate maturity amount (Simple interest)
//            double maturityAmount = amount + (amount * rate * duration) / (12 * 100);
//
//            // 3. Update user's balance
//            String updateBalanceSQL = "UPDATE user SET balance = balance + ? WHERE accountNo = ?";
//            try (PreparedStatement ps = con.prepareStatement(updateBalanceSQL)) {
//                ps.setDouble(1, maturityAmount);
//                ps.setLong(2, accountNo);
//                ps.executeUpdate();
//            }
//
//            // 4. Mark FD as matured
//            String updateFdSQL = "UPDATE fixed_deposits SET status = 'Matured' WHERE fd_id = ?";
//            try (PreparedStatement ps = con.prepareStatement(updateFdSQL)) {
//                ps.setInt(1, fdId);
//                ps.executeUpdate();
//            }
//
//            con.commit();
//            return "FD matured. Rs. " + maturityAmount + " credited to account.";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "FD maturity failed.";
//        }
//    }
    
    public static void autoMatureFDs() {
        System.out.println("FD Maturity Job Running...");

        try (Connection con = DbUtil.getConnection()) {

            con.setAutoCommit(false);

            // Get matured FDs
            String selectSQL = "SELECT fd_id, accountNo, amount, interest_rate FROM fixed_deposits WHERE DATE(maturity_date) <= CURDATE() AND status = 'Active'";
            try (PreparedStatement ps = con.prepareStatement(selectSQL)) {
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int fdId = rs.getInt("fd_id");
                    long accountNo = rs.getLong("accountNo");
                    int amount = rs.getInt("amount");
                    float interestRate = rs.getFloat("interest_rate");

                    double maturityAmount = amount + (amount * interestRate / 100.0);
                    int maturityAmountInt = (int) Math.round(maturityAmount);

                    // 1. Update user balance
                    String updateBalanceSQL = "UPDATE user SET balance = balance + ? WHERE accountNo = ?";
                    try (PreparedStatement updateBalance = con.prepareStatement(updateBalanceSQL)) {
                        updateBalance.setInt(1, maturityAmountInt);
                        updateBalance.setLong(2, accountNo);
                        updateBalance.executeUpdate();
                    }

                    // 2. Update FD status
                    String updateFDStatusSQL = "UPDATE fixed_deposits SET status = 'Matured' WHERE fd_id = ?";
                    try (PreparedStatement updateFD = con.prepareStatement(updateFDStatusSQL)) {
                        updateFD.setInt(1, fdId);
                        updateFD.executeUpdate();
                    }

                    System.out.println("FD ID " + fdId + " matured. Amount " + maturityAmountInt + " credited.");
                }
            }

            con.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void startScheduler() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            UserDao.autoMatureFDs();
        }, 0, 1, TimeUnit.DAYS); // run once daily
    }

    public static List<Map<String, Object>> getUserFDs(long accountNo) {
        List<Map<String, Object>> fdList = new ArrayList<>();
        String sql = "SELECT * FROM fixed_deposits WHERE accountNo = ?";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, accountNo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> fd = new HashMap<>();
                fd.put("fdId", rs.getInt("fd_id"));
                fd.put("amount", rs.getDouble("amount"));
                fd.put("rate", rs.getDouble("interest_rate"));
                fd.put("duration", rs.getInt("duration_months"));
                fd.put("startDate", rs.getDate("start_date"));
                fd.put("maturityDate", rs.getDate("maturity_date"));
                fd.put("status", rs.getString("status"));
                fdList.add(fd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fdList;
    }

}
