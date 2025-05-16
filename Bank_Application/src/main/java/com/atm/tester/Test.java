package com.atm.tester;

import java.util.Scanner;

import com.atm.dao.AdminDao;
import com.atm.dao.UserDao;


public class Test 
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter Username : ");
		String username = sc.nextLine();
		System.out.println();
		System.out.println("Enter Password : ");
		String password = sc.nextLine();
		
		
		if(AdminDao.authenticateAdmin(username, password))
		{
			System.out.println("Welcome " + username + " To Bank Console Application");
			System.out.println();
			int choice;
			
			do
			{
				System.out.println("0. Exit");
				System.out.println("1. Register User");
				System.out.println("2. Check Balance");
				System.out.println("3. Deposit");
				System.out.println("4. Withdraw");
				System.out.println("5. Transaction History");
				System.out.println("6. Reset Pin");
				System.out.println();
				System.out.println("Enter your choice : ");
				choice = sc.nextInt();
				sc.nextLine();
				switch (choice) 
				{
					case 0 :
					{
						System.err.println("Thank you for using our Banking Services !!!");
						System.err.println("Visit Again !!!!");
						System.exit(1);
						break;
					}
					
					case 1 :
					{
						System.out.println("Enter First Name : ");
						String firstName = sc.nextLine();
						System.out.println("Enter Middle Name : ");
						String middleName = sc.nextLine();
						System.out.println("Enter Last Name : ");
						String lastName = sc.nextLine();
						
//						UserDao.registerUser(firstName, middleName, lastName);
						String result = UserDao.registerUser(firstName, middleName, lastName);
				        System.out.println(result);
						break;
					}

					default:
					{
						
						break;
					}
				}
				
			}while(choice != 0);
		}
		else
		{
			System.err.println("Invalid username or password. Please try again.");
		}
		
		sc.close();
	}
}
