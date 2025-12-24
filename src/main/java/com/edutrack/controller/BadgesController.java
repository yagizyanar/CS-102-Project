package com.edutrack.controller;

import java.io.InputStream;
import java.util.List;

import com.edutrack.model.Badge;
import com.edutrack.model.User;
import com.edutrack.util.BadgeService;
import com.edutrack.util.SessionManager;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BadgesController {

    @FXML private FlowPane badgesGrid;
    @FXML private StackPane badgesOverlayRoot;

    private Runnable onClose;

    @FXML
    public void initialize() {
        loadUserBadges();
    }

    private void loadUserBadges() {
        if (badgesGrid == null) return;
        badgesGrid.getChildren().clear();
        
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            Label noBadges = new Label("Please log in to see badges");
            noBadges.setStyle("-fx-text-fill: #888;");
            badgesGrid.getChildren().add(noBadges);
            return;
        }
        
        List<Badge> userBadges = BadgeService.getUserBadges(user.getId());
        
        if (userBadges.isEmpty()) {
            Label noBadges = new Label("No badges earned yet. Complete tasks and maintain streaks to earn badges!");
            noBadges.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");
            noBadges.setWrapText(true);
            badgesGrid.getChildren().add(noBadges);
        } else {
            for (Badge badge : userBadges) {
                badgesGrid.getChildren().add(createBadgeNode(badge));
            }
        }
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setBadges(List<Badge> badges) {
        if (badgesGrid == null) return;
        badgesGrid.getChildren().clear();
        if (badges == null || badges.isEmpty()) {
            Label noBadges = new Label("No badges yet");
            noBadges.setStyle("-fx-text-fill: #888;");
            badgesGrid.getChildren().add(noBadges);
            return;
        }

        for (Badge b : badges) {
            badgesGrid.getChildren().add(createBadgeNode(b));
        }
    }

    private VBox createBadgeNode(Badge badge) {
        VBox container = new VBox(5);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10;");
        
        ImageView iv = new ImageView();
        iv.setFitWidth(56);
        iv.setFitHeight(56);
        iv.setPreserveRatio(true);

        Image img = tryLoadImage(badge.getIconPath());
        if (img != null) iv.setImage(img);

        Label name = new Label(badge.getName());
        name.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #333;");
        name.setWrapText(true);
        name.setMaxWidth(70);
        name.setAlignment(javafx.geometry.Pos.CENTER);
        
        container.getChildren().addAll(iv, name);
        
        Tooltip.install(container, new Tooltip(badge.getName() + "\n" + badge.getDescription()));
        return container;
    }

    private Image tryLoadImage(String path) {
        if (path == null) return null;
        String resourcePath = path.startsWith("/") ? path : "/" + path;
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) return null;
        return new Image(is);
    }

    @FXML
    private void close() {
        if (badgesOverlayRoot != null) {
            badgesOverlayRoot.setVisible(false);
        }
        if (onClose != null) onClose.run();
    }
}
