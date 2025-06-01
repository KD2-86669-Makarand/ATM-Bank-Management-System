package com.bank.model;

public class Session 
{
	private static Integer adminId = null;

	public static Integer getAdminId() 
	{
		return adminId;
	}

	public static void setAdminId(Integer adminId) 
	{
		Session.adminId = adminId;
	}
	
	
}
