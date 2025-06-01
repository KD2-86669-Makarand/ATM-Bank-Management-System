package com.atm.dao;

import java.io.FileWriter;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.MessagingException;
import javax.print.DocFlavor.STRING;

import com.atm.emailer.EmailSender;
import com.atm.util.DbUtil;
import com.atm.util.EmailUtil;
import com.mysql.cj.jdbc.ha.BalanceStrategy;

public class AtmDao 
{
	public static String getName(String card) {
	    String sql = "SELECT first_name, last_name FROM user WHERE card_number = ?";
	    
	    try (Connection con = DbUtil.getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql)) {
	        
	        stmt.setString(1, card);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            String firstName = rs.getString("first_name");
	            String lastName = rs.getString("last_name");
	            return firstName + " " + lastName;
	        }
	        
	    } catch (Exception e) {
	        System.err.println("Something went wrong during getName: " + e.getMessage());
	    }
	    
	    return "User not found";
	}

	public static boolean authenticateUser(String card, String pin) 
	{
        String sql = "SELECT * FROM user WHERE card_number = ? AND pin = ?";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) 
        {

            stmt.setString(1, card);
            stmt.setString(2, pin);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.err.println("Something went wrong during authentication.");
            return false;
        }
    }
    
    public static String checkBalance(String card) {
        String sql = "SELECT balance FROM user WHERE card_number = ?";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, card);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "Balance: ₹" + rs.getDouble("balance");
            } else {
                return "Card not found.";
            }
        } catch (Exception e) {
            return "Error during balance check.";
        }
    }
    
    public static String deposit(String card, double amount) {
        try (Connection con = DbUtil.getConnection()) {
            con.setAutoCommit(false);

            // 1. Update balance
            String updateBalance = "UPDATE user SET balance = balance + ? WHERE card_number = ?";
            try (PreparedStatement ps = con.prepareStatement(updateBalance)) {
                ps.setDouble(1, amount);
                ps.setString(2, card);
                ps.executeUpdate();
            }

            // 2. Insert transaction
            String insertTransaction = "INSERT INTO transactions(card_number, type, amount) VALUES (?, 'Deposit', ?)";
            String transactionId = null;
            try (PreparedStatement log = con.prepareStatement(insertTransaction, Statement.RETURN_GENERATED_KEYS)) {
                log.setString(1, card);
                log.setDouble(2, amount);
                log.executeUpdate();
                ResultSet generatedKeys = log.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transactionId = "TXN" + generatedKeys.getInt(1);
                }
            }

            con.commit();

            // 3. Fetch details
            String userSql = "SELECT first_name, balance, email FROM user u WHERE u.card_number = ?";
            try (PreparedStatement userStmt = con.prepareStatement(userSql)) {
                userStmt.setString(1, card);
                ResultSet rs = userStmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("first_name");
                    double balance = rs.getDouble("balance");
                    String email = rs.getString("email");

                    // 4. Format datetime
                    String dateTime = java.time.LocalDateTime.now().toString();

                    // 5. Send email
                    EmailSender.sendDepositEmail(email, name, card, amount, transactionId, balance, dateTime);
                }
            }

            return "₹" + amount + " deposited successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during deposit.";
        }
    }

    public static String withdraw(String card, double amount) {
        try (Connection con = DbUtil.getConnection()) {
            con.setAutoCommit(false);

            String checkBalSql = "SELECT balance FROM user WHERE card_number = ?";
            try (PreparedStatement stmt = con.prepareStatement(checkBalSql)) {
                stmt.setString(1, card);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    return "Card not found.";
                }

                double availableBalance = rs.getDouble("balance");

                if (availableBalance < amount) {
                    return "Insufficient funds.";
                }

                // 1. Deduct balance
                String updateBalance = "UPDATE user SET balance = balance - ? WHERE card_number = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateBalance)) {
                    updateStmt.setDouble(1, amount);
                    updateStmt.setString(2, card);
                    updateStmt.executeUpdate();
                }

                // 2. Insert transaction and get transaction ID
                String insertTransaction = "INSERT INTO transactions(card_number, type, amount) VALUES (?, 'Withdraw', ?)";
                String transactionId = null;
                try (PreparedStatement log = con.prepareStatement(insertTransaction, Statement.RETURN_GENERATED_KEYS)) {
                    log.setString(1, card);
                    log.setDouble(2, amount);
                    log.executeUpdate();

                    ResultSet generatedKeys = log.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        transactionId = "TXN" + generatedKeys.getInt(1);
                    }
                }

                // 3. Fetch user details
                String userSql = "SELECT first_name, balance, email FROM user WHERE card_number = ?";
                try (PreparedStatement userStmt = con.prepareStatement(userSql)) {
                    userStmt.setString(1, card);
                    ResultSet userRs = userStmt.executeQuery();

                    if (userRs.next()) {
                        String name = userRs.getString("first_name");
                        double newBalance = userRs.getDouble("balance");
                        String email = userRs.getString("email");

                        String dateTime = java.time.LocalDateTime.now().toString();
                        EmailSender.sendWithdrawalEmail(email, name, card, amount, transactionId, newBalance, dateTime);
                    }
                }

                con.commit();
                return "₹" + amount + " withdrawn successfully.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during withdrawal.";
        }
    }

    
    public static String showTransactions(String card) {
        StringBuilder output = new StringBuilder();
        String sql = "SELECT * FROM transactions WHERE card_number = ? ORDER BY timestamp DESC";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, card);
            ResultSet rs = ps.executeQuery();

            output.append("Recent Transactions:\n");
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                Timestamp time = rs.getTimestamp("timestamp");
                output.append(String.format("%s: ₹%.2f on %s\n", type, amount, time));
            }

        } catch (SQLException e) {
            return "Error during transaction history retrieval.";
        }

        return output.toString();
    }
    
    
    public static String resetPassword(String card, String oldPin, String newPin) {
        String checksql = "SELECT * FROM user WHERE card_number = ? AND pin = ?";
        String resetsql = "UPDATE user SET pin = ? WHERE card_number = ?";
        try (Connection con = DbUtil.getConnection()) {

            try (PreparedStatement checkstmt = con.prepareStatement(checksql)) {
                checkstmt.setString(1, card);
                checkstmt.setString(2, oldPin);

                ResultSet rs = checkstmt.executeQuery();
                if (!rs.next()) {
                    return "Invalid card or old pin!";
                }
            }

            try (PreparedStatement resetstmt = con.prepareStatement(resetsql)) {
                resetstmt.setString(1, newPin);
                resetstmt.setString(2, card);
                resetstmt.executeUpdate();
            }

            return "PIN reset successful.";

        } catch (Exception e) {
            return "Error during PIN reset.";
        }
    }
    
    public static boolean isPinSet(String card)
    {
    	String sql = "SELECT pin FROM user WHERE card_number = ?";
    	
    	try(Connection con = DbUtil.getConnection();
    			PreparedStatement stmt = con.prepareStatement(sql))
    	{
    		stmt.setString(1, card);
    		
    		ResultSet rs = stmt.executeQuery();
    		
    		if(rs.next())
    		{
    			return rs.getString("pin") != null;
    		}
    	}
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
    	return false;
    }
    
    public static void setInitialPin(String card, int newPin) 
    {
        String query = "UPDATE user SET pin = ? WHERE card_number = ?";
        try (Connection con = DbUtil.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, newPin);
            ps.setString(2, card);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("PIN set successfully! Please login now.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
