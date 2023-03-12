package com.vorpal.rosanjintalk;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.ManagementController;
import com.vorpal.rosanjintalk.ui.Shared;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RosanjinTalk extends Application {
    @Override
    public void start(final Stage stage) {
        stage.setTitle(Shared.TITLE);

//        final var root = new EditorPane(stage);
//        final var scene = new Scene(root, EditorPane.width(), EditorPane.height());

        final var root = new ManagementController(stage);
        root.configure();
        final var scene = new Scene(root.getView(), 500, 540);

        final var closeKeys = KeyCodeCombination.valueOf("Shortcut+W");
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (closeKeys.match(e)) {
                stage.close();
                e.consume();
            }
        });

        stage.setScene(scene);

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
