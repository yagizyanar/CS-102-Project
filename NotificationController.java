package project;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationController {

    @FXML private AnchorPane panelRoot;
    @FXML private VBox listBox;
    @FXML private Button btnClose;

    private final List<AppNotification> notifications = new ArrayList<>();

    @FXML
    private void initialize() {
        // ✅ EXAMPLE notifications 
        notifications.clear();

        notifications.add(new AppNotification("👤 Friend request",
                "Ahmet sent you a friend request.", LocalDateTime.now().minusMinutes(2), true));

        notifications.add(new AppNotification("💬 Thread reply",
                "Elif replied to your thread.", LocalDateTime.now().minusMinutes(15), true));

        notifications.add(new AppNotification("⏰ Reminder",
                "Don’t forget to finish your task today.", LocalDateTime.now().minusHours(3), true));

        notifications.add(new AppNotification("👤 Friend accepted",
                "Mert accepted your friend request.", LocalDateTime.now().minusDays(1), false));

        render();
    }

    @FXML
    private void closePanel() {
        
        if (panelRoot != null && panelRoot.getParent() instanceof Pane parent) {
            parent.getChildren().remove(panelRoot);
            return;
        }

        Main.goBack();
    }

    private void render() {
        if (listBox == null) return;
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

            FXMLLoader loader = new FXMLLoader(NotificationController.class.getResource("/project/fxml/notification.fxml"));
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
