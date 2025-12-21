
package project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.io.IOException;
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
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button resetButton;

    private static Timeline timeline;
    private static int remainingSeconds;
    private static boolean isStudyPhase = true;
    private static boolean isRunning = false;
    private static int tempStudyValue = 25;
    private static int tempBreakValue = 5;
    private static boolean savedPressed = false;

    private void switchPage(ActionEvent e, String fxml) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/project/" + fxml));
            Stage stage = (Stage) ((Node) e.getSource())
                    .getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setWidth(width);
            stage.setHeight(height);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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
    }

    @FXML
    private void openSettingsFromStart(ActionEvent e) {
        settingsReturnPage = "PomodoroStart.fxml";
        switchPage(e, "PomodoroSettings.fxml");
    }

    @FXML
    private void goToGroupPomodoro(ActionEvent e) {
        switchPage(e, "PomodoroGroup.fxml");
    }

    @FXML
    private void goDashboard(ActionEvent e) {
        switchPage(e, "Dashboard.fxml");
    }

    @FXML
    private void openSettingsFromGroup(ActionEvent e) {
        settingsReturnPage = "PomodoroGroup.fxml";
        switchPage(e, "PomodoroSettings.fxml");
    }

    @FXML
    private void goToMembers(ActionEvent e) {
        switchPage(e, "PomodoroMembers.fxml");
    }

    @FXML
    private void backToStartPomodoro(ActionEvent e) {
        switchPage(e, "PomodoroStart.fxml");
    }

    @FXML
    private void backToGroupPomodoro(ActionEvent e) {
        switchPage(e, "PomodoroGroup.fxml");
    }

    @FXML
    private void exitSettings(ActionEvent e) {
        studyTimeValue = tempStudyValue;
        breakTimeValue = tempBreakValue;

        if (settingsReturnPage != null) {
            switchPage(e, settingsReturnPage);
        }
    }

    @FXML
    private void startTimer(ActionEvent e) {
        if (isRunning) {
            return;
        }

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
    }

}
