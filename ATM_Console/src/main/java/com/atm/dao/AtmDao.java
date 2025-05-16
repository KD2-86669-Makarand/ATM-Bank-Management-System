package com.atm.dao;

import java.sql.*;
import java.util.Scanner;

import javax.print.DocFlavor.STRING;

import com.atm.util.DbUtil;

public class AtmDao {

    public static boolean authenticateUser(String card, String pin) {
        String sql = "SELECT * FROM user WHERE card_number = ? AND pin = ?";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

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

            String updateBalance = "UPDATE user SET balance = balance + ? WHERE card_number = ?";
            try (PreparedStatement ps = con.prepareStatement(updateBalance)) {
                ps.setDouble(1, amount);
                ps.setString(2, card);
                ps.executeUpdate();
            }

            String insertTransaction = "INSERT INTO transactions(card_number, type, amount) VALUES (?, 'Deposit', ?)";
            try (PreparedStatement log = con.prepareStatement(insertTransaction)) {
                log.setString(1, card);
                log.setDouble(2, amount);
                log.executeUpdate();
            }

            con.commit();
            return "₹" + amount + " deposited successfully.";
        } catch (SQLException e) {
            return "Error during deposit.";
        }
    }
 
    public static String withdraw(String card, double amount) {
        try (Connection con = DbUtil.getConnection()) {
            con.setAutoCommit(false);

            String checkBalance = "SELECT balance FROM user WHERE card_number = ?";
            try (PreparedStatement check = con.prepareStatement(checkBalance)) {
                check.setString(1, card);
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    double balance = rs.getDouble("balance");

                    if (balance >= amount) {
                        String updateBalance = "UPDATE user SET balance = balance - ? WHERE card_number = ?";
                        try (PreparedStatement ps = con.prepareStatement(updateBalance)) {
                            ps.setDouble(1, amount);
                            ps.setString(2, card);
                            ps.executeUpdate();
                        }

                        String insertTransaction = "INSERT INTO transactions(card_number, type, amount) VALUES (?, 'Withdraw', ?)";
                        try (PreparedStatement log = con.prepareStatement(insertTransaction)) {
                            log.setString(1, card);
                            log.setDouble(2, amount);
                            log.executeUpdate();
                        }

                        con.commit();
                        return "₹" + amount + " withdrawn successfully.";
                    } else {
                        return "Insufficient funds.";
                    }
                } else {
                    return "Card not found.";
                }
            }

        } catch (SQLException e) {
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
        String resetsql = "UPDATE users SET pin = ? WHERE card_number = ?";
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
    
    public static void setInitialPin(String card) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Set your new 4-digit PIN: ");
        String newPin = sc.nextLine();
        String query = "UPDATE user SET pin = ? WHERE card_number = ?";
        try (Connection con = DbUtil.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, newPin);
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
