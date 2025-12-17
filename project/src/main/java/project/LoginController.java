package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        switchScene(event, "mainLogin.fxml");
    }

    @FXML
    private void goToSignUp(ActionEvent event) throws IOException {
        switchScene(event, "signup.fxml");
    }

    @FXML
    private void goToReset(ActionEvent event) throws IOException {
        switchScene(event, "reset.fxml");
    }

    @FXML
    private void goToReset2(ActionEvent event) throws IOException {
        switchScene(event, "reset2.fxml");
    }

    private void switchScene(ActionEvent event, String fxml)
            throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource(fxml)
        );

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();

        stage.setScene(new Scene(root));
    }
}
