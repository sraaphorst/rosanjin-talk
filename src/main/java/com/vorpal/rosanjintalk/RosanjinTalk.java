package com.vorpal.rosanjintalk;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.editor.EditorController;
import com.vorpal.rosanjintalk.controller.management.ManagementController;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.shared.Shared;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RosanjinTalk extends Application {
    private static final int WIDTH = 650;
    private static final int HEIGHT = 500;
    private static final KeyCombination CLOSE_KEYS = KeyCodeCombination.valueOf("Shortcut+W");

    /**
     * Store the stage statically so that we can call static methods in this class to show
     * other elements of the stage.
     * @param stage the primary stage for this application
     */
    @Override
    public void start(final Stage stage) {
        stage.setTitle(Shared.TITLE);
        showManagement(stage);
    }

    /**
     * Static call to show the Management component, which is essentially the splash screen
     * and Fluke manager.
     * @param stage the primary stage for this application
     */
    public static void showManagement(final Stage stage) {
        final var managementController = new ManagementController(stage);
        managementController.configure();

        final var scene = new Scene(managementController.getView(), WIDTH, HEIGHT);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (CLOSE_KEYS.match(e)) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Static call to show the Editor component with a given Fluke file (null for a new one).
     * @param stage the primary stage for this application
     */
    public static void showEditor(final Stage stage,
                           final Fluke fluke) {
        final var editorController = new EditorController(stage, fluke);
        editorController.configure();

        final var scene = new Scene(editorController.getView(), WIDTH, HEIGHT);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            e.consume();
            if (CLOSE_KEYS.match(e)) {
                e.consume();
                final var confirm = confirmCloseEditor(editorController);
                if (confirm)
                    showManagement(stage);
            }
        });
        stage.setOnCloseRequest(e -> {
            e.consume();
            final var confirm = confirmCloseEditor(editorController);
            if (confirm)
                showManagement(stage);
        });

        stage.setScene(scene);
        stage.show();
    }

    /**
     * If a Fluke file being edited has seen any modifications and has not been saved,
     * before closing the window, request confirmation that changes will be lost.
     * @param editorController the EditorController editing the Fluke file
     * @return true if user opts to close or the saved file is up-to-date, and false otherwise
     */
    private static boolean confirmCloseEditor(final EditorController editorController) {
        if (editorController.isModified()) {
            return Shared.confirmationRequest(
                    "Are you sure you want to close?\n" +
                            "Any changes made will be lost."
            );
        }
        return true;
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
