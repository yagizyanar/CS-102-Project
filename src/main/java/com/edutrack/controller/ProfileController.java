package com.edutrack.controller;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.edutrack.dao.UserDAO;
import com.edutrack.model.Badge;
import com.edutrack.model.User;
import com.edutrack.util.BadgeService;
import com.edutrack.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
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
    
    // ----- Badge UI -----
    @FXML
    private FlowPane badgesTop3Pane;
    @FXML
    private Label lblMoreBadgesCount;

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
        
        // Auto-save notes when text changes
        if (txtNotes != null) {
            txtNotes.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) { // Lost focus
                    saveNotes();
                }
            });
        }
        
        // Check and award badges
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            BadgeService.checkAndAwardBadges(user);
        }
        
        refresh();
        loadBadges();
    }

    private void loadBadges() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;
        
        List<Badge> userBadges = BadgeService.getUserBadges(user.getId());
        
        // Show top 3 badges
        if (badgesTop3Pane != null) {
            badgesTop3Pane.getChildren().clear();
            int count = 0;
            for (Badge badge : userBadges) {
                if (count >= 3) break;
                ImageView iv = createBadgeIcon(badge);
                badgesTop3Pane.getChildren().add(iv);
                count++;
            }
            
            if (count == 0) {
                Label noBadges = new Label("No badges yet");
                noBadges.setStyle("-fx-text-fill: #888; -fx-font-size: 12;");
                badgesTop3Pane.getChildren().add(noBadges);
            }
        }
        
        // Update count label
        if (lblMoreBadgesCount != null) {
            int remaining = Math.max(0, userBadges.size() - 3);
            lblMoreBadgesCount.setText("+" + remaining + " more");
        }
    }

    private ImageView createBadgeIcon(Badge badge) {
        ImageView iv = new ImageView();
        iv.setFitWidth(48);
        iv.setFitHeight(48);
        iv.setPreserveRatio(true);
        
        String path = badge.getIconPath();
        if (path != null) {
            if (!path.startsWith("/")) path = "/" + path;
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                iv.setImage(new Image(is));
            }
        }
        
        Tooltip.install(iv, new Tooltip(badge.getName() + "\n" + badge.getDescription()));
        return iv;
    }

    private void saveNotes() {
        User user = SessionManager.getCurrentUser();
        if (user == null || txtNotes == null) return;
        user.setNotes(txtNotes.getText());
        new UserDAO().updateProfile(user);
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
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/edutrack/view/profileEditInfo.fxml") 
            );

            Pane pane = loader.load();
            EditInfoController c = loader.getController();

            User user = SessionManager.getCurrentUser();

            c.setInitialValues(user.getUsername(), user.getUniversity(), user.getMajor(), user.getClassesText());

            c.setOnClose(() -> {
                editInfoOverlay.getChildren().clear();
                editInfoOverlay.setVisible(false);
                refresh();
            });

            c.setOnSave(update -> {
                if (update.username != null)
                    user.setUsername(update.username);
                if (update.university != null)
                    user.setUniversity(update.university);
                if (update.major != null)
                    user.setMajor(update.major);

                if (update.selectedClass != null && !update.selectedClass.isBlank()) {
                    user.addClass(update.selectedClass);
                }

                if (update.avatarResource != null && !update.avatarResource.isBlank()) {
                    user.setProfilePicture(update.avatarResource);
                }

                if (update.uploadFile != null) {
                    user.setProfilePicture(update.uploadFile.getAbsolutePath());
                }
                
                // Save to database
                new UserDAO().updateProfile(user);
            });

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
        if (badgesOverlay != null)
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
        selectAvatar("com/edutrack/view/avatar1.png");
    }

    @FXML
    private void selectAvatar2() {
        selectAvatar("com/edutrack/view/avatar2.png");
    }

    @FXML
    private void selectAvatar3() {
        selectAvatar("com/edutrack/view/avatar3.png");
    }

    @FXML
    private void selectAvatar4() {
        selectAvatar("com/edutrack/view/avatar4.png");
    }

    @FXML
    private void selectAvatar5() {
        selectAvatar("com/edutrack/view/avatar5.png");
    }

    @FXML
    private void selectAvatar6() {
        selectAvatar("com/edutrack/view/avatar6.png");
    }

    @FXML
    private void selectAvatar7() {
        selectAvatar("com/edutrack/view/avatar7.png");
    }

    @FXML
    private void selectAvatar8() {
        selectAvatar("com/edutrack/view/avatar8.png");
    }

    @FXML
    private void selectAvatar9() {
        selectAvatar("com/edutrack/view/avatar9.png");
    }

    @FXML
    private void selectAvatar10() {
        selectAvatar("com/edutrack/view/avatar10.png");
    }

    @FXML
    private void selectAvatar11() {
        selectAvatar("com/edutrack/view/avatar11.png");
    }

    @FXML
    private void selectAvatar12() {
        selectAvatar("com/edutrack/view/avatar12.png");
    }

}
