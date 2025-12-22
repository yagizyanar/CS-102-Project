package com.edutrack.controller;

import com.edutrack.dao.TaskDAO;
import com.edutrack.dao.GoalDAO;
import com.edutrack.dao.EventDAO;
import com.edutrack.dao.FriendDAO;
import com.edutrack.model.Task;
import com.edutrack.model.User;
import com.edutrack.model.UserRequest;
import com.edutrack.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class NotificationController {

    @FXML
    private AnchorPane panelRoot;
    @FXML
    private VBox listBox;
    @FXML
    private Button btnClose;

    private final List<AppNotification> notifications = new ArrayList<>();
    private final TaskDAO taskDAO = new TaskDAO();
    private final GoalDAO goalDAO = new GoalDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final FriendDAO friendDAO = new FriendDAO();

    @FXML
    private void initialize() {
        notifications.clear();
        loadNotifications();
        render();
    }

    private void loadNotifications() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            System.out.println("NotificationController: No user logged in");
            notifications.add(new AppNotification("⚠️ Not logged in",
                    "Please log in to see notifications.", LocalDateTime.now(), false));
            return;
        }
        int userId = user.getId();
        System.out.println("NotificationController: Loading notifications for user " + userId);

        // Load pending friend requests
        List<UserRequest> pendingRequests = friendDAO.getPendingRequests(userId);
        System.out.println("NotificationController: Found " + pendingRequests.size() + " pending friend requests");
        for (UserRequest req : pendingRequests) {
            notifications.add(new AppNotification("👤 Friend request",
                    req.getUsername() + " sent you a friend request.", LocalDateTime.now(), true));
        }

        // Load ALL upcoming tasks (non-completed, not overdue)
        List<Task> tasks = taskDAO.getTasksByUserId(userId);
        System.out.println("NotificationController: Found " + tasks.size() + " total tasks");
        for (Task t : tasks) {
            if (!"COMPLETED".equals(t.getStatus()) && !isOverdue(t.getDueDate())) {
                String urgency = isDueWithin2Days(t.getDueDate()) ? "📋 Task due soon" : "📋 Upcoming task";
                notifications.add(new AppNotification(urgency,
                        "\"" + t.getTitle() + "\" - Due: " + (t.getDueDate() != null ? t.getDueDate() : "No date"),
                        LocalDateTime.now(), true));
            }
        }

        // Load ALL upcoming goals (non-completed, not overdue)
        List<GoalDAO.GoalRecord> goals = goalDAO.getGoalsByUserId(userId);
        System.out.println("NotificationController: Found " + goals.size() + " total goals");
        for (GoalDAO.GoalRecord g : goals) {
            if (!g.completed && !isOverdue(g.deadline)) {
                String urgency = isDueWithin2Days(g.deadline) ? "🎯 Goal due soon" : "🎯 Upcoming goal";
                notifications.add(new AppNotification(urgency,
                        "\"" + g.name + "\" - Due: " + (g.deadline != null ? g.deadline : "No date"),
                        LocalDateTime.now(), true));
            }
        }

        // Load ALL upcoming events (not overdue)
        List<EventDAO.EventRecord> events = eventDAO.getEventsByUserId(userId);
        System.out.println("NotificationController: Found " + events.size() + " total events");
        for (EventDAO.EventRecord ev : events) {
            if (!isOverdue(ev.eventDate)) {
                String urgency = isDueWithin2Days(ev.eventDate) ? "📅 Event coming up" : "📅 Upcoming event";
                notifications.add(new AppNotification(urgency,
                        "\"" + ev.name + "\" - Date: " + (ev.eventDate != null ? ev.eventDate : "No date"),
                        LocalDateTime.now(), true));
            }
        }

        System.out.println("NotificationController: Total notifications loaded: " + notifications.size());

        // If no notifications, add a placeholder
        if (notifications.isEmpty()) {
            notifications.add(new AppNotification("✅ All caught up!",
                    "No new notifications.", LocalDateTime.now(), false));
        }
    }

    private boolean isOverdue(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return false;
        try {
            LocalDate dueDate = parseDate(dateStr);
            return dueDate != null && dueDate.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return null;
        String[] formats = { "MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "M/d/yyyy", "d/M/yyyy" };
        for (String fmt : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt);
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean isDueWithin2Days(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return false;
        try {
            LocalDate dueDate = null;

            // Try various date formats
            String[] formats = { "MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "M/d/yyyy", "d/M/yyyy" };
            for (String fmt : formats) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt);
                    dueDate = LocalDate.parse(dateStr, formatter);
                    break;
                } catch (DateTimeParseException e) {
                    // Try next format
                }
            }

            // If still null, try ISO format as fallback
            if (dueDate == null) {
                dueDate = LocalDate.parse(dateStr);
            }

            LocalDate now = LocalDate.now();
            LocalDate twoDaysFromNow = now.plusDays(2);

            // Due within 2 days means: today <= dueDate <= today+2
            return !dueDate.isBefore(now) && !dueDate.isAfter(twoDaysFromNow);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @FXML
    private void closePanel() {

        if (panelRoot != null && panelRoot.getParent() instanceof Pane parent) {
            parent.getChildren().remove(panelRoot);
            return;
        }

        // Fallback: just hide if parent removal failed (Main.goBack unavailable)
        if (panelRoot != null)
            panelRoot.setVisible(false);
    }

    private void render() {
        if (listBox == null)
            return;
        listBox.getChildren().clear();
        for (AppNotification n : notifications) {
            listBox.getChildren().add(makeRow(n));
        }
    }

    private HBox makeRow(AppNotification n) {
        Circle dot = new Circle(5);
        dot.setFill(Color.web("#2aa2d8"));
        dot.setVisible(n.unread);

        Label title = new Label(n.title);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        title.setTextFill(Color.BLACK);

        Label time = new Label(n.timeText());
        time.setStyle("-fx-font-size: 10px; -fx-opacity: 0.65;");
        time.setTextFill(Color.BLACK);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(8, title, spacer, time);
        header.setAlignment(Pos.CENTER_LEFT);

        Label body = new Label(n.body);
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 11px;");
        body.setTextFill(Color.BLACK);

        VBox textBox = new VBox(4, header, body);

        HBox row = new HBox(10, dot, textBox);
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.TOP_LEFT);
        row.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: rgba(0,0,0,0.08);" +
                "-fx-border-width: 1;");

        // ✅ unread dot disappears when mouse enters the notification card
        row.setOnMouseEntered(e -> {
            if (n.unread) {
                n.unread = false;
                dot.setVisible(false);
            }
        });

        return row;
    }

    /** OPTIONAL popup overlay open (top-right) */
    public static void showOverlay(StackPane overlayRoot) {
        try {
            Parent existing = (Parent) overlayRoot.lookup("#notificationPanel");
            if (existing != null) {
                overlayRoot.getChildren().remove(existing);
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    NotificationController.class.getResource("/com/edutrack/view/notification.fxml"));
            Parent panel = loader.load();
            panel.setId("notificationPanel");

            StackPane.setAlignment(panel, Pos.TOP_RIGHT);
            StackPane.setMargin(panel, new Insets(16, 16, 0, 0));
            overlayRoot.getChildren().add(panel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class AppNotification {
        final String title;
        final String body;
        final LocalDateTime time;
        boolean unread;

        AppNotification(String title, String body, LocalDateTime time, boolean unread) {
            this.title = title;
            this.body = body;
            this.time = time;
            this.unread = unread;
        }

        String timeText() {
            return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }
}
