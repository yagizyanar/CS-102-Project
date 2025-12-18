package project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class ForumController {

    @FXML private Label lblTitle;
    @FXML private ComboBox<String> cmbClasses;
    @FXML private VBox messagesBox;
    @FXML private TextField txtMessage;
    @FXML private ScrollPane scrollPane;

    // messages per class (works without database)
    private final Map<String, ObservableList<Message>> storage = new HashMap<>();

    @FXML
    public void initialize() {
        cmbClasses.setItems(FXCollections.observableArrayList("CS102", "CS202", "MATH102"));
        cmbClasses.setValue("CS102");

        for (String c : cmbClasses.getItems()) {
            storage.put(c, FXCollections.observableArrayList());
        }

        // sample messages
        storage.get("CS102").add(new Message("Efe44", "Anyone understood Lab 4?", false));
        storage.get("CS102").add(new Message(Main.getCurrentUser().getUsername(), "Which part are you stuck on?", true));

        render("CS102");
    }

    @FXML
    private void onClassChanged() {
        String cls = cmbClasses.getValue();
        render(cls);
    }

    @FXML
    private void send() {
        String cls = cmbClasses.getValue();
        if (cls == null) return;

        String text = txtMessage.getText();
        if (text == null || text.trim().isEmpty()) return;

        Message m = new Message(Main.getCurrentUser().getUsername(), text.trim(), true);
        storage.get(cls).add(m);

        messagesBox.getChildren().add(bubble(m));
        txtMessage.clear();

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private void render(String cls) {
        if (cls == null) return;

        if (lblTitle != null) lblTitle.setText(cls + " Forum");
        messagesBox.getChildren().clear();

        for (Message m : storage.get(cls)) {
            messagesBox.getChildren().add(bubble(m));
        }

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private HBox bubble(Message m) {
        Label label = new Label(m.sender + ": " + m.text);
        label.setWrapText(true);
        label.setMaxWidth(700);

        VBox card = new VBox(label);
        card.setStyle(m.isMe
                ? "-fx-background-color: #3CB371; -fx-background-radius: 14; -fx-padding: 10;"
                : "-fx-background-color: #E0E0E0; -fx-background-radius: 14; -fx-padding: 10;");

        if (m.isMe) label.setStyle("-fx-text-fill: white;");

        HBox row = new HBox(card);
        row.setAlignment(m.isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return row;
    }

    private static class Message {
        final String sender;
        final String text;
        final boolean isMe;

        Message(String sender, String text, boolean isMe) {
            this.sender = sender;
            this.text = text;
            this.isMe = isMe;
        }
    }
}
