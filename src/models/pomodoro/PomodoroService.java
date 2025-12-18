package models.pomodoro;

import dao.PomodoroDAO;
import java.time.Duration;
import models.pomodoro.PomodoroSession;
import models.pomodorointerface.TimerListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import pomodoroInterface.TimerListener;
import javafx.util.Duration;

import java.time.LocalDateTime;

public class PomodoroService {

    private final PomodoroDAO pomodoroDAO;
    private Timeline timeline;
    private int remainingSeconds;
    private boolean isRunning;
    private boolean isPaused;

    private TimerListener listener;

    private int currentUserId;
    private Integer currentGroupId;
    private LocalDateTime sessionStartTime;
    private int sessionDurationMinutes;

    public PomodoroService() {
        this.pomodoroDAO = new PomodoroDAO();
        this.isRunning = false;
        this.isPaused = false;
        setupTimeline();
    }

    public void setTimerListener(TimerListener listener) {
        this.listener = listener;
    }

    private void setupTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            handleTick();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void handleTick() {
        if (remainingSeconds > 0) {
            remainingSeconds--;
            if (listener != null) {
                listener.onTick(formatTime(remainingSeconds));
            }
        } else {
            completeSession();
        }
    }

    public void startSession(int userId, Integer groupId, int minutes) {
        if (isRunning) return;

        this.currentUserId = userId;
        this.currentGroupId = groupId;
        this.sessionDurationMinutes = minutes;
        this.remainingSeconds = minutes * 60;
        this.sessionStartTime = LocalDateTime.now();

        this.isRunning = true;
        this.isPaused = false;

        if (listener != null) {
            listener.onTick(formatTime(remainingSeconds));
        }

        timeline.play(); 
    }

    public void pauseSession() {
        if (isRunning && !isPaused) {
            isPaused = true;
            timeline.pause(); 
        }
    }

    public void resumeSession() {
        if (isRunning && isPaused) {
            isPaused = false;
            timeline.play();
        }
    }

    public void stopSession() {
        timeline.stop(); 
        isRunning = false;
        isPaused = false;
        remainingSeconds = 0;
        
        if (listener != null) {
            listener.onTick("00:00");
        }
    }

    private void completeSession() {
        stopSession();

        if (listener != null) {
            listener.onFinish();
        }

        saveToDatabase();
    }

    private void saveToDatabase() {
        PomodoroSession session = new PomodoroSession();
        session.setUserId(currentUserId);
        session.setGroupId(currentGroupId);
        session.setSessionType(currentGroupId != null ? 
            PomodoroSession.SessionType.GROUP : 
            PomodoroSession.SessionType.INDIVIDUAL);

        session.setStartTime(sessionStartTime);
        session.setEndTime(LocalDateTime.now());
        session.setDurationMinutes(sessionDurationMinutes);

        boolean success = pomodoroDAO.saveSession(session);
        if (success) {
            System.out.println("Session successfully saved to database.");
        } else {
            System.err.println("Failed to save session.");
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}