package com.edutrack.controller;

import java.io.IOException;

import com.edutrack.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class BarController {

    @FXML
    private void hoverOn(MouseEvent e) {
        Node n = (Node) e.getSource();
        n.setScaleX(1.08);
        n.setScaleY(1.08);
        n.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.35)));
    }

    @FXML
    private void hoverOff(MouseEvent e) {
        Node n = (Node) e.getSource();
        n.setScaleX(1.0);
        n.setScaleY(1.0);
        n.setEffect(null);
    }

    @FXML
    private void goDashboard() throws IOException {
        Main.setContent("Dashboard");
    }

    @FXML
    private void goForums() throws IOException {
        Main.setContent("Forum");
    }

    @FXML
    private void goTasks() throws IOException {
        Main.setContent("TaskManagement");
    }

    @FXML
    private void goFriends() throws IOException {
        Main.setContent("Friends");
    }

    @FXML
    private void goPomodoro() throws IOException {
        Main.setContent("PomodoroStart");
    }

    @FXML
    private void goProfile() throws IOException {
        Main.setContent("profile");
    }

    @FXML
    private void goNotifications() throws IOException {

        showNotificationPopup();
    }
    
    private void showNotificationPopup() {
        try {
            javafx.scene.Scene scene = javafx.stage.Stage.getWindows().stream()
                    .filter(w -> w instanceof javafx.stage.Stage)
                    .map(w -> ((javafx.stage.Stage) w).getScene())
                    .filter(s -> s != null)
                    .findFirst()
                    .orElse(null);
            
            if (scene == null) {
                Main.setContent("notification");
                return;
            }
            
            Parent root = scene.getRoot();

            Node existing = root.lookup("#notificationPopup");
            if (existing != null && existing.getParent() instanceof Pane) {
                ((Pane) existing.getParent()).getChildren().remove(existing);
                return;
            }
  
            BorderPane borderPane = null;
            if (root instanceof BorderPane) {
                borderPane = (BorderPane) root;
            } else {
            
                Main.setContent("notification");
                return;
            }
            
            Node centerContent = borderPane.getCenter();
            StackPane centerWrapper = null;
            
            if (centerContent instanceof StackPane) {
                centerWrapper = (StackPane) centerContent;
            } else if (centerContent != null) {

                centerWrapper = new StackPane(centerContent);
                borderPane.setCenter(centerWrapper);
            } else {

                Main.setContent("notification");
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/edutrack/view/notificationPopup.fxml"));
            Parent popup = loader.load();
            popup.setId("notificationPopup");

            NotificationController controller = loader.getController();
            controller.setPopupMode(true);

            StackPane.setAlignment(popup, Pos.TOP_RIGHT);
            StackPane.setMargin(popup, new Insets(20, 20, 0, 0));
 
            centerWrapper.getChildren().add(popup);

            popup.toFront();
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Main.setContent("notification");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}