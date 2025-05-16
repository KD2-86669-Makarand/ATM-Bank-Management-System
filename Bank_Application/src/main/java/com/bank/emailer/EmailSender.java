package com.bank.emailer;


import javax.mail.*;
import javax.mail.internet.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EmailSender {

    private static final String FROM_EMAIL = "mb.finserv3103@gmail.com";
    private static final String PASSWORD = "nbpevafzpqwwyloy";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    
    public static void sendAccountCreationEmail(String toEmail, String customerName, String accountNumber, double initialBalance, String dateTime) throws MessagingException {
        String subject = "Welcome to MB Cooperative Bank Pvt. Ltd. - Account Created Successfully";
        String body = String.format(
            "Dear %s,\n\n" +
            "Congratulations! Your account with MB Cooperative Bank Pvt. Ltd. has been successfully created on %s.\n\n" +
            "Account Details:\n" +
            "- Account Number: %s\n" +
            "- Initial Deposit: ₹%.2f\n\n" +
            "Thank you for choosing MB Cooperative Bank. We look forward to serving your banking needs.\n\n" +
            "If you have any questions or need assistance, please contact our support team.\n\n" +
            "Best regards,\n" +
            "MB Cooperative Bank Team\n" +
            "Contact us: support@mbbank.com | +91-XXXXXXXXXX",
            customerName, dateTime, accountNumber, initialBalance);

        sendEmail(toEmail, subject, body);
    }
    
    public static void sendDepositEmail(String toEmail, String accountNumber,
            double amount, double newBalance) throws MessagingException 
    {
		String subject = "Amount Credited to Your Account";
		
		String last4Digits = accountNumber.length() > 4 
		? accountNumber.substring(accountNumber.length() - 4) 
		: accountNumber;
		
		String body = String.format(
		"Rs. %.2f credited to your A/c xxxx%s\n" +
		"Total Bal: Rs. %.2f\n\n" +
		"- MB Cooperative Bank Pvt. Ltd.",
		amount, last4Digits, newBalance);
		
		sendEmail(toEmail, subject, body);
    }

    public static void sendWithdrawalEmail(String toEmail, String accountNumber,
            double amount, double newBalance) throws MessagingException 
    {
		String subject = "Amount Debited from Your Account";
		
		String last4Digits = accountNumber.length() > 4 
		? accountNumber.substring(accountNumber.length() - 4) 
		: accountNumber;
		
		String body = String.format(
		"Rs. %.2f debited from your A/c xxxx%s\n" +
		"Total Bal: Rs. %.2f\n\n" +
		"- MB Cooperative Bank Pvt. Ltd.",
		amount, last4Digits, newBalance);
		
		sendEmail(toEmail, subject, body);
    }

    private static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
        System.out.println("Email sent successfully to " + toEmail);
    }
    
    public static void sendTransactionHistoryEmail(String toEmail, String accountNumber, File attachment, String format) throws MessagingException, IOException {
        String subject = "Transaction History for Account xxxx" + accountNumber.substring(accountNumber.length() - 4);
        String body = String.format(
            "Dear Customer,\n\n" +
            "Please find attached your transaction history for account ending in xxxx%s in %s format.\n\n" +
            "Thank you for banking with MB Cooperative Bank Pvt. Ltd.\n\n" +
            "Best regards,\n" +
            "MB Cooperative Bank Team",
            accountNumber.substring(accountNumber.length() - 4), format.toUpperCase());

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);

        // Create the attachment part
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachment);

        // Create a multipart message
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentPart);

        // Set the multipart message to the email
        message.setContent(multipart);

        Transport.send(message);
        System.out.println("Transaction history email sent successfully to " + toEmail);
    }
}
    
