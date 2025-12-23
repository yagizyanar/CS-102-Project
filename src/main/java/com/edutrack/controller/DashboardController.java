package com.edutrack.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.edutrack.Main;
import com.edutrack.dao.EventDAO;
import com.edutrack.dao.GoalDAO;
import com.edutrack.dao.TaskDAO;
import com.edutrack.model.Task;
import com.edutrack.model.User;
import com.edutrack.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DashboardController {

    @FXML private Label lblGreeting;
    @FXML private Label lblUsername;
    @FXML private Label lblStreak;
    @FXML private Label lblLevel;
    @FXML private Label lblXpProgress;
    @FXML private ProgressBar xpProgressBar;
    @FXML private Label lblTasksCompleted;
    @FXML private Label lblStudyHours;
    @FXML private Label lblGoalsReached;
    @FXML private ListView<Task> taskListView;
    @FXML private VBox eventsBox;
    @FXML private VBox activityBox;
    @FXML private PieChart taskPieChart;

    private final TaskDAO taskDAO = new TaskDAO();
    private final GoalDAO goalDAO = new GoalDAO();
    private final EventDAO eventDAO = new EventDAO();

    @FXML
    private void initialize() {
        loadUserInfo();
        loadTasks();
        loadEvents();
        loadStatistics();
        loadRecentActivity();
    }

    private void loadUserInfo() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        // Greeting based on time
        LocalTime now = LocalTime.now();
        String greeting;
        if (now.isBefore(LocalTime.NOON)) {
            greeting = "Good Morning!";
        } else if (now.isBefore(LocalTime.of(17, 0))) {
            greeting = "Good Afternoon!";
        } else {
            greeting = "Good Evening!";
        }
        
        if (lblGreeting != null) lblGreeting.setText(greeting);
        if (lblUsername != null) lblUsername.setText("Welcome back, " + user.getUsername());
        if (lblStreak != null) lblStreak.setText(String.valueOf(user.calculateStreak()));
        if (lblLevel != null) lblLevel.setText("Level " + user.getLevel());
        
        int xp = user.getXp();
        int nextLevelXp = user.getNextLevelXp();
        int currentLevelXp = (user.getLevel() - 1) * 100;
        int progressXp = xp - currentLevelXp;
        int neededXp = nextLevelXp - currentLevelXp;
        
        if (lblXpProgress != null) lblXpProgress.setText(progressXp + "/" + neededXp + " XP");
        if (xpProgressBar != null) xpProgressBar.setProgress(neededXp > 0 ? (double) progressXp / neededXp : 0);
    }

    private void loadTasks() {
        User user = SessionManager.getCurrentUser();
        if (user == null || taskListView == null) return;

        List<Task> allTasks = taskDAO.getTasksByUserId(user.getId());
        
        // Filter today's tasks and pending tasks
        ObservableList<Task> todayTasks = FXCollections.observableArrayList();
        LocalDate today = LocalDate.now();
        
        for (Task task : allTasks) {
            if (!"COMPLETED".equals(task.getStatus())) {
                todayTasks.add(task);
                if (todayTasks.size() >= 5) break; // Show max 5 tasks
            }
        }

        taskListView.setItems(todayTasks);
        taskListView.setCellFactory(lv -> new TaskListCell());
        
        // Update pie chart
        if (taskPieChart != null) {
            int completed = 0, pending = 0, overdue = 0;
            for (Task task : allTasks) {
                if ("COMPLETED".equals(task.getStatus())) {
                    completed++;
                } else if (isOverdue(task.getDueDate())) {
                    overdue++;
                } else {
                    pending++;
                }
            }
            
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            if (completed > 0) pieData.add(new PieChart.Data("Completed (" + completed + ")", completed));
            if (pending > 0) pieData.add(new PieChart.Data("Pending (" + pending + ")", pending));
            if (overdue > 0) pieData.add(new PieChart.Data("Overdue (" + overdue + ")", overdue));
            
            if (pieData.isEmpty()) {
                pieData.add(new PieChart.Data("No Tasks", 1));
            }
            
            taskPieChart.setData(pieData);
            taskPieChart.setLabelsVisible(false);
        }
    }

    private void loadEvents() {
        User user = SessionManager.getCurrentUser();
        if (user == null || eventsBox == null) return;

        eventsBox.getChildren().clear();
        
        List<EventDAO.EventRecord> events = eventDAO.getEventsByUserId(user.getId());
        LocalDate today = LocalDate.now();
        int count = 0;
        
        for (EventDAO.EventRecord event : events) {
            if (!isOverdue(event.eventDate) && count < 3) {
                eventsBox.getChildren().add(createEventRow(event));
                count++;
            }
        }
        
        if (count == 0) {
            Label noEvents = new Label("No upcoming events");
            noEvents.setStyle("-fx-text-fill: #888; -fx-font-size: 13;");
            eventsBox.getChildren().add(noEvents);
        }
    }

    private HBox createEventRow(EventDAO.EventRecord event) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10;");

        Circle dot = new Circle(5);
        dot.setFill(Color.web("#59B5E0"));

        VBox info = new VBox(2);
        Label name = new Label(event.name);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        Label date = new Label(event.eventDate != null ? event.eventDate : "No date");
        date.setStyle("-fx-text-fill: #888; -fx-font-size: 11;");
        info.getChildren().addAll(name, date);

        row.getChildren().addAll(dot, info);
        return row;
    }

    private void loadStatistics() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        List<Task> tasks = taskDAO.getTasksByUserId(user.getId());
        List<GoalDAO.GoalRecord> goals = goalDAO.getGoalsByUserId(user.getId());

        int completedTasks = 0;
        for (Task t : tasks) {
            if ("COMPLETED".equals(t.getStatus())) completedTasks++;
        }

        int completedGoals = 0;
        for (GoalDAO.GoalRecord g : goals) {
            if (g.completed) completedGoals++;
        }

        if (lblTasksCompleted != null) lblTasksCompleted.setText(String.valueOf(completedTasks));
        if (lblGoalsReached != null) lblGoalsReached.setText(String.valueOf(completedGoals));
        if (lblStudyHours != null) lblStudyHours.setText("0h"); // TODO: Track actual study time from Pomodoro
    }

    private void loadRecentActivity() {
        if (activityBox == null) return;
        activityBox.getChildren().clear();

        User user = SessionManager.getCurrentUser();
        if (user == null) {
            Label noActivity = new Label("No recent activity");
            noActivity.setStyle("-fx-text-fill: #888; -fx-font-size: 13;");
            activityBox.getChildren().add(noActivity);
            return;
        }

        // Get recent completed tasks
        List<Task> tasks = taskDAO.getTasksByUserId(user.getId());
        int count = 0;
        
        for (Task task : tasks) {
            if ("COMPLETED".equals(task.getStatus()) && count < 3) {
                activityBox.getChildren().add(createActivityRow("✓ Completed: " + task.getTitle(), "Task"));
                count++;
            }
        }
        
        if (count == 0) {
            Label noActivity = new Label("No recent activity");
            noActivity.setStyle("-fx-text-fill: #888; -fx-font-size: 13;");
            activityBox.getChildren().add(noActivity);
        }
    }

    private HBox createActivityRow(String text, String type) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8));
        row.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 8;");

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label typeLabel = new Label(type);
        typeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #888;");

        row.getChildren().addAll(label, spacer, typeLabel);
        return row;
    }

    private boolean isOverdue(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return false;
        try {
            String[] formats = {"MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "M/d/yyyy"};
            for (String fmt : formats) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt);
                    LocalDate dueDate = LocalDate.parse(dateStr, formatter);
                    return dueDate.isBefore(LocalDate.now());
                } catch (Exception e) {}
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void startPomodoro() {
        try {
            Main.setContent("PomodoroStart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToTasks() {
        try {
            Main.setContent("TaskManagement");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToForum() {
        try {
            Main.setContent("Forum");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Custom cell for task list
    private static class TaskListCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);
            if (empty || task == null) {
                setGraphic(null);
                setText(null);
            } else {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8));

                Circle statusDot = new Circle(5);
                if ("COMPLETED".equals(task.getStatus())) {
                    statusDot.setFill(Color.web("#28a745"));
                } else if ("IN_PROGRESS".equals(task.getStatus())) {
                    statusDot.setFill(Color.web("#ffc107"));
                } else {
                    statusDot.setFill(Color.web("#6c757d"));
                }

                VBox info = new VBox(2);
                Label title = new Label(task.getTitle());
                title.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
                Label due = new Label(task.getDueDate() != null ? "Due: " + task.getDueDate() : "No due date");
                due.setStyle("-fx-text-fill: #888; -fx-font-size: 10;");
                info.getChildren().addAll(title, due);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                /*Label priority = new Label(task.getPriority() != null ? task.getPriority() : "");
                String priorityColor = "#888";
                if ("HIGH".equals(task.getPriority())) priorityColor = "#dc3545";
                else if ("MEDIUM".equals(task.getPriority())) priorityColor = "#ffc107";
                else if ("LOW".equals(task.getPriority())) priorityColor = "#28a745";
                priority.setStyle("-fx-font-size: 10; -fx-text-fill: " + priorityColor + ";");

                row.getChildren().addAll(statusDot, info, spacer, priority);
                setGraphic(row);*/
            }
        }
    }
}
