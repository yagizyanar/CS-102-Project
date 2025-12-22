package com.edutrack.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class EditInfoController {

    @FXML
    private Pane root;

    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtUniversity;
    @FXML
    private TextField txtMajor;
    @FXML
    private ComboBox<String> cmbClasses;
    @FXML
    private TextArea txtInfoNotes;

    @FXML
    private ImageView avatar1;
    @FXML
    private ImageView avatar2;
    @FXML
    private ImageView avatar3;
    @FXML
    private ImageView avatar4;
    @FXML
    private ImageView avatar5;
    @FXML
    private ImageView avatar6;
    @FXML
    private ImageView avatar7;
    @FXML
    private ImageView avatar8;
    @FXML
    private ImageView avatar9;
    @FXML
    private ImageView avatar10;
    @FXML
    private ImageView avatar11;
    @FXML
    private ImageView avatar12;

    private final List<ImageView> avatarViews = new ArrayList<>();
    private Runnable onClose;
    private Consumer<InfoUpdate> onSave;

    private String selectedAvatarResource = null;
    private File selectedUploadFile = null;

    public static class InfoUpdate {
        public final String username;
        public final String university;
        public final String major;
        public final String selectedClass;
        public final String avatarResource;
        public final File uploadFile;

        public InfoUpdate(String username, String university, String major,
                String selectedClass, String avatarResource, File uploadFile) {
            this.username = username;
            this.university = university;
            this.major = major;
            this.selectedClass = selectedClass;
            this.avatarResource = avatarResource;
            this.uploadFile = uploadFile;
        }
    }

    @FXML
    public void initialize() {
        if (cmbClasses != null && cmbClasses.getItems().isEmpty()) {
            cmbClasses.getItems().addAll("CS102", "CS202", "MATH102");
        }

        addAvatar(avatar1, "com/edutrack/view/images/avatar1.png");
        addAvatar(avatar2, "com/edutrack/view/images/avatar2.png");
        addAvatar(avatar3, "com/edutrack/view/images/avatar3.png");
        addAvatar(avatar4, "com/edutrack/view/images/avatar4.png");
        addAvatar(avatar5, "com/edutrack/view/images/avatar5.png");
        addAvatar(avatar6, "com/edutrack/view/images/avatar6.png");
        addAvatar(avatar7, "com/edutrack/view/images/avatar7.png");
        addAvatar(avatar8, "com/edutrack/view/images/avatar8.png");
        addAvatar(avatar9, "com/edutrack/view/images/avatar9.png");
        addAvatar(avatar10, "com/edutrack/view/images/avatar10.png");
        addAvatar(avatar11, "com/edutrack/view/images/avatar11.png");
        addAvatar(avatar12, "com/edutrack/view/images/avatar12.png");
    }

    private void addAvatar(ImageView iv, String resourcePath) {
        if (iv == null)
            return;
        avatarViews.add(iv);

        iv.setOnMouseClicked(e -> selectAvatar(resourcePath));
    }

    private void selectAvatar(String resourcePath) {
        selectedAvatarResource = resourcePath;

        for (ImageView v : avatarViews) {
            if (v != null)
                v.setStyle("");
        }
        for (ImageView v : avatarViews) {
            if (v == null)
                continue;
        }
    }

    @FXML
    private void avatarClicked(MouseEvent e) {
        Object src = e.getSource();
        if (src == avatar1)
            selectAvatar("project/fxml/avatar1.png");
        else if (src == avatar2)
            selectAvatar("project/fxml/avatar2.png");
        else if (src == avatar3)
            selectAvatar("project/fxml/avatar3.png");
        else if (src == avatar4)
            selectAvatar("project/fxml/avatar4.png");
        else if (src == avatar5)
            selectAvatar("project/fxml/avatar5.png");
        else if (src == avatar6)
            selectAvatar("project/fxml/avatar6.png");
        else if (src == avatar7)
            selectAvatar("project/fxml/avatar7.png");
        else if (src == avatar8)
            selectAvatar("project/fxml/avatar8.png");
        else if (src == avatar9)
            selectAvatar("project/fxml/avatar9.png");
        else if (src == avatar10)
            selectAvatar("project/fxml/avatar10.png");
        else if (src == avatar11)
            selectAvatar("project/fxml/avatar11.png");
        else if (src == avatar12)
            selectAvatar("project/fxml/avatar12.png");
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setOnSave(Consumer<InfoUpdate> onSave) {
        this.onSave = onSave;
    }

    public void setInitialValues(String username, String university, String major, String classesText) {
        if (txtUsername != null)
            txtUsername.setText(username == null ? "" : username);
        if (txtUniversity != null)
            txtUniversity.setText(university == null ? "" : university);
        if (txtMajor != null)
            txtMajor.setText(major == null ? "" : major);
    }

    @FXML
    private void upload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a profile picture");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        Node anyNode = txtUsername != null ? txtUsername : (root != null ? root : null);
        if (anyNode == null || anyNode.getScene() == null)
            return;

        File file = chooser.showOpenDialog(anyNode.getScene().getWindow());
        if (file != null)
            selectedUploadFile = file;
    }

    @FXML
    private void save() {
        String u = cleanOrNull(txtUsername);
        String uni = cleanOrNull(txtUniversity);
        String maj = cleanOrNull(txtMajor);

        String selectedClass = (cmbClasses != null) ? cmbClasses.getValue() : null;

        if (onSave != null) {
            onSave.accept(new InfoUpdate(u, uni, maj, selectedClass, selectedAvatarResource, selectedUploadFile));
        }

        close(); 
    }

    private String cleanOrNull(TextField tf) {
        if (tf == null)
            return null;
        String t = tf.getText();
        if (t == null)
            return null;
        t = t.trim();
        return t.isEmpty() ? null : t;
    }

    @FXML
    private void close() {
        if (onClose != null)
            onClose.run();
        else if (root != null)
            root.setVisible(false);
    }
}
