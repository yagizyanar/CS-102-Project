package com.edutrack.controller;

import com.edutrack.Main;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.io.IOException;

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
        Main.setContent("notification");
    }
}
