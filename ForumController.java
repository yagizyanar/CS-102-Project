package project;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ForumController implements Initializable {

    // ====== Scaling wrapper (from FXML) ======
    @FXML private StackPane viewport;
    @FXML private Group rootGroup;

    private static final double DESIGN_W = 1920.0;
    private static final double DESIGN_H = 1080.0;

    // ====== UI (from FXML) ======
    @FXML private Label lblTitle;
    @FXML private Label lblThreadTitle;
    @FXML private Label lblStats;

    @FXML private ComboBox<String> cmbClasses;
    @FXML private TextField txtSearch;

    @FXML private ListView<String> listThreads;

    @FXML private ScrollPane scrollMessages;
    @FXML private VBox messagesBox;

    @FXML private TextArea txtMessage;

    // ====== Data: each course has its own threads/messages ======
    private final Map<String, LinkedHashMap<String, List<Message>>> data = new LinkedHashMap<>();
    private final ObservableList<String> visibleThreads = FXCollections.observableArrayList();

    private String currentCourse;
    private String currentThread;

    // Prevent recursion when rebuilding list
    private boolean isUpdatingThreadList = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ---- Course list (edit to your real list if needed) ----
        cmbClasses.setItems(FXCollections.observableArrayList(
                "CS 101", "CS 102", "CS 103", "CS 201", "CS 202", "CS 203", "CS 204", "CS 205"
        ));

        listThreads.setItems(visibleThreads);

        // Click thread opens it (no Open button)
        listThreads.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (isUpdatingThreadList) return;
            if (newV != null && !newV.equals(currentThread)) openThread(newV);
        });

        // Select first course by default
        if (!cmbClasses.getItems().isEmpty()) {
            cmbClasses.getSelectionModel().selectFirst();
            onClassChanged();
        }

        // Bind scaling after layout is ready
        Platform.runLater(this::bindScale);
    }

    // ==================== SCALE (keeps design identical) ====================

    private void bindScale() {
        if (viewport == null || rootGroup == null) return;

        ChangeListener<Number> resize = (obs, oldVal, newVal) -> applyScale();
        viewport.widthProperty().addListener(resize);
        viewport.heightProperty().addListener(resize);

        applyScale();
    }

    private void applyScale() {
        double w = viewport.getWidth();
        double h = viewport.getHeight();
        if (w <= 0 || h <= 0) return;

        double scale = Math.min(w / DESIGN_W, h / DESIGN_H);

        rootGroup.setScaleX(scale);
        rootGroup.setScaleY(scale);

        rootGroup.setTranslateX((w - DESIGN_W * scale) / 2.0);
        rootGroup.setTranslateY((h - DESIGN_H * scale) / 2.0);
    }

    // ==================== UI HANDLERS ====================

    @FXML
    private void onClassChanged() {
        currentCourse = cmbClasses.getValue();
        if (currentCourse == null) return;

        data.putIfAbsent(currentCourse, new LinkedHashMap<>());

        lblTitle.setText(currentCourse.replace(" ", "") + " FORUM");

        LinkedHashMap<String, List<Message>> threads = data.get(currentCourse);

        // Default threads per course (only if empty)
        if (threads.isEmpty()) {
            threads.put("#General Comment", new ArrayList<>());
            threads.put("#Homework Help", new ArrayList<>());
            threads.put("#Announcements", new ArrayList<>());
            threads.put("#Exams", new ArrayList<>());

            threads.get("#General Comment").add(
                    new Message("System", "Welcome to " + currentCourse + " forum!", LocalDateTime.now())
            );
        }

        openThread("#General Comment");
    }

    @FXML
    private void searchThreads() {
        refreshThreadListPreserveSelection();
    }

    @FXML
    private void newThread() {
        if (currentCourse == null) return;

        TextInputDialog dialog = new TextInputDialog("#New Thread");
        dialog.setTitle("New Thread");
        dialog.setHeaderText("Create a new thread for " + currentCourse);
        dialog.setContentText("Thread title:");

        dialog.showAndWait().ifPresent(raw -> {
            String name = raw.trim();
            if (name.isEmpty()) return;
            if (!name.startsWith("#")) name = "#" + name;

            LinkedHashMap<String, List<Message>> threads = data.get(currentCourse);
            if (threads.containsKey(name)) {
                info("Thread exists", "That thread already exists.");
                return;
            }

            threads.put(name, new ArrayList<>());
            openThread(name);
        });
    }

    @FXML
    private void deleteSelectedThread() {
        if (currentCourse == null) return;

        String selected = listThreads.getSelectionModel().getSelectedItem();
        if (selected == null) {
            info("No selection", "Select a thread first.");
            return;
        }

        if ("#General Comment".equals(selected)) {
            info("Can't delete", "You can't delete the default thread.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Thread");
        confirm.setHeaderText("Delete " + selected + " ?");
        confirm.setContentText("All messages will be removed.");

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            data.get(currentCourse).remove(selected);

            if (Objects.equals(currentThread, selected)) openThread("#General Comment");
            else {
                refreshThreadListPreserveSelection();
                updateStats();
            }
        }
    }

    @FXML
    private void refreshThread() {
        refreshThreadListPreserveSelection();
        renderMessages();
        updateStats();
    }

    @FXML
    private void clearMessage() {
        if (txtMessage != null) txtMessage.clear();
    }

    @FXML
    private void sendMessage() {
        if (currentCourse == null || currentThread == null) return;

        String msg = (txtMessage.getText() == null) ? "" : txtMessage.getText().trim();
        if (msg.isEmpty()) return;

        data.get(currentCourse).get(currentThread)
                .add(new Message(safeUsername(), msg, LocalDateTime.now()));

        txtMessage.clear();
        renderMessages();
        updateStats();
    }

    // ==================== INTERNAL HELPERS ====================

    private void openThread(String thread) {
        if (currentCourse == null) return;

        LinkedHashMap<String, List<Message>> threads = data.get(currentCourse);
        threads.putIfAbsent(thread, new ArrayList<>());

        currentThread = thread;
        lblThreadTitle.setText(thread);

        refreshThreadListPreserveSelection();
        renderMessages();
        updateStats();
    }

    private void refreshThreadListPreserveSelection() {
        if (currentCourse == null) return;

        isUpdatingThreadList = true;
        try {
            String q = (txtSearch.getText() == null) ? "" : txtSearch.getText().trim().toLowerCase();
            LinkedHashMap<String, List<Message>> threads = data.get(currentCourse);

            List<String> filtered = new ArrayList<>();
            for (String t : threads.keySet()) {
                if (q.isEmpty() || t.toLowerCase().contains(q)) filtered.add(t);
            }

            visibleThreads.setAll(filtered);

            if (currentThread != null && visibleThreads.contains(currentThread)) {
                listThreads.getSelectionModel().select(currentThread);
            } else if (!visibleThreads.isEmpty()) {
                listThreads.getSelectionModel().select(0);
                currentThread = listThreads.getSelectionModel().getSelectedItem();
                if (currentThread != null) lblThreadTitle.setText(currentThread);
            }
        } finally {
            isUpdatingThreadList = false;
        }
    }

    private void renderMessages() {
        messagesBox.getChildren().clear();

        List<Message> msgs = data.get(currentCourse).getOrDefault(currentThread, List.of());
        for (Message m : msgs) {
            messagesBox.getChildren().add(makeMessageRow(m));
        }

        scrollMessages.layout();
        scrollMessages.setVvalue(1.0);
    }

    private VBox makeMessageRow(Message m) {
        boolean isMe = m.sender.equalsIgnoreCase(safeUsername());

        Label header = new Label(m.sender + " • " + m.timeFormatted());
        header.setStyle("-fx-font-size: 10px; -fx-opacity: 0.70;");

        Label body = new Label(m.text);
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 12px;");

        VBox bubble = new VBox(4, header, body);
        bubble.setPadding(new Insets(10));
        bubble.setMaxWidth(520);

        if (isMe) {
            bubble.setStyle("-fx-background-color: #33c0a7; -fx-background-radius: 14;");
            header.setStyle("-fx-font-size: 10px; -fx-opacity: 0.85; -fx-text-fill: white;");
            body.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
        } else {
            bubble.setStyle("-fx-background-color: #e6e6e6; -fx-background-radius: 14;");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox();
        if (isMe) row.getChildren().addAll(spacer, bubble);
        else row.getChildren().addAll(bubble, spacer);

        VBox container = new VBox(row);
        container.setPadding(new Insets(2, 0, 2, 0));
        return container;
    }

    private void updateStats() {
        if (currentCourse == null) {
            lblStats.setText("Threads: 0 | Messages: 0");
            return;
        }

        LinkedHashMap<String, List<Message>> threads = data.get(currentCourse);
        int tCount = threads.size();
        int mCount = threads.values().stream().mapToInt(List::size).sum();
        lblStats.setText("Threads: " + tCount + " | Messages: " + mCount);
    }

    private void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String safeUsername() {
        try {
            if (Main.getCurrentUser() != null && Main.getCurrentUser().getUsername() != null) {
                return Main.getCurrentUser().getUsername();
            }
        } catch (Throwable ignored) {}
        return "You";
    }

    // ==================== MODEL ====================

    private static class Message {
        final String sender;
        final String text;
        final LocalDateTime time;

        Message(String sender, String text, LocalDateTime time) {
            this.sender = sender;
            this.text = text;
            this.time = time;
        }

        String timeFormatted() {
            return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }
}
