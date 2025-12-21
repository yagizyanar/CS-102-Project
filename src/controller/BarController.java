package project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class BarController {

    @FXML
    private void hoverOn(MouseEvent e) {
        Button b = (Button) e.getSource();
        b.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #888;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );
    }

    @FXML
    private void hoverOff(MouseEvent e) {
        Button b = (Button) e.getSource();
        b.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );
    }
}
