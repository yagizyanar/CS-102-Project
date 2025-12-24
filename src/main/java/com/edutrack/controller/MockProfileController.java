package com.edutrack.controller;

import java.util.List;

import com.edutrack.dao.BadgeDAO;
import com.edutrack.dao.UserDAO;
import com.edutrack.model.Badge;
import com.edutrack.model.User;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class MockProfileController {

    @FXML
    private AnchorPane mockProfileRoot;
    @FXML
    private Circle profileCircle;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblLevel;
    @FXML
    private Label lblMajor;
    @FXML
    private Label lblUniversity;
    @FXML
    private Label lblClasses;
    @FXML
    private FlowPane badgesContainer;

    private User targetUser;
    private final UserDAO userDAO = new UserDAO();
    private final BadgeDAO badgeDAO = new BadgeDAO();

    @FXML
    private void initialize() {
        // Will be set by setUser method
    }

    public void setUser(User user) {
        this.targetUser = user;
        loadUserProfile();
    }

    public void setUserByUsername(String username) {
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            setUser(user);
        }
    }


    public void setUserById(int userId) {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            setUser(user);
        }
    }

    private void loadUserProfile() {
        if (targetUser == null) {
            lblUsername.setText("User not found");
            return;
        }

        // Username
        lblUsername.setText(targetUser.getUsername());

        // Level
        int level = targetUser.getLevel();
        lblLevel.setText("Level " + level);

        // Profile Picture
        loadProfileImage();

        // Major
        String major = targetUser.getMajor();
        if (major != null && !major.trim().isEmpty()) {
            lblMajor.setText(major);
        } else {
            lblMajor.setText("Not specified");
            lblMajor.setStyle("-fx-text-fill: #999999; -fx-font-style: italic;");
        }

        // University
        String university = targetUser.getUniversity();
        if (university != null && !university.trim().isEmpty()) {
            lblUniversity.setText(university);
        } else {
            lblUniversity.setText("Not specified");
            lblUniversity.setStyle("-fx-text-fill: #999999; -fx-font-style: italic;");
        }

        // Classes
        String classes = targetUser.getClassesText();
        if (classes != null && !classes.trim().isEmpty()) {
            lblClasses.setText(classes);
        } else {
            lblClasses.setText("No classes");
            lblClasses.setStyle("-fx-text-fill: #999999; -fx-font-style: italic;");
        }

        // Load badges
        loadBadges();
    }

    private void loadProfileImage() {
        try {
            String imagePath = targetUser.getProfilePicture();
            if (imagePath == null || imagePath.isEmpty()) {
                imagePath = "/com/edutrack/view/avatar1.png";
            }

            // Make sure path starts with /
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }

            java.io.InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                Image image = new Image(imageStream);
                if (!image.isError()) {
                    profileCircle.setFill(new ImagePattern(image));
                } else {
                    // Fallback to default avatar
                    imageStream = getClass().getResourceAsStream("/com/edutrack/view/avatar1.png");
                    if (imageStream != null) {
                        profileCircle.setFill(new ImagePattern(new Image(imageStream)));
                    } else {
                        profileCircle.setFill(Color.web("#E0E0E0"));
                    }
                }
            } else {
                // Fallback to default avatar
                imageStream = getClass().getResourceAsStream("/com/edutrack/view/avatar1.png");
                if (imageStream != null) {
                    profileCircle.setFill(new ImagePattern(new Image(imageStream)));
                } else {
                    profileCircle.setFill(Color.web("#E0E0E0"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading profile image: " + e.getMessage());
            e.printStackTrace();
            // Final fallback
            profileCircle.setFill(Color.web("#E0E0E0"));
        }
    }

    private void loadBadges() {
        badgesContainer.getChildren().clear();

        List<Badge> badges = badgeDAO.getUserBadges(targetUser.getId());

        if (badges.isEmpty()) {
            Label noBadges = new Label("No badges earned yet");
            noBadges.setStyle("-fx-text-fill: #999999; -fx-font-style: italic; -fx-font-size: 13px;");
            badgesContainer.getChildren().add(noBadges);
        } else {
            for (Badge badge : badges) {
                badgesContainer.getChildren().add(createBadgeCard(badge));
            }
        }
    }

    private VBox createBadgeCard(Badge badge) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 10; " +
                     "-fx-padding: 10; " +
                     "-fx-border-color: #e0e0e0; " +
                     "-fx-border-radius: 10; " +
                     "-fx-border-width: 1;");
        card.setPrefWidth(80);

        // Badge icon/emoji
        Label icon = new Label(getBadgeIcon(badge));
        icon.setStyle("-fx-font-size: 24px;");

        // Badge name
        Label name = new Label(badge.getName());
        name.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666; -fx-font-weight: bold;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(70);

        card.getChildren().addAll(icon, name);
        return card;
    }

    private String getBadgeIcon(Badge badge) {
        // Return icon based on badge category or name
        Badge.BadgeCategory category = badge.getCategory();
        if (category == null) return "🎖️";

        switch (category) {
            case PRODUCTIVITY:
                return "📚";
            case STREAK:
                return "🔥";
            case SOCIAL:
                return "👥";
            case COURSE:
                return "✅";
            case XP:
                return "🏆";
            case SPECIAL:
                return "⭐";
            default:
                return "🎖️";
        }
    }

    @FXML
    private void closeProfile() {
        if (mockProfileRoot != null && mockProfileRoot.getParent() instanceof Pane parent) {
            parent.getChildren().remove(mockProfileRoot);
        }
    }
}