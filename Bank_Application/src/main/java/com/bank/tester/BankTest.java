package com.bank.tester;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import com.bank.dao.AdminDao;
import com.bank.dao.UserDao;
import com.bank.emailer.EmailSender;
import com.bank.model.Session;
import com.bank.util.DbUtil;
import com.bank.util.FileGenerator;


public class BankTest 
{
	public static void main(String[] args)
	{
		UserDao.startScheduler();
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Press Any key ");
		sc.nextLine();

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
				System.out.println("7. Email transaction History");
				System.out.println("8. Create New FD");
				System.out.println("9. Check FD status");
				System.out.println("10. View Profile Info");
				System.out.println("Your Admin_Id : "+Session.getAdminId());
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
						Session.setAdminId(null);
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
					    System.out.println("Enter Email : ");
					    String email = sc.nextLine();

					    String accountNumber = UserDao.registerUser(firstName, middleName, lastName, email);
					    System.out.println("User registered successfully. Account Number: " + accountNumber);

					    String dateTime = java.time.LocalDateTime.now().toString();
					    
					    try {
					        EmailSender.sendAccountCreationEmail(
					            email,                        // toEmail
					            firstName + " " + lastName,  // customerName
					            accountNumber,               // accountNumber
					            2000.00,                     // initial balance
					            dateTime                     // account creation datetime
					        );
					        System.out.println("Account creation email sent successfully.");
					    } 
					    catch (Exception e) 
					    {
					        System.err.println("Failed to send email: " + e.getMessage());
					    }
					    
					    break;
					}
					
					case 2 :
					{
						System.out.println("Enter Account Number : ");
						long accountNo = sc.nextLong();
						
						System.out.println("Available Balance : " + UserDao.checkBalance(accountNo));
						break;
					}

					case 3 :
					{
						
						System.out.println("Enter Account Number : ");
						long accountNo = sc.nextLong();
						
						System.out.println("Enter Amount to Deposit :");
						int amount = sc.nextInt();
						
						int adminId = Session.getAdminId();
						
						System.out.println("Amount Diposited : " + UserDao.deposit(accountNo, amount, adminId));
						break;
					}

					case 4 :
					{
						System.out.println("Enter Account Number : ");
						long accountNo = sc.nextLong();
						
						System.out.println("Enter Amount to Withdraw :");
						int amount = sc.nextInt();
						
						int adminId = Session.getAdminId();
						
						System.out.println("Amount Withdrawn : " + UserDao.withdraw(accountNo, amount, adminId));
						break;

					}
					
					case 5 :
					{
						System.out.println("Enter Account Number : ");
						long accountNo = sc.nextLong();
						System.out.println("Transaction History : ");
						System.out.println(UserDao.showTransactions(accountNo));
						break;
					}
					
					case 6 :
					{
						System.out.println("Enter Account Number : ");
						long accountNo = sc.nextLong();
						
						break;
					}
					
					case 7:
					{
                        System.out.println("Enter Account Number: ");
                        long accountNo = sc.nextLong();
                        System.out.println("Enter email format (PDF/CSV): ");
                        String format = sc.nextLine().trim().toUpperCase(); // sc.nextLine().trim().toUpperCase();
                        
                        try {
                            // Get email from user table
                            String userEmail = null;
                            String cardNumber = null;
                            try (Connection con = DbUtil.getConnection()) {
                                String sql = "SELECT email, card_number FROM user WHERE accountNo = ?";
                                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                                    stmt.setLong(1, accountNo);
                                    ResultSet rs = stmt.executeQuery();
                                    if (rs.next()) {
                                        userEmail = rs.getString("email");
                                        cardNumber = rs.getString("card_number");
                                    } else {
                                        System.out.println("Account not found.");
                                        break;
                                    }
                                }
                            }

                            // Generate file based on format
                            File file;
                            var transactions = UserDao.getTransactionHistory(accountNo);
                            if (format.equals("PDF")) {
                                file = FileGenerator.generateTransactionPDF(transactions, accountNo);
                            } else if (format.equals("CSV")) {
                                file = FileGenerator.generateTransactionCSV(transactions, accountNo);
                            } else {
                                System.out.println("Invalid format. Please choose PDF or CSV.");
                                break;
                            }

                            // Send email with attachment
                            EmailSender.sendTransactionHistoryEmail(userEmail, cardNumber, file, format);
                            System.out.println("Transaction history sent to " + userEmail);

                            // Clean up file
                            file.delete();
                        } catch (Exception e) {
                            System.err.println("Error sending transaction history: " + e.getMessage());
                        }
                        break;
					}
					
					case 8 :
					{
						System.out.println("Enter Account No : ");
						long accountNo = sc.nextLong();

						System.out.println("Enter the Amount : ");
						Double amount = sc.nextDouble();
						
						System.out.println("Enter the Tenure : ");
						int durationMonths = sc.nextInt();
						
						UserDao.createFixedDeposit(accountNo, amount, durationMonths);
						break;
					}
					
					case 9 : 
					{
						System.out.println("Enter Account No : ");
						long accountNo = sc.nextLong();
						
						System.out.println(UserDao.getUserFDs(accountNo));
						
						break;
					}
					
					case 10 : 
					{
						System.out.println("Enter Account No : ");
						long accountNo = sc.nextLong();
						System.out.println(UserDao.profileInfo(accountNo));
						
						break;
					}
					
					default:
					{
						System.out.println("Invalid Inputs !!!");
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
