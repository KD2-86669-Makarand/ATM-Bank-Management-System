package com.atm.tester;

import java.util.Scanner;

import com.atm.dao.AtmDao;

public class ATMTester 
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter Card number : ");
		String card = sc.nextLine();
		
		// Step 1: Check if PIN is set
		if (!AtmDao.isPinSet(card)) {
		    System.out.println("You haven't set your PIN yet.");
		    System.out.println();
	        System.out.print("Set your new 4-digit PIN: ");
	        int newPin = sc.nextInt();
		    AtmDao.setInitialPin(card, newPin);
		    // Optional: Exit and force the user to login again
		    return;
		}
		
		System.out.println("Enter Pin : ");
		String pin = sc.nextLine();
		
		System.out.println();
		
		if(AtmDao.authenticateUser(card, pin))
		{
			String name = AtmDao.getName(card);
			
			System.out.println("Welcome " + name + " To ATM Concole");
			System.out.println();
			int choice;
			
			do 
			{
				System.out.println("0. Exit");
				System.out.println("1. Check balance");
				System.out.println("2. Deposit");
				System.out.println("3. Withdraw");
				System.out.println("4. Transaction History");
				System.out.println("5. Pin reset");
				System.out.println();
				System.out.println("Enter the choice : ");
				choice = sc.nextInt();
				sc.nextLine();
				switch (choice) 
				{
					case 0 :
					{
						System.err.println("Thank you for using our ATM Services !!!");
						System.err.println("Visit Again !!!!");
						System.exit(1);
						break;
					}
					case 1:
					{
					    System.out.println(AtmDao.checkBalance(card));
					    System.out.println();
					    break;
					}
	
					case 2:
					{
					    System.out.println("Enter Amount to Deposit : ");
					    double depositAmount = sc.nextDouble();
					    
					    if(depositAmount <= 0)
					    {
					    	System.err.println("Invalid Deposit amount !!!");
					    	System.out.println();
					    	return;
					    }
					    else
					    {
						    System.out.println();
						    System.out.println(AtmDao.deposit(card, depositAmount));
						    System.out.println();
					    }
					    break;
					}
	
					case 3:
					{
					    System.out.println("Enter Amount to Withdraw : ");
					    double withdrawAmount = sc.nextDouble();
					    System.out.println();
					    System.out.println(AtmDao.withdraw(card, withdrawAmount));
					    System.out.println();
					    break;
					}
	
					case 4:
					{
					    System.out.println(AtmDao.showTransactions(card));
					    System.out.println();
					    break;
					}
	
					case 5:
					{
					    sc.nextLine(); // clear buffer
					    System.out.println("Enter old pin : ");
					    String oldPin = sc.nextLine();
					    System.out.println();
					    System.out.println("Enter new pin : ");
					    String newPin = sc.nextLine();
					    System.out.println();
					    System.out.println(AtmDao.resetPassword(card, oldPin, newPin));
					    System.out.println();
					    break;
					}
					
					default:
					{
						System.err.println("Invalid Choice !!!");
						System.out.println();
						break;
					}
				}
				
				
			} while (choice != 0);
		}
		else
		{
			System.err.println("Invalid Card or PIN");
		}
		
		sc.close();
		
	}
}

//case 0 :
//{
//	System.err.println("Thank you for using our ATM Services !!!");
//	System.err.println("Visit Again !!!!");
//	System.exit(1);
//	break;
//}
//
//case 1 :
//{
//	AtmDao.checkBalance(card);
//	break;
//}
//
//case 2 : 
//{
//	System.out.println("Enter Amount to Deposit : ");
//	double DepositAmount = sc.nextDouble();
//	AtmDao.deposit(card, DepositAmount);
//	break;
//}
//
//case 3 :
//{
//	System.out.println("Enter Amount to Withdraw : ");
//	double withdrawAmount = sc.nextDouble();
//	AtmDao.withdraw(card, withdrawAmount);
//	break;
//}
//
//case 4 :
//{
////	System.out.println("Enter your Card number : ");
////	String cardnum = sc.nextLine();
//	AtmDao.showTransactions(card);
//	break;
//}
//
//case 5:
//{
//	System.out.println("Enter old pin : ");
//	String oldPin = sc.nextLine();
//
//	System.out.println("Enter new pin : ");
//	String newPin = sc.nextLine();
//	
//	AtmDao.resetPassword(card, oldPin, newPin);
//	break;
//}