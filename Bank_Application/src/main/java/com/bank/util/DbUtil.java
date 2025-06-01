package com.bank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.bank.dao.AdminDao;

public class DbUtil 
{
	private static final String DB_URL = "jdbc:mysql://localhost:3306/ebank";
	private static final String DB_USER = "makarand";
	private static final String DB_PASSWORD = "manager";

	public static Connection getConnection() throws SQLException
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		}
		catch (Exception e) 
		{
	            e.printStackTrace();
	            return null;
		}
	}
	
}
