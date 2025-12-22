package com.edutrack.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    private static Properties mailProps = new Properties();

    static {
        try (InputStream input = EmailService.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (input != null) {
                mailProps.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendWelcomeEmail(String recipientEmail, String username) {
        String host = mailProps.getProperty("mail.smtp.host");
        final String user = mailProps.getProperty("mail.username");
        final String password = mailProps.getProperty("mail.password");

        if (host == null || "smtp.example.com".equals(host)) {
            System.out.println("Email Service: SMTP not configured. Skipping email to " + recipientEmail);
            return;
        }

        Session session = Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Welcome to EduTrack!");
            message.setText("Hello " + username + ",\n\n"
                    + "Welcome to EduTrack! We are excited to have you on board.\n"
                    + "Start tracking your study sessions and stay productive!\n\n"
                    + "Best Regards,\nEduTrack Team");

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}
