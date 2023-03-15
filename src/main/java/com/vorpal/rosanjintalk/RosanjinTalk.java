package com.vorpal.rosanjintalk;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.controller.editor.EditorController;
import com.vorpal.rosanjintalk.controller.management.ManagementController;
import com.vorpal.rosanjintalk.controller.player.PlayerController;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.shared.Shared;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.function.Function;

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
    static void showManagement(final Stage stage) {
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
        setupController(
                editorController,
                stage,
                e -> editorController.isModified()
        );
        stage.show();
    }

    public static void showPlayer(final Stage stage,
                                  final Fluke fluke) {
        final var playerController = new PlayerController(stage, fluke);
        setupController(
                playerController,
                stage,
                e -> true);
        stage.show();
    }

    /**
     * Common code for player and editor controllers.
     * @param controller the Controller to configure
     * @param stage      the Stage to display the controller on
     * @param check      a check to see if we should prompt before closing
     */
    private static void setupController(final Controller<?> controller,
                                        final Stage stage,
                                        final Function<Controller<?>, Boolean> check) {
        controller.configure();

        final var scene = new Scene(controller.getView(), WIDTH, HEIGHT);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            e.consume();
            if (CLOSE_KEYS.match(e)) {
                e.consume();
                final var confirm = confirmCloseEditor(controller, check);
                if (confirm)
                    showManagement(stage);
            }
        });
        stage.setOnCloseRequest(e -> {
            e.consume();
            final var confirm = confirmCloseEditor(controller, check);
            if (confirm)
                showManagement(stage);
        });

        stage.setScene(scene);
    }

    /**
     * If an editor or player is being closed, if necessary, prompt the user
     * whether the pane should be closed.
     * For editors, this involves the Fluke file being modified.
     * For players, we close indiscriminately.
     * @param controller the Controller
     * @return true if user opts to close or check returns false, and false otherwise
     */
    private static boolean confirmCloseEditor(final Controller<?> controller,
                                              final Function<Controller<?>, Boolean> check) {
        if (check.apply(controller)) {
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
