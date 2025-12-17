package project;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.TilePane;
import project.user.User;

public class DashboardController {

    // XP strip
    @FXML private Label lblXpGreeting;
    @FXML private Label lblLevelRange;
    @FXML private Slider lvlSlider;
    @FXML private Label lblXp;

    // Content
    @FXML private TilePane coursesTile;
    @FXML private BarChart<String, Number> progressChart;
    @FXML private ListView<String> todoList;

    // Bottom streak
    @FXML private Label lblStreak;

    private User user;

    private static final int XP_PER_LEVEL = 500;

    @FXML
    public void initialize() {
        user = Main.getCurrentUser();
        lvlSlider.setDisable(true);

        refreshAll();
        seedChart();
        seedTodo();
    }

    private void refreshAll() {
        // Greeting
        lblXpGreeting.setText("Hello, " + user.getUsername());

        // XP
        int xp = user.getXpAmount();
        lblXp.setText("Current: " + xp + " XP");

        int level = (xp / XP_PER_LEVEL) + 1;
        lblLevelRange.setText("LVL " + level + " → LVL " + (level + 1));
        lvlSlider.setValue((xp % XP_PER_LEVEL) * 100.0 / XP_PER_LEVEL);

        // 🔥 SAME streak as bar
        int streak = user.calculateStreak();
        lblStreak.setText(String.valueOf(streak));
    }

    private void seedChart() {
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.getData().add(new XYChart.Data<>("Mon", 2));
        s.getData().add(new XYChart.Data<>("Tue", 4));
        s.getData().add(new XYChart.Data<>("Wed", 3));
        s.getData().add(new XYChart.Data<>("Thu", 5));
        s.getData().add(new XYChart.Data<>("Fri", 1));

        progressChart.getData().clear();
        progressChart.getData().add(s);
    }

    private void seedTodo() {
        todoList.getItems().setAll(
                "Study for exam",
                "Finish assignment",
                "Review lecture notes"
        );
    }
}
