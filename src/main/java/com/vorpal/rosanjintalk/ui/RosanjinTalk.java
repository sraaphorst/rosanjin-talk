package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.
// https://bell-sw.com/pages/downloads/#/java-8-lts

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

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
     * @return A String representing the path where fluke files are stored.
     * @throws URISyntaxException if an error occurs trying to get the path
     */
    public static File getPath() throws URISyntaxException {
        final URI location = RosanjinTalk.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI();
        return new File(location).getParentFile();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
