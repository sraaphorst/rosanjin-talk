package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.
// https://bell-sw.com/pages/downloads/#/java-8-lts

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    public static void main(final String[] args) {
        launch(args);
    }
}
