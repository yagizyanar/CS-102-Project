package com.edutrack.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.edutrack.dao.GoalDAO;
import com.edutrack.dao.TaskDAO;
import com.edutrack.model.Task;
import com.edutrack.model.User;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.TilePane;

public class DashboardController {

    // XP strip
    @FXML
    private Label lblXpGreeting;
    @FXML
    private Label lblLevelRange;
    @FXML
    private Slider lvlSlider;
    @FXML
    private Label lblXp;

    // Content
    @FXML
    private TilePane coursesTile;
    @FXML
    private BarChart<String, Number> progressChart;
    @FXML
    private ListView<String> todoList;

    // Bottom streak
    @FXML
    private Label lblStreak;

    private User user;
    private final TaskDAO taskDAO = new TaskDAO();
    private final GoalDAO goalDAO = new GoalDAO();

    private static final int XP_PER_LEVEL = 100;

    @FXML

    public void initialize() {
        user = com.edutrack.util.SessionManager.getCurrentUser();
        if (user == null)
            return;
        lvlSlider.setDisable(true);

        refreshAll();
        seedChart();
        seedTodo();
    }

    private void refreshAll() {
        // Greeting
        lblXpGreeting.setText("Hello, " + user.getUsername());

        // XP
        int xp = user.getXpAmount();
        lblXp.setText("Current: " + xp + " XP");

        int level = (xp / XP_PER_LEVEL) + 1;
        lblLevelRange.setText("LVL " + level + " → LVL " + (level + 1));
        lvlSlider.setValue((xp % XP_PER_LEVEL) * 100.0 / XP_PER_LEVEL);

        // 🔥 SAME streak as bar
        int streak = user.calculateStreak();
        lblStreak.setText(String.valueOf(streak));
    }

    private void seedChart() {
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.getData().add(new XYChart.Data<>("Mon", 2));
        s.getData().add(new XYChart.Data<>("Tue", 4));
        s.getData().add(new XYChart.Data<>("Wed", 3));
        s.getData().add(new XYChart.Data<>("Thu", 5));
        s.getData().add(new XYChart.Data<>("Fri", 1));

        progressChart.getData().clear();
        progressChart.getData().add(s);
    }

    private void seedTodo() {
        todoList.getItems().clear();

        int userId = user.getId();

        // Load tasks from database - show non-completed, non-overdue
        List<Task> tasks = taskDAO.getTasksByUserId(userId);
        for (Task t : tasks) {
            if (!"COMPLETED".equals(t.getStatus()) && !isOverdue(t.getDueDate())) {
                todoList.getItems()
                        .add("📋 " + t.getTitle() + (t.getDueDate() != null ? " (Due: " + t.getDueDate() + ")" : ""));
            }
        }

        // Load goals from database - show non-completed, non-overdue
        List<GoalDAO.GoalRecord> goals = goalDAO.getGoalsByUserId(userId);
        for (GoalDAO.GoalRecord g : goals) {
            if (!g.completed && !isOverdue(g.deadline)) {
                todoList.getItems().add("🎯 " + g.name + (g.deadline != null ? " (Due: " + g.deadline + ")" : ""));
            }
        }

        // If no items, show a placeholder
        if (todoList.getItems().isEmpty()) {
            todoList.getItems().add("No upcoming tasks or goals!");
        }
    }

    private boolean isOverdue(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return false;
        try {
            // Try MM/dd/yyyy format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate dueDate = LocalDate.parse(dateStr, formatter);
            return dueDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            try {
                // Try yyyy-MM-dd format
                LocalDate dueDate = LocalDate.parse(dateStr);
                return dueDate.isBefore(LocalDate.now());
            } catch (DateTimeParseException e2) {
                return false;
            }
        }
    }
}
