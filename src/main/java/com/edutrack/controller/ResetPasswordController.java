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

        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            setStatus("Email not found.");
            return;
        }

        // Send Email (Async)
        new Thread(() -> {
            EmailService.sendPasswordReminder(email, user.getUsername(), user.getPassword());
        }).start();

        setStatus("Password sent to your email! Please check inbox.");

        // No need to navigate to code entry screen logic.
        // Optionally, could redirect to login after a delay, but let's stay here so
        // they read the message.
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
