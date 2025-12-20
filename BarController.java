package project;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class BarController {

    @FXML
    private void hoverOn(MouseEvent e) {
        Node n = (Node) e.getSource();
        n.setScaleX(1.08);
        n.setScaleY(1.08);
        n.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.35)));
    }

    @FXML
    private void hoverOff(MouseEvent e) {
        Node n = (Node) e.getSource();
        n.setScaleX(1.0);
        n.setScaleY(1.0);
        n.setEffect(null);
    }

    @FXML
    private void goDashboard() {
       
        Main.switchScene("/project/fxml/dashboard.fxml");
    }

    @FXML
    private void goForums() {
   
        Main.switchScene("/project/fxml/forum.fxml");
    }

    @FXML private void goTasks()     { Main.switchScene("/project/fxml/TaskManagement.fxml"); }
    @FXML private void goFriends()   { Main.switchScene("/project/fxml/Friends.fxml"); }
    @FXML private void goPomodoro()  { Main.switchScene("/project/fxml/PomodoroStart.fxml"); }
    @FXML private void goProfile()   { Main.switchScene("/project/fxml/profile.fxml"); }

    @FXML
    private void goNotifications() {
      
        Main.switchScene("/project/fxml/notification.fxml");
    }
}
