package com.edutrack.controller;

import java.io.IOException;

import com.edutrack.Main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class PomodoroController {

    private static String settingsReturnPage;
    private static int studyTimeValue = 25;
    private static int breakTimeValue = 5;

    @FXML
    private Slider studyTime;
    @FXML
    private Slider breakTime;
    @FXML
    private Label studyTimeLabel;
    @FXML
    private Label breakTimeLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private Label phaseLabel;
    @FXML
    private Label groupStatusLabel;
    @FXML
    private Label readyCountLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button readyButton;
    @FXML
    private VBox membersListBox;

    private static Timeline timeline;
    private static int remainingSeconds;
    private static boolean isStudyPhase = true;
    private static boolean isRunning = false;
    private static int tempStudyValue = 25;
    private static int tempBreakValue = 5;
    private static boolean savedPressed = false;

    @FXML
    private void initialize() {
        if (timerLabel != null) {
            updateTimerLabel();

            if (isRunning && timeline != null) {
                timeline.stop();
                timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
                    remainingSeconds--;
                    updateTimerLabel();
                    if (remainingSeconds <= 0) {
                        switchPhase();
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
        }

        if (phaseLabel != null) {
            updatePhaseLabel();
        }

        if (studyTime != null) {
            savedPressed = false;
            tempStudyValue = studyTimeValue;
            tempBreakValue = breakTimeValue;

            studyTime.setValue(studyTimeValue);
            studyTimeLabel.setText(String.valueOf(studyTimeValue));

            studyTime.valueProperty().addListener((obs, oldVal, newVal) -> {
                studyTimeValue = newVal.intValue();
                studyTimeLabel.setText(String.valueOf(studyTimeValue));
            });
        }

        if (breakTime != null) {
            breakTime.setValue(breakTimeValue);
            breakTimeLabel.setText(String.valueOf(breakTimeValue));

            breakTime.valueProperty().addListener((obs, oldVal, newVal) -> {
                breakTimeValue = newVal.intValue();
                breakTimeLabel.setText(String.valueOf(breakTimeValue));
            });
        }

        // Update group members list if in group page
        if (membersListBox != null) {
            updateMembersList();
        }

        // Update ready count
        if (readyCountLabel != null) {
            updateReadyCount();
        }
    }

    private void updateMembersList() {
        if (membersListBox == null)
            return;
        membersListBox.getChildren().clear();

        FriendsController.Group group = FriendsController.getCurrentGroup();
        if (group == null)
            return;

        for (FriendsController.User member : group.getMembers()) {
            javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(10);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setPadding(new javafx.geometry.Insets(8));
            row.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8;");

            javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(6);
            dot.setFill(
                    member.isReady() ? javafx.scene.paint.Color.web("#28a745") : javafx.scene.paint.Color.web("#ccc"));

            Label nameLabel = new Label(member.getUsername());
            nameLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #333333;");

            Label statusLabel = new Label(member.isReady() ? "Ready" : "Not Ready");
            statusLabel.setStyle("-fx-font-size: 11; -fx-text-fill: " + (member.isReady() ? "#28a745" : "#999") + ";");

            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            row.getChildren().addAll(dot, nameLabel, spacer, statusLabel);
            membersListBox.getChildren().add(row);
        }
    }

    private void updateReadyCount() {
        FriendsController.Group group = FriendsController.getCurrentGroup();
        if (group == null) {
            if (readyCountLabel != null)
                readyCountLabel.setText("0/0 Ready");
            return;
        }

        int ready = group.getReadyCount();
        int total = group.getMemberCount();
        int needed = (int) Math.ceil(total / 2.0);

        if (readyCountLabel != null) {
            readyCountLabel.setText(ready + "/" + total + " Ready (Need " + needed + ")");
        }

        // Auto-start if half ready
        if (group.isHalfReady() && !isRunning && groupStatusLabel != null) {
            groupStatusLabel.setText("Starting soon...");
            Platform.runLater(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                startGroupTimer();
            });
        }
    }

    @FXML
    private void openSettingsFromStart(ActionEvent e) {
        settingsReturnPage = "PomodoroStart";
        try {
            Main.setContent("PomodoroSettings");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goToGroupPomodoro(ActionEvent e) {
        // Check if user has a group
        FriendsController.Group group = FriendsController.getCurrentGroup();
        if (group == null) {
            showAlert("No Group",
                    "You need to join or create a group first!\nGo to Friends page to create or join a group.");
            return;
        }

        try {
            Main.setContent("PomodoroGroup");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goDashboard(ActionEvent e) {
        try {
            Main.setContent("Dashboard");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openSettingsFromGroup(ActionEvent e) {
        settingsReturnPage = "PomodoroGroup";
        try {
            Main.setContent("PomodoroSettings");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goToMembers(ActionEvent e) {
        try {
            Main.setContent("PomodoroMembers");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void backToStartPomodoro(ActionEvent e) {
        try {
            Main.setContent("PomodoroStart");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void backToGroupPomodoro(ActionEvent e) {
        try {
            Main.setContent("PomodoroGroup");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void exitSettings(ActionEvent e) {
        studyTimeValue = tempStudyValue;
        breakTimeValue = tempBreakValue;

        if (settingsReturnPage != null) {
            try {
                Main.setContent(settingsReturnPage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void toggleReady(ActionEvent e) {
        FriendsController.User currentUser = FriendsController.getCurrentUser();
        if (currentUser == null)
            return;

        currentUser.setReady(!currentUser.isReady());

        if (readyButton != null) {
            readyButton.setText(currentUser.isReady() ? "Not Ready" : "Ready");
            readyButton.setStyle(currentUser.isReady()
                    ? "-fx-background-color: #dc3545; -fx-text-fill: white;"
                    : "-fx-background-color: #28a745; -fx-text-fill: white;");
        }

        updateReadyCount();
        updateMembersList();
    }

    @FXML
    private void startTimer(ActionEvent e) {
        if (isRunning)
            return;

        if (remainingSeconds <= 0) {
            isStudyPhase = true;
            remainingSeconds = studyTimeValue * 60;
            updatePhaseLabel();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                switchPhase();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        isRunning = true;
    }

    private void startGroupTimer() {
        if (isRunning)
            return;

        isStudyPhase = true;
        remainingSeconds = studyTimeValue * 60;
        updatePhaseLabel();

        timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                switchPhase();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        isRunning = true;

        if (groupStatusLabel != null) {
            groupStatusLabel.setText("Session in progress!");
        }
    }

    private void updateTimerLabel() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        if (timerLabel != null) {
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    private void updatePhaseLabel() {
        if (phaseLabel != null) {
            phaseLabel.setText(isStudyPhase ? "Study Time" : "Break Time");
        }
    }

    private void switchPhase() {
        isStudyPhase = !isStudyPhase;
        remainingSeconds = isStudyPhase ? studyTimeValue * 60 : breakTimeValue * 60;
        updatePhaseLabel();
    }

    @FXML
    private void stopTimer(ActionEvent e) {
        if (timeline != null) {
            timeline.stop();
            isRunning = false;
            if (startButton != null) {
                startButton.setText("Start");
            }
        }
    }

    @FXML
    private void resetTimer(ActionEvent e) {
        if (timeline != null) {
            timeline.stop();
        }
        isRunning = false;
        isStudyPhase = true;
        remainingSeconds = studyTimeValue * 60;
        updateTimerLabel();
        updatePhaseLabel();
        if (startButton != null) {
            startButton.setText("Start");
        }
    }

    @FXML
    private void saveAndExitSettings(ActionEvent e) {
        savedPressed = true;
        tempStudyValue = studyTimeValue;
        tempBreakValue = breakTimeValue;

        if (timeline != null) {
            timeline.stop();
        }

        isRunning = false;
        isStudyPhase = true;
        remainingSeconds = studyTimeValue * 60;

        if (settingsReturnPage != null) {
            try {
                Main.setContent(settingsReturnPage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
