package com.edutrack.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.edutrack.Main;
import com.edutrack.dao.UserDAO;
import com.edutrack.model.User;
import com.edutrack.util.EmailService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ResetPasswordController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField codeField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;

    // Temporary storage for recovery codes (In a real app, store in DB with expiry)
    private static final Map<String, String> recoveryCodes = new HashMap<>();
    private final UserDAO userDAO = new UserDAO();

    // Step 1: Send Code (reset.fxml)
    // Step 1: Send Password Reminder (reset.fxml)
    @FXML
    private void handleSendCode() {
        String email = emailField.getText();

        if (email.isEmpty()) {
            setStatus("Please enter your email.");
            return;
        }

        // Email format validation
        if (!isValidEmail(email)) {
            setStatus("Please enter a valid email address.");
            return;
        }

        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            setStatus("Email not found.");
            return;
        }

        // Send Email (Async)
        new Thread(() -> {
            EmailService.sendPasswordReminder(email, user.getUsername(), user.getPassword());
        }).start();

        setStatus("Password sent to your email!");
        
        // Redirect to login after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    try {
                        Main.setRoot("mainLogin");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    // Step 2: Reset Password (reset2.fxml)
    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        String code = codeField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (email.isEmpty() || code.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            setStatus("All fields are required.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            setStatus("Passwords do not match.");
            return;
        }

        // Verify Code
        String validCode = recoveryCodes.get(email);
        if (validCode == null || !validCode.equals(code)) {
            setStatus("Invalid or expired code.");
            return;
        }

        // Update Password
        if (userDAO.updatePassword(email, newPass)) {
            setStatus("Password reset successful! Please login.");
            recoveryCodes.remove(email); // Invalidate code
            try {
                Main.setRoot("mainLogin");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setStatus("Failed to update password. Password might be taken.");
        }
    }

    @FXML
    private void goToLogin() throws IOException {
        Main.setRoot("mainLogin");
    }

    private void setStatus(String msg) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
        }
    }
}
