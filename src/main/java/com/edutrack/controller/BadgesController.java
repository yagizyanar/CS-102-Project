package com.edutrack.controller;

import java.io.InputStream;
import java.util.List;

import com.edutrack.model.Badge;

import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class BadgesController {

    @FXML private FlowPane badgesGrid;

    private Runnable onClose;

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setBadges(List<Badge> badges) {
        if (badgesGrid == null) return;
        badgesGrid.getChildren().clear();
        if (badges == null) return;

        for (Badge b : badges) {
            badgesGrid.getChildren().add(createBadgeNode(b));
        }
    }

    private ImageView createBadgeNode(Badge badge) {
        ImageView iv = new ImageView();
        iv.setFitWidth(56);
        iv.setFitHeight(56);
        iv.setPreserveRatio(true);

        Image img = tryLoadImage(badge.getIconPath());
        if (img != null) iv.setImage(img);

        Tooltip.install(iv, new Tooltip(badge.getName() + "\n" + badge.getDescription()));
        return iv;
    }

    private Image tryLoadImage(String path) {
        if (path == null) return null;
        InputStream is = getClass().getResourceAsStream("/" + path);
        if (is == null) return null;
        return new Image(is);
    }

    @FXML
    private void close() {
        if (onClose != null) onClose.run();
    }

}
