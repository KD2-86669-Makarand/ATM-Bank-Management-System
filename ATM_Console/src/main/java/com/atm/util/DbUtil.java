package com.atm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil 
{
	private static final String DB_URL = "jdbc:mysql://localhost:3306/ebank";
	
	private static final String DB_USER = "makarand";
	
	private static final String DB_PASSWORD = "mak";
	
	
	public static Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	}
}
