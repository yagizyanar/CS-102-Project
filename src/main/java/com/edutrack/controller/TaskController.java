package com.edutrack.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.edutrack.dao.EventDAO;
import com.edutrack.dao.GoalDAO;
import com.edutrack.dao.TaskDAO;
import com.edutrack.model.Task;
import com.edutrack.model.User;
import com.edutrack.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class TaskController {

    @FXML
    private VBox tasksContainer;

    @FXML
    private VBox goalsContainer;

    @FXML
    private VBox eventsContainer;

    private static ArrayList<TaskItem> tasks = new ArrayList<>();
    private static ArrayList<GoalItem> goals = new ArrayList<>();
    private static ArrayList<EventItem> events = new ArrayList<>();

    private final TaskDAO taskDAO = new TaskDAO();
    private final GoalDAO goalDAO = new GoalDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final com.edutrack.dao.UserDAO userDAO = new com.edutrack.dao.UserDAO();

    public static class TaskItem {
        int id;
        String name;
        String deadline;
        boolean completed;

        TaskItem(int id, String name, String deadline, boolean completed) {
            this.id = id;
            this.name = name;
            this.deadline = deadline;
            this.completed = completed;
        }

        TaskItem(String name, String deadline) {
            this.id = 0;
            this.name = name;
            this.deadline = deadline;
            this.completed = false;
        }
    }

    public static class GoalItem {
        int id;
        String name;
        String deadline;
        boolean completed;

        GoalItem(int id, String name, String deadline, boolean completed) {
            this.id = id;
            this.name = name;
            this.deadline = deadline;
            this.completed = completed;
        }

        GoalItem(String name, String deadline) {
            this.id = 0;
            this.name = name;
            this.deadline = deadline;
            this.completed = false;
        }
    }

    public static class EventItem {
        int id;
        String type;
        String name;
        String date;
        String note;

        EventItem(int id, String type, String name, String date, String note) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.date = date;
            this.note = note;
        }

        EventItem(String type, String name, String date, String note) {
            this.id = 0;
            this.type = type;
            this.name = name;
            this.date = date;
            this.note = note;
        }
    }

    // Helper method to check if a date string (mm/dd/yyyy) is overdue
    private boolean isOverdue(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate dueDate = LocalDate.parse(dateStr, formatter);
            return dueDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            try {
                // Try alternative format dd/MM/yyyy
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dueDate = LocalDate.parse(dateStr, formatter2);
                return dueDate.isBefore(LocalDate.now());
            } catch (DateTimeParseException e2) {
                try {
                    // Try ISO format yyyy-MM-dd
                    LocalDate dueDate = LocalDate.parse(dateStr);
                    return dueDate.isBefore(LocalDate.now());
                } catch (DateTimeParseException e3) {
                    return false;
                }
            }
        }
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty())
            return false;
        String[] formats = { "MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd" };
        for (String fmt : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt);
                LocalDate.parse(dateStr, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // strict parsers might fail if not fully matching pattern
                // ignore
            }
        }
        return false;
    }

    @FXML
    private void initialize() {
        loadFromDatabase();
        refreshTasksList();
        refreshGoalsList();
        refreshEventsList();
    }

    private void loadFromDatabase() {
        User user = SessionManager.getCurrentUser();
        if (user == null)
            return;
        int userId = user.getId();

        loadTasks(userId);
        loadGoals(userId);
        loadEvents(userId);
    }

    private void loadTasks(int userId) {
        tasks.clear();
        List<Task> dbTasks = taskDAO.getTasksByUserId(userId);
        for (Task t : dbTasks) {
            tasks.add(new TaskItem(t.getId(), t.getTitle(), t.getDueDate(), "COMPLETED".equals(t.getStatus())));
        }
    }

    private void loadGoals(int userId) {
        goals.clear();
        List<GoalDAO.GoalRecord> dbGoals = goalDAO.getGoalsByUserId(userId);
        for (GoalDAO.GoalRecord g : dbGoals) {
            goals.add(new GoalItem(g.id, g.name, g.deadline, g.completed));
        }
    }

    private void loadEvents(int userId) {
        events.clear();
        List<EventDAO.EventRecord> dbEvents = eventDAO.getEventsByUserId(userId);
        for (EventDAO.EventRecord ev : dbEvents) {
            events.add(new EventItem(ev.id, ev.type, ev.name, ev.eventDate, ev.note));
        }
    }

    @FXML
    private void showAddTaskDialog(ActionEvent e) {
        Dialog<TaskItem> dialog = new Dialog<>();
        dialog.setTitle("Add a task");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setStyle("-fx-background-color: #E8E0F0;");

        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Enter task name");
        taskNameField.setPrefWidth(350);
        taskNameField.setStyle("-fx-border-color: #6B4C9A; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextField deadlineField = new TextField();
        deadlineField.setPromptText("mm/dd/yyyy");
        deadlineField.setPrefWidth(350);
        deadlineField.setStyle("-fx-border-color: #6B4C9A; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label taskLabel = new Label("Task Name");
        taskLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        Label deadlineLabel = new Label("Deadline");
        deadlineLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        grid.add(taskLabel, 0, 0);
        grid.add(taskNameField, 0, 1);
        grid.add(deadlineLabel, 0, 2);
        grid.add(deadlineField, 0, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #E8E0F0;");

        dialog.setResultConverter(button -> {
            if (button == addButton) {
                String name = taskNameField.getText().trim();
                String deadline = deadlineField.getText().trim();
                if (!name.isEmpty()) {
                    return new TaskItem(name, deadline);
                }
            }
            return null;
        });

        // Add validation prevention
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(addButton);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            boolean valid = true;
            String date = deadlineField.getText().trim();
            if (!date.isEmpty() && !isValidDate(date)) {
                showAlert("Invalid Date", "Please enter a valid date (mm/dd/yyyy).");
                valid = false;
            }
            if (taskNameField.getText().trim().isEmpty()) {
                valid = false;
            }

            if (!valid) {
                event.consume();
            }
        });

        Optional<TaskItem> result = dialog.showAndWait();
        result.ifPresent(task -> {
            User user = SessionManager.getCurrentUser();
            if (user != null) {
                // Save to database
                com.edutrack.model.Task dbTask = new com.edutrack.model.Task(
                        user.getId(), task.name, "", task.deadline, "CS 101"); // Default tag
                boolean success = taskDAO.addTask(dbTask);
                if (success) {
                    loadTasks(user.getId());
                    refreshTasksList();
                } else {
                    showAlert("Error", "Failed to save task.");
                }
            }
        });
    }

    @FXML
    private void showAddGoalDialog(ActionEvent e) {
        Dialog<GoalItem> dialog = new Dialog<>();
        dialog.setTitle("Add a personal goal");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setStyle("-fx-background-color: #E8E0F0;");

        TextField goalNameField = new TextField();
        goalNameField.setPromptText("Enter the personal goal name");
        goalNameField.setPrefWidth(350);
        goalNameField.setStyle("-fx-border-color: #6B4C9A; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextField deadlineField = new TextField();
        deadlineField.setPromptText("mm/dd/yyyy");
        deadlineField.setPrefWidth(350);
        deadlineField.setStyle("-fx-border-color: #6B4C9A; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label goalLabel = new Label("Goal Name");
        goalLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        Label deadlineLabel = new Label("Deadline");
        deadlineLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        grid.add(goalLabel, 0, 0);
        grid.add(goalNameField, 0, 1);
        grid.add(deadlineLabel, 0, 2);
        grid.add(deadlineField, 0, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #E8E0F0;");

        dialog.setResultConverter(button -> {
            if (button == addButton) {
                String name = goalNameField.getText().trim();
                String deadline = deadlineField.getText().trim();
                if (!name.isEmpty()) {
                    return new GoalItem(name, deadline);
                }
            }
            return null;
        });

        // Add validation prevention
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(addButton);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            boolean valid = true;
            String date = deadlineField.getText().trim();
            if (!date.isEmpty() && !isValidDate(date)) {
                showAlert("Invalid Date", "Please enter a valid date (mm/dd/yyyy).");
                valid = false;
            }
            if (goalNameField.getText().trim().isEmpty()) {
                valid = false;
            }

            if (!valid) {
                event.consume();
            }
        });

        Optional<GoalItem> result = dialog.showAndWait();
        result.ifPresent(goal -> {
            User user = SessionManager.getCurrentUser();
            if (user != null) {
                boolean success = goalDAO.addGoal(user.getId(), goal.name, goal.deadline);
                if (success) {
                    loadGoals(user.getId());
                    refreshGoalsList();
                } else {
                    showAlert("Error", "Failed to save goal.");
                }
            }
        });
    }

    @FXML
    private void showAddEventDialog(ActionEvent e) {
        Dialog<EventItem> dialog = new Dialog<>();
        dialog.setTitle("Add an event");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setStyle("-fx-background-color: #E8E0F0;");

        TextField eventTypeField = new TextField();
        eventTypeField.setPromptText("Enter the event type");
        eventTypeField.setPrefWidth(350);
        eventTypeField.setStyle("-fx-border-color: #6B4C9A; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextField eventNameField = new TextField();
        eventNameField.setPromptText("Enter the event name");
        eventNameField.setPrefWidth(350);
        eventNameField.setStyle("-fx-border-color: #6B4C9A; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextField dateField = new TextField();
        dateField.setPromptText("mm/dd/yyyy");
        dateField.setPrefWidth(350);
        dateField.setStyle("-fx-border-color: #6B4C9A; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextField noteField = new TextField();
        noteField.setPromptText("Enter personal note");
        noteField.setPrefWidth(350);
        noteField.setStyle("-fx-border-color: #6B4C9A; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label typeLabel = new Label("Event Type");
        typeLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        Label nameLabel = new Label("Event Name");
        nameLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        Label dateLabel = new Label("Date");
        dateLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        Label noteLabel = new Label("Personal Note");
        noteLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        grid.add(typeLabel, 0, 0);
        grid.add(eventTypeField, 0, 1);
        grid.add(nameLabel, 0, 2);
        grid.add(eventNameField, 0, 3);
        grid.add(dateLabel, 0, 4);
        grid.add(dateField, 0, 5);
        grid.add(noteLabel, 0, 6);
        grid.add(noteField, 0, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #E8E0F0;");

        dialog.setResultConverter(button -> {
            if (button == addButton) {
                String type = eventTypeField.getText().trim();
                String name = eventNameField.getText().trim();
                String rawDate = dateField.getText().trim();
                String date = formatDateForDB(rawDate);
                String note = noteField.getText().trim();
                if (!name.isEmpty()) {
                    return new EventItem(type, name, date, note);
                }
            }
            return null;
        });

        // Add validation prevention
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(addButton);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            boolean valid = true;
            String date = dateField.getText().trim();
            if (!date.isEmpty() && !isValidDate(date)) {
                showAlert("Invalid Date", "Please enter a valid date (mm/dd/yyyy).");
                valid = false;
            }
            if (eventNameField.getText().trim().isEmpty()) {
                valid = false;
            }

            if (!valid) {
                event.consume();
            }
        });

        Optional<EventItem> result = dialog.showAndWait();
        result.ifPresent(event -> {
            User user = SessionManager.getCurrentUser();
            if (user != null) {
                eventDAO.addEvent(user.getId(), event.type, event.name, event.date, event.note);
                loadEvents(user.getId());
                refreshEventsList();
            }
        });
    }

    private void refreshTasksList() {
        if (tasksContainer == null)
            return;
        tasksContainer.getChildren().clear();

        for (int i = 0; i < tasks.size(); i++) {
            TaskItem task = tasks.get(i);
            int index = i;
            HBox row = createTaskRow(task, index);
            tasksContainer.getChildren().add(row);
        }
    }

    private void refreshGoalsList() {
        if (goalsContainer == null)
            return;
        goalsContainer.getChildren().clear();

        for (int i = 0; i < goals.size(); i++) {
            GoalItem goal = goals.get(i);
            int index = i;
            HBox row = createGoalRow(goal, index);
            goalsContainer.getChildren().add(row);
        }
    }

    private void refreshEventsList() {
        if (eventsContainer == null)
            return;
        eventsContainer.getChildren().clear();

        for (int i = 0; i < events.size(); i++) {
            EventItem event = events.get(i);
            int index = i;
            HBox row = createEventRow(event, index);
            eventsContainer.getChildren().add(row);
        }
    }

    private HBox createTaskRow(TaskItem task, int index) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(75);
        row.setMinHeight(75);

        // Check if overdue and apply red styling
        boolean overdue = !task.completed && isOverdue(task.deadline);
        if (overdue) {
            row.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 0 0 1 0; -fx-background-color: #fff0f0;");
        } else {
            row.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        }
        row.setPadding(new Insets(10, 10, 10, 10));

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(task.completed);
        checkBox.setOnAction(e -> {
            boolean isNowCompleted = checkBox.isSelected();
            task.completed = isNowCompleted;

            // Update status in DB
            taskDAO.updateTaskStatus(task.id, isNowCompleted ? "COMPLETED" : "PENDING");

            // Award points if completed and NOT overdue
            if (isNowCompleted && !isOverdue(task.deadline)) {
                User user = SessionManager.getCurrentUser();
                if (user != null) {
                    int oldLevel = user.getLevel(); // Get level BEFORE points
                    boolean pointsAdded = userDAO.addPoints(user.getId(), 10);

                    if (pointsAdded) {
                        // Update local session user
                        user.setXpAmount(user.getXpAmount() + 10);

                        int newLevel = user.getLevel(); // Get level AFTER points

                        // Check for Level Up
                        if (newLevel > oldLevel) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Level Up!");
                            alert.setHeaderText(null);
                            alert.setContentText("Congratulations! You've reached Level " + newLevel + "!");
                            alert.showAndWait();
                        }

                        // Check for new badges
                        com.edutrack.util.BadgeService.checkAndAwardBadges(user);
                    }
                }
            }
        });

        Label nameLabel = new Label(task.name);
        nameLabel.setFont(new Font(14));
        if (overdue) {
            nameLabel.setStyle("-fx-text-fill: #c6131b; -fx-font-weight: bold;");
        } else {
            nameLabel.setStyle("-fx-text-fill: #333333;");
        }

        Label overdueLabel = new Label(overdue ? " [OVERDUE]" : "");
        overdueLabel.setStyle("-fx-text-fill: #c6131b; -fx-font-weight: bold;");

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label(task.deadline);
        dateLabel.setFont(new Font(14));
        dateLabel.setStyle(overdue ? "-fx-text-fill: #c6131b; -fx-font-weight: bold;" : "-fx-text-fill: #888888;");

        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #c6131b; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            // Delete from database
            taskDAO.deleteTask(task.id);
            // Update UI list
            tasks.remove(index);
            refreshTasksList();
        });

        row.getChildren().addAll(checkBox, nameLabel, overdueLabel, spacer, dateLabel, deleteBtn);
        return row;
    }

    private HBox createGoalRow(GoalItem goal, int index) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(75);
        row.setMinHeight(75);

        // Check if overdue and apply red styling
        boolean overdue = !goal.completed && isOverdue(goal.deadline);
        if (overdue) {
            row.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 0 0 1 0; -fx-background-color: #fff0f0;");
        } else {
            row.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        }
        row.setPadding(new Insets(10, 10, 10, 10));

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(goal.completed);
        checkBox.setOnAction(e -> {
            goal.completed = checkBox.isSelected();
            goalDAO.updateGoalCompleted(goal.id, goal.completed);
        });

        Label nameLabel = new Label(goal.name);
        nameLabel.setFont(new Font(14));
        if (overdue) {
            nameLabel.setStyle("-fx-text-fill: #c6131b; -fx-font-weight: bold;");
        } else {
            nameLabel.setStyle("-fx-text-fill: #333333;");
        }

        Label overdueLabel = new Label(overdue ? " [OVERDUE]" : "");
        overdueLabel.setStyle("-fx-text-fill: #c6131b; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label(goal.deadline != null ? goal.deadline : "");
        dateLabel.setFont(new Font(14));
        dateLabel.setStyle(overdue ? "-fx-text-fill: #c6131b; -fx-font-weight: bold;" : "-fx-text-fill: #888888;");

        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #c6131b; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            // Delete from database
            goalDAO.deleteGoal(goal.id);
            // Update UI list
            goals.remove(index);
            refreshGoalsList();
        });

        row.getChildren().addAll(checkBox, nameLabel, overdueLabel, spacer, dateLabel, deleteBtn);
        return row;
    }

    private HBox createEventRow(EventItem event, int index) {
        HBox row = new HBox(10);
        row.setPrefHeight(100);
        row.setMinHeight(100);

        // Check if overdue and apply red styling
        boolean overdue = isOverdue(event.date);
        if (overdue) {
            row.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: #ff6b6b; -fx-background-color: #fff0f0;");
        } else {
            row.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: #e0e0e0;");
        }
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        HBox typeRow = new HBox(5);
        Label typeLabel = new Label(event.type);
        typeLabel.setFont(new Font(18));
        typeLabel.setStyle(overdue ? "-fx-font-weight: bold; -fx-text-fill: #c6131b;"
                : "-fx-font-weight: bold; -fx-text-fill: #333333;");

        Label overdueLabel = new Label(overdue ? " [OVERDUE]" : "");
        overdueLabel.setFont(new Font(14));
        overdueLabel.setStyle("-fx-text-fill: #c6131b; -fx-font-weight: bold;");
        typeRow.getChildren().addAll(typeLabel, overdueLabel);

        Label dateLabel = new Label("Date: " + event.date);
        dateLabel.setFont(new Font(12));
        dateLabel.setStyle(overdue ? "-fx-text-fill: #c6131b; -fx-font-weight: bold;" : "-fx-text-fill: #666666;");

        Label nameLabel = new Label(event.name);
        nameLabel.setFont(new Font(12));
        if (overdue) {
            nameLabel.setStyle("-fx-text-fill: #c6131b;");
        } else {
            nameLabel.setStyle("-fx-text-fill: #333333;");
        }

        infoBox.getChildren().addAll(typeRow, dateLabel, nameLabel);

        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #c6131b; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            // Delete from database
            eventDAO.deleteEvent(event.id);
            // Update UI list
            events.remove(index);
            refreshEventsList();
        });

        row.getChildren().addAll(infoBox, deleteBtn);
        return row;
    }

    private String formatDateForDB(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return "";
        try {
            // Assume input is MM/dd/yyyy as prompted
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date = LocalDate.parse(dateStr, inputFormatter);
            return date.toString(); // Returns yyyy-MM-dd
        } catch (Exception e) {
            // If parsing fails (maybe already correct or different format), return original
            return dateStr;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}