package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class RosanjinTalk extends Application {
    @Override
    public void start(final Stage stage) {
//        final var root = new CreatePanel();
        final var root = new SplashScreen();
        final var scene = new Scene(root, 400, 500);
        stage.setScene(scene);

        stage.setScene(scene);
        stage.setTitle("RosanjinTalk");
        stage.show();
    }

    /**
     * Get the path in which the fluke files are stored.
     * @return File representing the path where fluke files are stored.
     */
    public static File getFlukePath() {
        try {
            final URI location = RosanjinTalk.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();
            final var path = Paths.get(location).toAbsolutePath().getParent().getParent().resolve("flukes");
            final var repo = path.toFile();

            // Create the path if it doesn't exist.
            // The directory is a file instead of a path?
            if (repo.isFile())
                unrecoverableError("The directory:\n\n" + repo.getPath() +
                        "\n\nthat is supposed to contain fluke files\n" +
                        "is not a directory.");

            if (!repo.exists() && !repo.mkdir())
                unrecoverableError ("The directory:\n\n" + repo.getPath() +
                        "\n\nthat is supposed to contain fluke files\n" +
                        "could not be created.");

            return repo;
        } catch (URISyntaxException ex) {
            unrecoverableError("Could not determine path of running application.");
        }

        // Java compiler needs a return statement even though this point will never be reached.
        return null;
    }

    public static void unrecoverableError(final String text) {
        final var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("RosanjinTalk Error");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
        System.exit(1);
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
