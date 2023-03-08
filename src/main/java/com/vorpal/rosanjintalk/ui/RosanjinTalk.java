package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RosanjinTalk extends Application {
    @Override
    public void start(final Stage stage) {
        stage.setTitle(Shared.TITLE);

        final var root = new EditorPane(stage);
        final var scene = new Scene(root, EditorPane.width(), EditorPane.height());

        final var closeKeys = KeyCodeCombination.valueOf("Shortcut+W");
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (closeKeys.match(e)) {
                stage.close();
                e.consume();
            }
        });

//        final var root = new SplashScreen(stage);
//        final var scene = new Scene(root, 400, 500);

        stage.setScene(scene);

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    /**
     * Get the path in which the fluke files are stored.
     * @return Path representing the path where fluke files are stored.
     */
    public static Path getFlukePath() {
        try {
            final URI location = RosanjinTalk.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();
            final var path = Paths.get(location).toAbsolutePath().getParent().getParent().resolve("flukes");
            final var file = path.toFile();

            // Create the path if it doesn't exist.
            // The directory is a file instead of a path?
            if (file.isFile())
                Shared.unrecoverableError("The directory:\n\n" + file.getPath() +
                        "\n\nthat is supposed to contain fluke files\n" +
                        "is not a directory.");

            if (!file.exists() && !file.mkdir())
                Shared.unrecoverableError ("The directory:\n\n" + file.getPath() +
                        "\n\nthat is supposed to contain fluke files\n" +
                        "could not be created.");

            return path;
        } catch (URISyntaxException ex) {
            Shared.unrecoverableError("Could not determine path of running application.");
            return null;
        }
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
