package com.atm.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.atm.util.DbUtil;

public class UserDao 
{
	private static final long STARTING_CARD_NUMBER = 4827159360482175L;
	
	public static String registerUser(String firstName, String middleName, String lastName) {
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
	        String insertUser = "INSERT INTO user (first_name, middle_name, last_name, card_number, valid_till) VALUES(?, ?, ?, ?, ?)";
	        try (PreparedStatement ps = con.prepareStatement(insertUser)) {
	            ps.setString(1, firstName);
	            ps.setString(2, middleName);
	            ps.setString(3, lastName);
	            ps.setLong(4, newCardNumber);
	            ps.setDate(5, sqlValidTill);
	            
	            ps.executeUpdate();
	            
	            // 4. Format response
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
	            return "User registered.\nCard Number: " + newCardNumber +
	                   "\nValid Till: " + validTill.format(formatter);
	        }
	    } catch (Exception e) {
	        System.err.println("Something went wrong during user registration: " + e.getMessage());
	        return "Registration failed: " + e.getMessage();
	    }
	}
	
}
