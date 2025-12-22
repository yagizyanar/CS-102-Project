package com.edutrack.controller;

import com.edutrack.dao.UserDAO;
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
import com.edutrack.model.User;

import java.io.File;
import java.io.InputStream;

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

    // ===================== Refresh UI =====================

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

        // Ensure proper path format for resource stream
        String resourcePath = path;
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }

        // resource avatar
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

    // ===================== Edit Info Overlay =====================

    @FXML

    private void openEditInfo() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/project/fxml/profileEditInfo.fxml") // <-- your edit info fxml name
            );

            Pane pane = loader.load();
            EditInfoController c = loader.getController();

            User user = SessionManager.getCurrentUser();

            // initial values
            c.setInitialValues(user.getUsername(), user.getUniversity(), user.getMajor(), user.getClassesText());

            // ✅ CLOSE callback
            c.setOnClose(() -> {
                // Hide overlay container or remove pane from it
                editInfoOverlay.getChildren().clear();
                editInfoOverlay.setVisible(false);
                refresh();
            });

            // ✅ SAVE callback (empty values keep old)
            c.setOnSave(update -> {
                if (update.username != null)
                    user.setUsername(update.username);
                if (update.university != null)
                    user.setUniversity(update.university);
                if (update.major != null)
                    user.setMajor(update.major);

                // class (if you want multiple classes, call addClass)
                if (update.selectedClass != null && !update.selectedClass.isBlank()) {
                    user.addClass(update.selectedClass);
                }

                // avatar
                if (update.avatarResource != null && !update.avatarResource.isBlank()) {
                    user.setProfilePicture(update.avatarResource);
                }

                // upload file path
                if (update.uploadFile != null) {
                    user.setProfilePicture(update.uploadFile.getAbsolutePath());
                }
            });

            // show inside overlay pane
            editInfoOverlay.getChildren().setAll(pane);
            editInfoOverlay.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeEditInfo() {
        if (editInfoOverlay != null)
            editInfoOverlay.setVisible(false);
        refresh();
    }

    /**
     * ✅ Tick button should call this (or call tickButton which calls this).
     * ✅ Keeps old values if text fields are empty.
     * ✅ Applies avatar if selected.
     * ✅ Closes overlay (goes back to profile page).
     */
    @FXML
    private void saveEditInfo() {
        User user = SessionManager.getCurrentUser();
        if (user == null)
            return;

        // Only update if NOT blank (otherwise keep old)
        safeSetText(editUsernameField, user::setUsername);
        safeSetText(editUniversityField, user::setUniversity);
        safeSetText(editMajorField, user::setMajor);

        // Apply avatar if selected
        if (selectedAvatarPath != null && !selectedAvatarPath.isBlank()) {
            user.setProfilePicture(selectedAvatarPath);
        }

        // Close overlay and refresh profile UI
        closeEditInfo();
    }

    private void safeSetText(TextField field, java.util.function.Consumer<String> setter) {
        if (field == null || setter == null)
            return;
        String v = field.getText();
        if (v != null && !v.trim().isEmpty()) {
            setter.accept(v.trim());
        }
        // else: do nothing -> keep old value
    }

    // Aliases in case your FXML uses these names
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

    // ===================== Edit Bio (because your FXML calls it)
    // =====================

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

            // Set close callback
            controller.setOnClose(() -> {
                if (editBioOverlay != null) {
                    editBioOverlay.getChildren().clear();
                    editBioOverlay.setVisible(false);
                }
                refresh();
            });

            // Set save callback - save to user and database
            controller.setOnSave(bioText -> {
                if (user != null) {
                    user.setBio(bioText);
                    new UserDAO().updateProfile(user);
                }
            });

            // Show in overlay
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

    // ===================== Badges (optional) =====================

    @FXML
    private void openBadges() {
        if (badgesOverlay != null)
            badgesOverlay.setVisible(true);
    }

    @FXML
    private void closeBadges() {
        if (badgesOverlay != null)
            badgesOverlay.setVisible(false);
    }

    // ===================== Multi-class (optional) =====================

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

    // ===================== 12 avatar handlers =====================
    // IMPORTANT:
    // - These set selectedAvatarPath (so tick applies it)
    // - They ALSO preview immediately by updating the ImageView

    private void selectAvatar(String resourcePath) {
        selectedAvatarPath = resourcePath;

        // preview instantly
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
