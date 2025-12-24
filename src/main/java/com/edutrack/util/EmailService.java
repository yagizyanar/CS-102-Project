package com.edutrack.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
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
    private static final String LOG_FILE = "email_debug.log";

    static {
        log("Initializing EmailService...");
        try (InputStream input = EmailService.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (input != null) {
                mailProps.load(input);
                log("mail.properties loaded successfully.");
            } else {
                log("ERROR: mail.properties not found in classpath resources!");
            }
        } catch (IOException e) {
            log("ERROR: Exception loading mail.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendWelcomeEmail(String recipientEmail, String username) {
        log("Preparing to send welcome email to: " + recipientEmail);
        if (!isConfigured()) {
            String msg = "Email Service: SMTP not configured (host NOT found). Skipping welcome email to "
                    + recipientEmail;
            log(msg);
            System.out.println(msg);
            return;
        }

        sendEmail(recipientEmail, "Welcome to EduTrack!", "Hello " + username + ",\n\n"
                + "Welcome to EduTrack! We are excited to have you on board.\n"
                + "Start tracking your study sessions and stay productive!\n\n"
                + "Best Regards,\nEduTrack Team");
    }

    public static void sendRecoveryEmail(String recipientEmail, String code) {
        log("Preparing to send recovery email to: " + recipientEmail);
        if (!isConfigured()) {
            String msg = "Email Service: SMTP not configured. Skipping recovery email to " + recipientEmail;
            log(msg);
            System.out.println(msg);
            return;
        }

        sendEmail(recipientEmail, "Password Recovery Code", "Hello,\n\n"
                + "You requested a password reset. Your recovery code is:\n\n"
                + code + "\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Best Regards,\nEduTrack Team");
    }

    public static void sendPasswordReminder(String recipientEmail, String username, String password) {
        log("Preparing to send password reminder to: " + recipientEmail);
        if (!isConfigured()) {
            String msg = "Email Service: SMTP not configured. Skipping password reminder to " + recipientEmail;
            log(msg);
            System.out.println(msg);
            return;
        }

        sendEmail(recipientEmail, "Password Reminder", "Hello " + username + ",\n\n"
                + "You requested your password. Here are your login details:\n\n"
                + "Username: " + username + "\n"
                + "Password: " + password + "\n\n"
                + "Please keep this information safe.\n\n"
                + "Best Regards,\nEduTrack Team");
    }

    private static boolean isConfigured() {
        String host = mailProps.getProperty("mail.smtp.host");
        boolean configured = host != null && !"smtp.example.com".equals(host);
        if (!configured) {
            log("isConfigured check failed. Host value: " + host);
        }
        return configured;
    }

    private static void sendEmail(String recipientEmail, String subject, String body) {
        final String user = mailProps.getProperty("mail.username");
        final String password = mailProps.getProperty("mail.password");

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
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            log("Email sent successfully to " + recipientEmail);
            System.out.println("Email sent successfully to " + recipientEmail);

        } catch (MessagingException e) {
            log("Failed to send email to " + recipientEmail + ". Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }

    private static void log(String message) {
        // Appending to file
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(LocalDateTime.now() + ": " + message);
        } catch (IOException e) {
            // If logging fails, fallback to console
            System.err.println("Failed to write to log file: " + e.getMessage());
            System.out.println("LOG (fallback): " + message);
        }
    }
}
