package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.user.User;

import java.io.File;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

public class Main extends Application {

    private static Stage primaryStage;

    private static final User currentUser = new User(1, "Ahmet", "ahmet@edu.com", "1234");

    public static User getCurrentUser() {
        return currentUser;
    }

    private static final Deque<String> history = new ArrayDeque<>();
    private static String currentFxmlPath = null;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("EduTrack");


        switchSceneNoHistory("/project/fxml/profile.fxml");

        primaryStage.show();
    }


    public static void switchScene(String fxmlPath) {
        String target = normalizePath(fxmlPath);

        try {
     
            if (currentFxmlPath != null && !currentFxmlPath.equals(target)) {
                history.push(currentFxmlPath);
            }

            loadAndSetRoot(target);

            currentFxmlPath = target;
            if (primaryStage.getScene() != null) {
                primaryStage.getScene().setUserData(target);
            }

        } catch (Exception e) {
            showLoadError(target, e);
        }
    }

    public static void goBack() {
        if (!history.isEmpty()) {
            String prev = history.pop();
            switchSceneNoHistory(prev);
        }
    }

    private static void switchSceneNoHistory(String fxmlPath) {
        String target = normalizePath(fxmlPath);

        try {
            loadAndSetRoot(target);

            currentFxmlPath = target;
            if (primaryStage.getScene() != null) {
                primaryStage.getScene().setUserData(target);
            }

        } catch (Exception e) {
            showLoadError(target, e);
        }
    }

    private static void loadAndSetRoot(String fxmlPath) throws Exception {
        URL url = resolveFXML(fxmlPath);

        if (url == null) {
            throw new IllegalStateException("FXML not found on classpath or filesystem: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root, 1200, 700));
        } else {
            primaryStage.getScene().setRoot(root);
        }
    }

    /** Show error on screen */
    private static void showLoadError(String fxmlPath, Exception e) {
        e.printStackTrace();

        Label msg = new Label(
                "FXML FAILED TO LOAD:\n" + fxmlPath +
                        "\n\nLook at console stack trace.\n\n" +
                        "Common cause: FXML not in resources output."
        );
        msg.setStyle("-fx-font-size: 16px; -fx-padding: 30px;");
        StackPane fallback = new StackPane(msg);

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(fallback, 1200, 700));
        } else {
            primaryStage.getScene().setRoot(fallback);
        }
    }

    private static URL resolveFXML(String fxmlPath) {
        if (fxmlPath == null || fxmlPath.isBlank()) return null;

        String p = fxmlPath.startsWith("/") ? fxmlPath : "/" + fxmlPath;

        URL url = Main.class.getResource(p);
        if (url != null) return url;

        url = Main.class.getClassLoader().getResource(p.startsWith("/") ? p.substring(1) : p);
        if (url != null) return url;

        File f1 = new File("src" + p);
        if (f1.exists()) {
            try { return f1.toURI().toURL(); } catch (Exception ignored) {}
        }

        File f2 = new File("resources" + p);
        if (f2.exists()) {
            try { return f2.toURI().toURL(); } catch (Exception ignored) {}
        }

        return null;
    }

    private static String normalizePath(String fxmlPath) {
        if (fxmlPath == null) return null;
        return fxmlPath.startsWith("/") ? fxmlPath : "/" + fxmlPath;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
