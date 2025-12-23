package com.edutrack.controller;

import java.io.File;
import java.io.InputStream;

import com.edutrack.dao.UserDAO;
import com.edutrack.model.User;
import com.edutrack.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ProfileController {

    // ----- Main profile UI -----
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblUniversity;
    @FXML
    private Label lblMajor;
    @FXML
    private Label lblClasses;

    @FXML
    private Label lblLevel;
    @FXML
    private Label lblXpText;
    @FXML
    private ProgressBar xpBar;

    @FXML
    private TextArea txtBio;
    @FXML
    private TextArea txtNotes;
    @FXML
    private ImageView imgProfile;

    // ----- Overlays -----
    @FXML
    private Pane editInfoOverlay;
    @FXML
    private Pane editBioOverlay;
    @FXML
    private Pane badgesOverlay;

    // ----- Overlay Controllers -----
    @FXML
    private EditInfoController editInfoOverlayController;
    @FXML
    private BadgesController badgesOverlayController;

    // ----- Edit info fields (only injected if fx:id matches) -----
    @FXML
    private TextField editUsernameField;
    @FXML
    private TextField editUniversityField;
    @FXML
    private TextField editMajorField;

    @FXML
    private TextField editAddClassField;
    @FXML
    private ListView<String> editClassesList;

    // keep last avatar selection
    private String selectedAvatarPath = null;

    @FXML
    public void initialize() {
        if (editInfoOverlay != null)
            editInfoOverlay.setVisible(false);
        if (editBioOverlay != null)
            editBioOverlay.setVisible(false);
        if (badgesOverlay != null)
            badgesOverlay.setVisible(false);
        refresh();
    }


    @FXML
    private void refresh() {
        User user = SessionManager.getCurrentUser();
        if (user == null)
            return;

        if (lblUsername != null)
            lblUsername.setText(nz(user.getUsername()));
        if (lblEmail != null)
            lblEmail.setText(nz(user.getEmail()));

        try {
            if (lblUniversity != null)
                lblUniversity.setText(nz(user.getUniversity()));
            if (lblMajor != null)
                lblMajor.setText(nz(user.getMajor()));
            if (lblClasses != null)
                lblClasses.setText(nz(user.getClassesText()));
            if (txtBio != null)
                txtBio.setText(nz(user.getBio()));
            if (txtNotes != null)
                txtNotes.setText(nz(user.getNotes()));

            int xp = user.getXp();
            int next = user.getNextLevelXp();

            if (lblLevel != null)
                lblLevel.setText("LVL " + user.getLevel());
            if (lblXpText != null)
                lblXpText.setText(xp + " / " + next);
            if (xpBar != null)
                xpBar.setProgress(next <= 0 ? 0 : Math.min(1.0, xp / (double) next));
        } catch (Throwable ignored) {
        }

        updateProfileImage(user.getProfilePicture());
    }

    @FXML
    private void saveProfile() {
        User user = SessionManager.getCurrentUser();
        if (user == null)
            return;

        if (txtBio != null)
            user.setBio(txtBio.getText());
        if (txtNotes != null)
            user.setNotes(txtNotes.getText());

        new UserDAO().updateProfile(user);
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    private void updateProfileImage(String path) {
        if (imgProfile == null)
            return;
        if (path == null || path.isBlank())
            return;

        String resourcePath = path;
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }

        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is != null) {
            imgProfile.setImage(new Image(is));
            return;
        }

        // file path
        File f = new File(path);
        if (f.exists())
            imgProfile.setImage(new Image(f.toURI().toString()));
    }


    @FXML
    private void openEditInfo() {
        if (editInfoOverlay == null || editInfoOverlayController == null)
            return;

        User user = SessionManager.getCurrentUser();
        if (user == null)
            return;

        // Set initial values
        editInfoOverlayController.setInitialValues(
            user.getUsername(), 
            user.getUniversity(), 
            user.getMajor(), 
            user.getClassesText()
        );

        // Set up close callback
        editInfoOverlayController.setOnClose(() -> {
            editInfoOverlay.setVisible(false);
            refresh();
        });

        // Set up save callback
        editInfoOverlayController.setOnSave(update -> {
            if (update.username != null && !update.username.isBlank())
                user.setUsername(update.username);
            if (update.university != null && !update.university.isBlank())
                user.setUniversity(update.university);
            if (update.major != null && !update.major.isBlank())
                user.setMajor(update.major);

            if (update.selectedClass != null && !update.selectedClass.isBlank()) {
                user.addClass(update.selectedClass);
            }

            // Handle avatar selection
            if (update.avatarResource != null && !update.avatarResource.isBlank()) {
                user.setProfilePicture(update.avatarResource);
            }

            // Handle file upload
            if (update.uploadFile != null) {
                user.setProfilePicture(update.uploadFile.getAbsolutePath());
            }

            // Save to database
            new UserDAO().updateProfile(user);
            refresh();
        });

        // Show the overlay
        editInfoOverlay.setVisible(true);
    }

    @FXML
    private void closeEditInfo() {
        if (editInfoOverlay != null)
            editInfoOverlay.setVisible(false);
        refresh();
    }

    @FXML
    private void saveEditInfo() {
        User user = SessionManager.getCurrentUser();
        if (user == null)
            return;

        safeSetText(editUsernameField, user::setUsername);
        safeSetText(editUniversityField, user::setUniversity);
        safeSetText(editMajorField, user::setMajor);

        if (selectedAvatarPath != null && !selectedAvatarPath.isBlank()) {
            user.setProfilePicture(selectedAvatarPath);
        }

        closeEditInfo();
    }

    private void safeSetText(TextField field, java.util.function.Consumer<String> setter) {
        if (field == null || setter == null)
            return;
        String v = field.getText();
        if (v != null && !v.trim().isEmpty()) {
            setter.accept(v.trim());
        }
    }

    @FXML
    private void tickButton() {
        saveEditInfo();
    }

    @FXML
    private void closeButton() {
        closeEditInfo();
    }

    @FXML
    private void closePopup() {
        closeEditInfo();
    }


    @FXML
    private void openEditBio() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/edutrack/view/profileEditBio.fxml"));

            javafx.scene.layout.Pane pane = loader.load();
            EditBioController controller = loader.getController();

            User user = SessionManager.getCurrentUser();
            if (user != null) {
                controller.setInitialText(user.getBio());
            }

            controller.setOnClose(() -> {
                if (editBioOverlay != null) {
                    editBioOverlay.getChildren().clear();
                    editBioOverlay.setVisible(false);
                }
                refresh();
            });

            controller.setOnSave(bioText -> {
                if (user != null) {
                    user.setBio(bioText);
                    new UserDAO().updateProfile(user);
                }
            });

            if (editBioOverlay != null) {
                editBioOverlay.getChildren().setAll(pane);
                editBioOverlay.setVisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeEditBio() {
        if (editBioOverlay != null) {
            editBioOverlay.getChildren().clear();
            editBioOverlay.setVisible(false);
        }
        refresh();
    }


    @FXML
    private void openBadges() {
        if (badgesOverlay == null || badgesOverlayController == null)
            return;

        // Set up close callback
        badgesOverlayController.setOnClose(() -> {
            badgesOverlay.setVisible(false);
        });

        // You can set badges here if you have them
        // For example: badgesOverlayController.setBadges(userBadgesList);

        // Show the overlay
        badgesOverlay.setVisible(true);
    }

    @FXML
    private void closeBadges() {
        if (badgesOverlay != null)
            badgesOverlay.setVisible(false);
    }


    @FXML
    private void addClassToUser() {
        User user = SessionManager.getCurrentUser();
        if (user == null || editAddClassField == null)
            return;

        String c = editAddClassField.getText();
        if (c == null || c.isBlank())
            return;

        user.addClass(c.trim());
        editAddClassField.clear();

        if (editClassesList != null)
            editClassesList.getItems().setAll(user.getClassesList());
        refresh();
    }

    @FXML
    private void removeSelectedClass() {
        if (editClassesList == null)
            return;
        String sel = editClassesList.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;

        SessionManager.getCurrentUser().removeClass(sel);
        editClassesList.getItems().setAll(SessionManager.getCurrentUser().getClassesList());
        refresh();
    }


    private void selectAvatar(String resourcePath) {
        selectedAvatarPath = resourcePath;

        updateProfileImage(resourcePath);
    }

    @FXML
    private void selectAvatar1() {
        selectAvatar("project/fxml/avatar1.png");
    }

    @FXML
    private void selectAvatar2() {
        selectAvatar("project/fxml/avatar2.png");
    }

    @FXML
    private void selectAvatar3() {
        selectAvatar("project/fxml/avatar3.png");
    }

    @FXML
    private void selectAvatar4() {
        selectAvatar("project/fxml/avatar4.png");
    }

    @FXML
    private void selectAvatar5() {
        selectAvatar("project/fxml/avatar5.png");
    }

    @FXML
    private void selectAvatar6() {
        selectAvatar("project/fxml/avatar6.png");
    }

    @FXML
    private void selectAvatar7() {
        selectAvatar("project/fxml/avatar7.png");
    }

    @FXML
    private void selectAvatar8() {
        selectAvatar("project/fxml/avatar8.png");
    }

    @FXML
    private void selectAvatar9() {
        selectAvatar("project/fxml/avatar9.png");
    }

    @FXML
    private void selectAvatar10() {
        selectAvatar("project/fxml/avatar10.png");
    }

    @FXML
    private void selectAvatar11() {
        selectAvatar("project/fxml/avatar11.png");
    }

    @FXML
    private void selectAvatar12() {
        selectAvatar("project/fxml/avatar12.png");
    }

}
