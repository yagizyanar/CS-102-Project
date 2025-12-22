package com.edutrack.controller;

import com.edutrack.Main;
import com.edutrack.dao.TaskDAO;
import com.edutrack.model.Task;
import com.edutrack.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ToDoController {

    @FXML
    private ListView<Task> taskListView;
    @FXML
    private TextField titleField;
    @FXML
    private TextField courseField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeField;

    private final TaskDAO taskDAO = new TaskDAO();
    private ObservableList<Task> tasks;

    public void initialize() {
        int userId = SessionManager.getCurrentUser().getId();
        tasks = FXCollections.observableArrayList(taskDAO.getTasksByUserId(userId));
        taskListView.setItems(tasks);

        // Custom Cell Factory to show Overdue/Completed Status
        taskListView.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String displayText = item.getTitle() + " (" + item.getCourseTag() + ") - Due: " + item.getDueDate();

                    if ("COMPLETED".equals(item.getStatus())) {
                        displayText += " [DONE]";
                        setStyle("-fx-text-fill: green; -fx-font-style: italic;");
                    } else {
                        // Check Overdue
                        try {
                            if (item.getDueDate() != null && !item.getDueDate().isEmpty()) {
                                String dueStr = item.getDueDate();
                                if (dueStr.contains(" ")) {
                                    // Has Time
                                    java.time.LocalDateTime due = java.time.LocalDateTime.parse(dueStr,
                                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                                    if (due.isBefore(java.time.LocalDateTime.now())) {
                                        displayText += " [OVERDUE]";
                                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                    } else {
                                        setStyle("");
                                    }
                                } else {
                                    // Only Date
                                    LocalDate due = LocalDate.parse(dueStr);
                                    if (due.isBefore(LocalDate.now())) {
                                        displayText += " [OVERDUE]";
                                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                    } else {
                                        setStyle("");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // Ignore parse errors
                            setStyle("");
                            // System.out.println("Parse error: " + e.getMessage());
                        }
                    }
                    setText(displayText);
                }
            }
        });
    }

    @FXML
    private void handleAddTask() {
        String title = titleField.getText();
        String course = courseField.getText();
        LocalDate date = datePicker.getValue();
        String time = timeField.getText();

        if (title.isEmpty() || date == null)
            return;

        String finalDueDate = date.toString();
        if (time != null && !time.trim().isEmpty()) {
            finalDueDate += " " + time.trim();
            // Basic validation could involve check regex like \d{2}:\d{2}
        }

        Task newTask = new Task(SessionManager.getCurrentUser().getId(), title, "", finalDueDate, course);
        if (taskDAO.addTask(newTask)) {
            // Refresh list
            initialize();
            titleField.clear();
            courseField.clear();
            datePicker.setValue(null);
            timeField.clear();
        }
    }

    @FXML
    private void handleMarkCompleted() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Marking task as completed: " + selected.getId()); // Debug
            taskDAO.updateTaskStatus(selected.getId(), "COMPLETED");
            initialize();
        } else {
            System.out.println("No task selected!");
        }
    }

    @FXML
    private void handleDelete() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            taskDAO.deleteTask(selected.getId());
            initialize();
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Main.setRoot("Dashboard");
    }
}
