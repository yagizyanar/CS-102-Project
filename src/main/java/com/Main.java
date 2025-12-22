package com.edutrack;

import java.io.IOException;

import com.edutrack.util.DatabaseManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static Scene scene;
    private static BorderPane mainLayout;
    private static Parent barNode;

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager.initialize();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/edutrack/view/mainLogin.fxml"));
            Parent root = fxmlLoader.load();
            scene = new Scene(root, 1280, 800);
            stage.setTitle("EduTrack");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            System.out.println("Could not load Login.fxml yet. Please ensure view is created.");
            e.printStackTrace();
        }
    }

    public static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/edutrack/view/" + fxml + ".fxml"));
        scene.setRoot(fxmlLoader.load());
    }

    public static void showMainLayout(String defaultContent) throws IOException {
        if (barNode == null) {
            FXMLLoader barLoader = new FXMLLoader(Main.class.getResource("/com/edutrack/view/bar.fxml"));
            barNode = barLoader.load();
        }

        mainLayout = new BorderPane();
        mainLayout.setTop(barNode);

        FXMLLoader contentLoader = new FXMLLoader(
                Main.class.getResource("/com/edutrack/view/" + defaultContent + ".fxml"));
        mainLayout.setCenter(contentLoader.load());

        scene.setRoot(mainLayout);
    }

    public static void setContent(String fxml) throws IOException {
        if (mainLayout == null) {
            showMainLayout(fxml);
            return;
        }

        FXMLLoader contentLoader = new FXMLLoader(Main.class.getResource("/com/edutrack/view/" + fxml + ".fxml"));
        mainLayout.setCenter(contentLoader.load());
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}