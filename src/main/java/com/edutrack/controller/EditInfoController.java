package com.edutrack.controller;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.edutrack.dao.UserDAO;
import com.edutrack.model.User;
import com.edutrack.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class EditInfoController {

    @FXML private StackPane editInfoOverlayRoot;
    @FXML private TextField txtUsername;
    @FXML private TextField txtUniversity;
    @FXML private TextField txtMajor;
    @FXML private ComboBox<String> cmbClasses;
    @FXML private ImageView imgPreview;

    @FXML private ImageView avatar1, avatar2, avatar3, avatar4, avatar5, avatar6;
    @FXML private ImageView avatar7, avatar8, avatar9, avatar10, avatar11, avatar12;

    private final List<ImageView> avatarViews = new ArrayList<>();
    private Runnable onClose;
    private Consumer<InfoUpdate> onSave;

    private String selectedAvatarResource = null;
    private ImageView selectedAvatarView = null;

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
            cmbClasses.getItems().addAll("CS101", "CS102", "CS201", "CS202", "CS223", "MATH101", "MATH102", "PHYS101", "PHYS102");
        }

        setupAvatar(avatar1, "com/edutrack/view/avatar1.png");
        setupAvatar(avatar2, "com/edutrack/view/avatar2.png");
        setupAvatar(avatar3, "com/edutrack/view/avatar3.png");
        setupAvatar(avatar4, "com/edutrack/view/avatar4.png");
        setupAvatar(avatar5, "com/edutrack/view/avatar5.png");
        setupAvatar(avatar6, "com/edutrack/view/avatar6.png");
        setupAvatar(avatar7, "com/edutrack/view/avatar7.png");
        setupAvatar(avatar8, "com/edutrack/view/avatar8.png");
        setupAvatar(avatar9, "com/edutrack/view/avatar9.png");
        setupAvatar(avatar10, "com/edutrack/view/avatar10.png");
        setupAvatar(avatar11, "com/edutrack/view/avatar11.png");
        setupAvatar(avatar12, "com/edutrack/view/avatar12.png");

        // Load current user's profile picture as preview
        User user = SessionManager.getCurrentUser();
        if (user != null && user.getProfilePicture() != null) {
            loadPreviewImage(user.getProfilePicture());
        }
    }

    private void setupAvatar(ImageView iv, String resourcePath) {
        if (iv == null) return;
        avatarViews.add(iv);
        
        iv.setStyle("-fx-cursor: hand;");
        iv.setOnMouseClicked(e -> selectAvatar(iv, resourcePath));
        iv.setOnMouseEntered(e -> {
            if (iv != selectedAvatarView) {
                iv.setOpacity(0.7);
            }
        });
        iv.setOnMouseExited(e -> {
            if (iv != selectedAvatarView) {
                iv.setOpacity(1.0);
            }
        });
    }

    private void selectAvatar(ImageView clickedView, String resourcePath) {
        selectedAvatarResource = resourcePath;
        
        // Reset all avatar styles
        for (ImageView v : avatarViews) {
            if (v != null) {
                v.setStyle("-fx-cursor: hand;");
                v.setOpacity(1.0);
                v.setScaleX(1.0);
                v.setScaleY(1.0);
            }
        }
        
        // Highlight selected avatar
        selectedAvatarView = clickedView;
        if (clickedView != null) {
            clickedView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, #59B5E0, 10, 0.8, 0, 0);");
            clickedView.setScaleX(1.15);
            clickedView.setScaleY(1.15);
        }
        
        // Update preview image
        loadPreviewImage(resourcePath);
    }

    private void loadPreviewImage(String resourcePath) {
        if (imgPreview == null) return;
        
        try {
            String path = resourcePath;
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                imgPreview.setImage(new Image(is));
            }
        } catch (Exception e) {
            System.out.println("Could not load preview: " + e.getMessage());
        }
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setOnSave(Consumer<InfoUpdate> onSave) {
        this.onSave = onSave;
    }

    public void setInitialValues(String username, String university, String major, String classesText) {
        if (txtUsername != null) txtUsername.setText(username == null ? "" : username);
        if (txtUniversity != null) txtUniversity.setText(university == null ? "" : university);
        if (txtMajor != null) txtMajor.setText(major == null ? "" : major);
    }

    @FXML
    private void save() {
        String u = cleanOrNull(txtUsername);
        String uni = cleanOrNull(txtUniversity);
        String maj = cleanOrNull(txtMajor);
        String selectedClass = (cmbClasses != null) ? cmbClasses.getValue() : null;

        // Update user in session and database
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            if (u != null) user.setUsername(u);
            if (uni != null) user.setUniversity(uni);
            if (maj != null) user.setMajor(maj);
            if (selectedClass != null && !selectedClass.isEmpty()) {
                user.addClass(selectedClass);
            }
            if (selectedAvatarResource != null) {
                user.setProfilePicture(selectedAvatarResource);
            }
            
            // Save to database
            new UserDAO().updateProfile(user);
        }

        if (onSave != null) {
            onSave.accept(new InfoUpdate(u, uni, maj, selectedClass, selectedAvatarResource, null));
        }

        close();
    }

    private String cleanOrNull(TextField tf) {
        if (tf == null) return null;
        String t = tf.getText();
        if (t == null) return null;
        t = t.trim();
        return t.isEmpty() ? null : t;
    }

    @FXML
    private void close() {
        if (editInfoOverlayRoot != null) {
            editInfoOverlayRoot.setVisible(false);
        }
        if (onClose != null) {
            onClose.run();
        }
    }
}
