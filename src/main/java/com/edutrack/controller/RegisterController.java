package com.edutrack.controller;

import com.edutrack.Main;
import com.edutrack.dao.UserDAO;
import com.edutrack.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Random;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField majorField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();
    private final Random random = new Random();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String major = majorField != null ? majorField.getText() : "";
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("All fields (except Major) are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        // Assign random avatar (avatar1.png to avatar12.png)
        int avatarNum = random.nextInt(12) + 1;
        String avatarPath = "com/edutrack/view/avatar" + avatarNum + ".png";

        User newUser = new User(username, password, email, major);
        newUser.setProfilePicture(avatarPath);

        if (userDAO.registerUser(newUser)) {
            // Send Welcome Email (Async to avoid UI freeze)
            new Thread(() -> {
                com.edutrack.util.EmailService.sendWelcomeEmail(email, username);
            }).start();

            statusLabel.setText("Registration successful! Please login.");
            try {
                Main.setRoot("mainLogin");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Registration failed! Username/Email taken OR Password must be unique.");
        }
    }

    @FXML
    private void goToLogin() throws IOException {
        Main.setRoot("mainLogin");
    }
}
