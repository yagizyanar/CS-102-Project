package com.edutrack.controller;

import java.io.IOException;

import com.edutrack.Main;
import com.edutrack.dao.UserDAO;
import com.edutrack.model.User;
import com.edutrack.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Enter username and password.");
            return;
        }

        User user = userDAO.loginUser(username, password);
        User user2 = userDAO.loginUserByEmail(username,password);

        if (user != null) {
            SessionManager.setCurrentUser(user);
            System.out.println("Login successful for: " + user.getUsername());
            try {
                // Navigate to the main layout with persistent nav bar
                Main.showMainLayout("Dashboard");
            } catch (IOException e) {
                e.printStackTrace();
                setStatus("Error loading dashboard.");
            }
        }
        else if(user2 != null){
            SessionManager.setCurrentUser(user2);
            System.out.println("Login successful for: " + user2.getUsername());
            try {
                // Navigate to the main layout with persistent nav bar
                Main.showMainLayout("Dashboard");
            } catch (IOException e) {
                e.printStackTrace();
                setStatus("Error loading dashboard.");
            }
        } 
        else {
            setStatus("Invalid credentials.");
        }
    }

    private void setStatus(String msg) {
        if (statusLabel != null)
            statusLabel.setText(msg);
    }

    // Navigation Methods from User's Demo
    @FXML
    private void goToSignUp(ActionEvent event) throws IOException {
        Main.setRoot("signup");
    }

    @FXML
    private void goToReset(ActionEvent event) throws IOException {
        Main.setRoot("reset");
    }
}