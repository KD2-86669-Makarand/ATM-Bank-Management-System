package com.bank.model;

public class User 
{
	private int userId;
	private String firstName;
	private String middleName;
	private String lastName;
	private long cardnumber;
	private int pin;
	private double balance;
	
	public User() {}

	public User(String firstName, String middleName, String lastName, long cardnumber, int pin, double balance) {
		super();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.cardnumber = cardnumber;
		this.pin = pin;
		this.balance = balance;
	}

	public User(int userId, String firstName, String middleName, String lastName, long cardnumber, int pin,
			double balance) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.cardnumber = cardnumber;
		this.pin = pin;
		this.balance = balance;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getCardnumber() {
		return cardnumber;
	}

	public void setCardnumber(long cardnumber) {
		this.cardnumber = cardnumber;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName="
				+ lastName + ", cardnumber=" + cardnumber + ", pin=" + pin + ", balance=" + balance + "]";
	}
	
}
