package com.atm.emailer;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    private static final String FROM_EMAIL = "mb.finserv3103@gmail.com";
    private static final String PASSWORD = "nbpevafzpqwwyloy";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    
    public static void sendDepositEmail(String toEmail, String customerName, String accountNumber,
                                        double amount, String transactionId, double newBalance, String dateTime) throws MessagingException {
        String subject = "Deposit Successful - Your Account Has Been Credited";
        String body = String.format(
        	    "Dear %s,\n\n" +
        	    "We are pleased to inform you that a deposit has been successfully made to your account %s on %s.\n\n" +
        	    "Details of the transaction:\n" +
        	    "- Deposit Amount: ₹%.2f\n" +
        	    "- Transaction ID: %s\n" +
        	    "- Updated Balance: ₹%.2f\n\n" +
        	    "Thank you for banking with MB Bank. If you did not authorize this transaction, please contact our customer support immediately.\n\n" +
        	    "Best regards,\n" +
        	    "MB Bank Team\n" +
        	    "Contact us: support@mbbank.com | +91-XXXXXXX807",
                customerName, accountNumber, dateTime, amount, transactionId, newBalance);

        sendEmail(toEmail, subject, body);
    }

    public static void sendWithdrawalEmail(String toEmail, String customerName, String accountNumber,
                                           double amount, String transactionId, double newBalance, String dateTime) throws MessagingException {
        String subject = "Withdrawal Successful - Your Account Has Been Debited";
        String body = String.format(
        	    "Dear %s,\n\n" +
        	    "This is to confirm that a withdrawal has been successfully processed from your account %s on %s.\n\n" +
        	    "Details of the transaction:\n" +
        	    "- Withdrawal Amount: ₹%.2f\n" +
        	    "- Transaction ID: %s\n" +
        	    "- Updated Balance: ₹%.2f\n\n" +
        	    "If you did not authorize this transaction, please contact our customer support immediately to report the issue.\n\n" +
        	    "Thank you for choosing MB Bank.\n\n" +
        	    "Best regards,\n" +
        	    "MB Bank Team\n" +
        	    "Contact us: support@mbbank.com | +91-XXXXXXX807",
        	    customerName, accountNumber, dateTime, amount, transactionId, newBalance);

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

    // Example usage:
    public static void main(String[] args) {
        try {
            sendDepositEmail("customer@example.com", "John Doe", "1234567890",
                    5000.00, "TXN123456", 15000.00, "2025-05-16 10:30 AM");

            sendWithdrawalEmail("customer@example.com", "John Doe", "1234567890",
                    2000.00, "TXN123457", 13000.00, "2025-05-16 11:00 AM");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
