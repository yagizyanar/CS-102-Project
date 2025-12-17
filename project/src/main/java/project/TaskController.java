package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class TaskController {

    @FXML
    private VBox tasksContainer;

    @FXML
    private VBox goalsContainer;

    @FXML
    private VBox eventsContainer;

    private static ArrayList<Task> tasks = new ArrayList<>();
    private static ArrayList<Goal> goals = new ArrayList<>();
    private static ArrayList<Event> events = new ArrayList<>();

    public static class Task {
        String name;
        String deadline;
        boolean completed;

        Task(String name, String deadline) {
            this.name = name;
            this.deadline = deadline;
            this.completed = false;
        }
    }

    public static class Goal {
        String name;
        boolean completed;

        Goal(String name) {
            this.name = name;
            this.completed = false;
        }
    }

    public static class Event {
        String type;
        String name;
        String date;
        String note;

        Event(String type, String name, String date, String note) {
            this.type = type;
            this.name = name;
            this.date = date;
            this.note = note;
        }
    }

    @FXML
    private void initialize() {
        refreshTasksList();
        refreshGoalsList();
        refreshEventsList();
    }

    @FXML
    private void showAddTaskDialog(ActionEvent e) {
        Dialog<Task> dialog = new Dialog<>();
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
                    return new Task(name, deadline);
                }
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(task -> {
            tasks.add(task);
            refreshTasksList();
        });
    }

    @FXML
    private void showAddGoalDialog(ActionEvent e) {
        Dialog<Goal> dialog = new Dialog<>();
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

        Label goalLabel = new Label("Goal Name");
        goalLabel.setStyle("-fx-text-fill: #6B4C9A; -fx-font-size: 12;");

        grid.add(goalLabel, 0, 0);
        grid.add(goalNameField, 0, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #E8E0F0;");

        dialog.setResultConverter(button -> {
            if (button == addButton) {
                String name = goalNameField.getText().trim();
                if (!name.isEmpty()) {
                    return new Goal(name);
                }
            }
            return null;
        });

        Optional<Goal> result = dialog.showAndWait();
        result.ifPresent(goal -> {
            goals.add(goal);
            refreshGoalsList();
        });
    }

    @FXML
    private void showAddEventDialog(ActionEvent e) {
        Dialog<Event> dialog = new Dialog<>();
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
                String date = dateField.getText().trim();
                String note = noteField.getText().trim();
                if (!name.isEmpty()) {
                    return new Event(type, name, date, note);
                }
            }
            return null;
        });

        Optional<Event> result = dialog.showAndWait();
        result.ifPresent(event -> {
            events.add(event);
            refreshEventsList();
        });
    }

    private void refreshTasksList() {
        if (tasksContainer == null) return;
        tasksContainer.getChildren().clear();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            int index = i;
            HBox row = createTaskRow(task, index);
            tasksContainer.getChildren().add(row);
        }
    }

    private void refreshGoalsList() {
        if (goalsContainer == null) return;
        goalsContainer.getChildren().clear();

        for (int i = 0; i < goals.size(); i++) {
            Goal goal = goals.get(i);
            int index = i;
            HBox row = createGoalRow(goal, index);
            goalsContainer.getChildren().add(row);
        }
    }

    private void refreshEventsList() {
        if (eventsContainer == null) return;
        eventsContainer.getChildren().clear();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            int index = i;
            HBox row = createEventRow(event, index);
            eventsContainer.getChildren().add(row);
        }
    }

    private HBox createTaskRow(Task task, int index) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(75);
        row.setMinHeight(75);
        row.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        row.setPadding(new Insets(10, 10, 10, 10));

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(task.completed);
        checkBox.setOnAction(e -> task.completed = checkBox.isSelected());

        Label nameLabel = new Label(task.name);
        nameLabel.setFont(new Font(14));

        // Spacer - ortadaki boşluk
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label(task.deadline);
        dateLabel.setFont(new Font(14));
        dateLabel.setStyle("-fx-text-fill: #888888;");

        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #c6131b; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            tasks.remove(index);
            refreshTasksList();
        });

        row.getChildren().addAll(checkBox, nameLabel, spacer, dateLabel, deleteBtn);
        return row;
    }

    private HBox createGoalRow(Goal goal, int index) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(75);
        row.setMinHeight(75);
        row.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        row.setPadding(new Insets(10, 10, 10, 10));

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(goal.completed);
        checkBox.setOnAction(e -> goal.completed = checkBox.isSelected());

        Label nameLabel = new Label(goal.name);
        nameLabel.setFont(new Font(14));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #c6131b; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            goals.remove(index);
            refreshGoalsList();
        });

        row.getChildren().addAll(checkBox, nameLabel, spacer, deleteBtn);
        return row;
    }

    private HBox createEventRow(Event event, int index) {
        HBox row = new HBox(10);
        row.setPrefHeight(100);
        row.setMinHeight(100);
        row.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: #e0e0e0;");
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label typeLabel = new Label(event.type);
        typeLabel.setFont(new Font(18));
        typeLabel.setStyle("-fx-font-weight: bold;");

        Label dateLabel = new Label("Date: " + event.date);
        dateLabel.setFont(new Font(12));
        dateLabel.setStyle("-fx-text-fill: #666666;");

        Label nameLabel = new Label(event.name);
        nameLabel.setFont(new Font(12));

        infoBox.getChildren().addAll(typeLabel, dateLabel, nameLabel);

        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #c6131b; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> {
            events.remove(index);
            refreshEventsList();
        });

        row.getChildren().addAll(infoBox, deleteBtn);
        return row;
    }

    private void switchPage(ActionEvent e, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/project/" + fxml));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setWidth(width);
            stage.setHeight(height);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}