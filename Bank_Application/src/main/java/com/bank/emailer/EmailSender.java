//package com.bank.emailer;
//
//
//import javax.mail.*;
//import javax.mail.internet.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Properties;
//
//public class EmailSender {
//
//    private static final String FROM_EMAIL = "mb.finserv3103@gmail.com";
//    private static final String PASSWORD = "nbpevafzpqwwyloy";
//    private static final String SMTP_HOST = "smtp.gmail.com";
//    private static final int SMTP_PORT = 587;
//    
//    public static void sendAccountCreationEmail(String toEmail, String customerName, String accountNumber, double initialBalance, String dateTime) throws MessagingException {
//        String subject = "Welcome to MB Cooperative Bank Pvt. Ltd. - Account Created Successfully";
//        String body = String.format(
//            "Dear %s,\n\n" +
//            "Congratulations! Your account with MB Cooperative Bank Pvt. Ltd. has been successfully created on %s.\n\n" +
//            "Account Details:\n" +
//            "- Account Number: %s\n" +
//            "- Initial Deposit: ₹%.2f\n\n" +
//            "Thank you for choosing MB Cooperative Bank. We look forward to serving your banking needs.\n\n" +
//            "If you have any questions or need assistance, please contact our support team.\n\n" +
//            "Best regards,\n" +
//            "MB Cooperative Bank Team\n" +
//            "Contact us: support@mbbank.com | +91-XXXXXXXXXX",
//            customerName, dateTime, accountNumber, initialBalance);
//
//        sendEmail(toEmail, subject, body);
//    }
//    
//    public static void sendDepositEmail(String toEmail, String accountNumber,
//            double amount, double newBalance) throws MessagingException 
//    {
//		String subject = "Amount Credited to Your Account";
//		
//		String last4Digits = accountNumber.length() > 4 
//		? accountNumber.substring(accountNumber.length() - 4) 
//		: accountNumber;
//		
//		String body = String.format(
//		"Rs. %.2f credited to your A/c xxxx%s\n" +
//		"Total Bal: Rs. %.2f\n\n" +
//		"- MB Cooperative Bank Pvt. Ltd.",
//		amount, last4Digits, newBalance);
//		
//		sendEmail(toEmail, subject, body);
//    }
//
//    public static void sendWithdrawalEmail(String toEmail, String accountNumber,
//            double amount, double newBalance) throws MessagingException 
//    {
//		String subject = "Amount Debited from Your Account";
//		
//		String last4Digits = accountNumber.length() > 4 
//		? accountNumber.substring(accountNumber.length() - 4) 
//		: accountNumber;
//		
//		String body = String.format(
//		"Rs. %.2f debited from your A/c xxxx%s\n" +
//		"Total Bal: Rs. %.2f\n\n" +
//		"- MB Cooperative Bank Pvt. Ltd.",
//		amount, last4Digits, newBalance);
//		
//		sendEmail(toEmail, subject, body);
//    }
//
//    private static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", SMTP_HOST);
//        props.put("mail.smtp.port", SMTP_PORT);
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
//            }
//        });
//
//        Message message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(FROM_EMAIL));
//        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//        message.setSubject(subject);
//        message.setText(body);
//
//        Transport.send(message);
//        System.out.println("Email sent successfully to " + toEmail);
//    }
//    
//    public static void sendTransactionHistoryEmail(String toEmail, String accountNumber, File attachment, String format) throws MessagingException, IOException {
//        String subject = "Transaction History for Account xxxx" + accountNumber.substring(accountNumber.length() - 4);
//        String body = String.format(
//            "Dear Customer,\n\n" +
//            "Please find attached your transaction history for account ending in xxxx%s in %s format.\n\n" +
//            "Thank you for banking with MB Cooperative Bank Pvt. Ltd.\n\n" +
//            "Best regards,\n" +
//            "MB Cooperative Bank Team",
//            accountNumber.substring(accountNumber.length() - 4), format.toUpperCase());
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", SMTP_HOST);
//        props.put("mail.smtp.port", SMTP_PORT);
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
//            }
//        });
//
//        Message message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(FROM_EMAIL));
//        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//        message.setSubject(subject);
//
//        // Create the message part
//        BodyPart messageBodyPart = new MimeBodyPart();
//        messageBodyPart.setText(body);
//
//        // Create the attachment part
//        MimeBodyPart attachmentPart = new MimeBodyPart();
//        attachmentPart.attachFile(attachment);
//
//        // Create a multipart message
//        Multipart multipart = new MimeMultipart();
//        multipart.addBodyPart(messageBodyPart);
//        multipart.addBodyPart(attachmentPart);
//
//        // Set the multipart message to the email
//        message.setContent(multipart);
//
//        Transport.send(message);
//        System.out.println("Transaction history email sent successfully to " + toEmail);
//    }
//}
//    


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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
        
        String htmlBody = String.format(
            "<html><body style='font-family: Arial, sans-serif; color: #333333;'>" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #dddddd; border-radius: 5px;'>" +
            "<div style='text-align: center; margin-bottom: 20px;'>" +
            "<h1 style='color: #2c5282;'>Welcome to MB Cooperative Bank</h1>" +
            "</div>" +
            "<p>Dear <b>%s</b>,</p>" +
            "<p>Congratulations! Your account with MB Cooperative Bank Pvt. Ltd. has been successfully created on <b>%s</b>.</p>" +
            "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
            "<h3 style='margin-top: 0; color: #2c5282;'>Account Details:</h3>" +
            "<p style='margin: 5px 0;'><b>Account Number:</b> %s</p>" +
            "<p style='margin: 5px 0;'><b>Initial Deposit:</b> ₹%.2f</p>" +
            "</div>" +
            "<p>Thank you for choosing MB Cooperative Bank. We look forward to serving your banking needs.</p>" +
            "<p>If you have any questions or need assistance, please contact our support team.</p>" +
            "<div style='margin-top: 30px; padding-top: 20px; border-top: 1px solid #dddddd;'>" +
            "<p style='margin: 5px 0;'><b>Best regards,</b></p>" +
            "<p style='margin: 5px 0;'>MB Cooperative Bank Team</p>" +
            "<p style='margin: 5px 0;'><i>Contact us: support@mbbank.com | +91-XXXXXXXXXX</i></p>" +
            "</div>" +
            "</div></body></html>",
            customerName, dateTime, accountNumber, initialBalance);

        sendHtmlEmail(toEmail, subject, htmlBody);
    }
    
    public static void sendDepositEmail(String toEmail, String accountNumber,
            double amount, double newBalance) throws MessagingException 
    {
        String subject = "Amount Credited to Your Account";
        
        String last4Digits = accountNumber.length() > 4 
        ? accountNumber.substring(accountNumber.length() - 4) 
        : accountNumber;
        
        String htmlBody = String.format(
            "<html><body style='font-family: Arial, sans-serif; color: #333333;'>" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #dddddd; border-radius: 5px;'>" +
            "<div style='text-align: center; margin-bottom: 20px;'>" +
            "<h2 style='color: #2c5282;'>Transaction Alert</h2>" +
            "</div>" +
            "<div style='background-color: #e9f5e9; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
            "<h3 style='margin-top: 0; color: #2e7d32;'>CREDIT ALERT</h3>" +
            "<p style='font-size: 18px; margin: 5px 0;'><b>Rs. %.2f</b> has been credited to your account</p>" +
            "<p style='margin: 5px 0;'>Account: xxxx%s</p>" +
            "<p style='margin: 5px 0;'>Available Balance: <b>Rs. %.2f</b></p>" +
            "</div>" +
            "<div style='margin-top: 30px; text-align: center; font-size: 12px; color: #777777;'>" +
            "<p>- MB Cooperative Bank Pvt. Ltd. -</p>" +
            "</div>" +
            "</div></body></html>",
            amount, last4Digits, newBalance);
        
        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    public static void sendWithdrawalEmail(String toEmail, String accountNumber,
            double amount, double newBalance) throws MessagingException 
    {
        String subject = "Amount Debited from Your Account";
        
        String last4Digits = accountNumber.length() > 4 
        ? accountNumber.substring(accountNumber.length() - 4) 
        : accountNumber;
        
        String htmlBody = String.format(
            "<html><body style='font-family: Arial, sans-serif; color: #333333;'>" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #dddddd; border-radius: 5px;'>" +
            "<div style='text-align: center; margin-bottom: 20px;'>" +
            "<h2 style='color: #2c5282;'>Transaction Alert</h2>" +
            "</div>" +
            "<div style='background-color: #fdf2f3; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
            "<h3 style='margin-top: 0; color: #c62828;'>DEBIT ALERT</h3>" +
            "<p style='font-size: 18px; margin: 5px 0;'><b>Rs. %.2f</b> has been debited from your account</p>" +
            "<p style='margin: 5px 0;'>Account: xxxx%s</p>" +
            "<p style='margin: 5px 0;'>Available Balance: <b>Rs. %.2f</b></p>" +
            "</div>" +
            "<div style='margin-top: 30px; text-align: center; font-size: 12px; color: #777777;'>" +
            "<p>- MB Cooperative Bank Pvt. Ltd. -</p>" +
            "</div>" +
            "</div></body></html>",
            amount, last4Digits, newBalance);
        
        sendHtmlEmail(toEmail, subject, htmlBody);
    }
    
    private static void sendHtmlEmail(String toEmail, String subject, String htmlBody) throws MessagingException {
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
        
        // Set the content as HTML
        message.setContent(htmlBody, "text/html; charset=utf-8");

        Transport.send(message);
        System.out.println("HTML email sent successfully to " + toEmail);
    }
    
    public static void sendTransactionHistoryEmail(String toEmail, String accountNumber, File attachment, String format) throws MessagingException, IOException {
        String subject = "Transaction History for Account xxxx" + accountNumber.substring(accountNumber.length() - 4);
        
        String htmlBody = String.format(
            "<html><body style='font-family: Arial, sans-serif; color: #333333;'>" +
            "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #dddddd; border-radius: 5px;'>" +
            "<div style='text-align: center; margin-bottom: 20px;'>" +
            "<h2 style='color: #2c5282;'>Transaction History</h2>" +
            "</div>" +
            "<p>Dear Customer,</p>" +
            "<p>Please find attached your transaction history for account ending in <b>xxxx%s</b> in %s format.</p>" +
            "<p>Thank you for banking with MB Cooperative Bank Pvt. Ltd.</p>" +
            "<div style='margin-top: 30px; padding-top: 20px; border-top: 1px solid #dddddd;'>" +
            "<p style='margin: 5px 0;'><b>Best regards,</b></p>" +
            "<p style='margin: 5px 0;'>MB Cooperative Bank Team</p>" +
            "</div>" +
            "</div></body></html>",
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

        // Create the HTML body part
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlBody, "text/html; charset=utf-8");

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
        System.out.println("Transaction history email with attachment sent successfully to " + toEmail);
    }
}