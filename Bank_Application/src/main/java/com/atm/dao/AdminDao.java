package com.atm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.atm.util.DbUtil;

public class AdminDao 
{
//	 public static boolean authenticateUser(String card, String pin) {
//	        String sql = "SELECT * FROM users WHERE card_number = ? AND pin = ?";
//
//	        try (Connection con = DbUtil.getConnection();
//	             PreparedStatement stmt = con.prepareStatement(sql)) {
//
//	            stmt.setString(1, card);
//	            stmt.setString(2, pin);
//
//	            ResultSet rs = stmt.executeQuery();
//	            return rs.next();
//
//	        } catch (Exception e) {
//	            System.err.println("Something went wrong during authentication.");
//	            return false;
//	        }
//	    }
	
	public static boolean authenticateAdmin(String username, String password)
	{
		String adminAuthenticateSql = "SELECT * FROM admin WHERE username = ? AND password = ?";
		try(Connection con = DbUtil.getConnection();
			PreparedStatement stmt = con.prepareStatement(adminAuthenticateSql)) 
		{
			stmt.setString(1, username);
			stmt.setString(2, password);
			 
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
	            return true;
	        } else {
	            return false;
	        }
			
		} 
		catch (Exception e) 
		{
			System.err.println("Error during admin authentication ");
			e.printStackTrace();
			return false;
		}
		
	}
}
