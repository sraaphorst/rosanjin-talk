package com.vorpal.rosanjintalk.shared;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.RosanjinTalk;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

public final class Shared {
    public static final String TITLE = "RosanjinTalk";
    public static final String WARNING_TITLE = TITLE + " Warning";
    public static final String ERROR_TITLE = TITLE + " Error";
    public static final String CONFIRMATION_TITLE = TITLE + " Confirmation";

    public static final double SPACING = 10;
    public static final double HORIZONTAL_SPACING = 20;
    public static final double VERTICAL_SPACING = 20;
    public static final Insets PADDING = new Insets(SPACING);

    private Shared() {}

    /**
     * Create a standard vertical ScrollPane to manage n.
     * @param n the Node managed by the ScrollPane
     * @return  the configured ScrollPane
     */
    public static ScrollPane createStandardScrollPane(final Node n) {
        final var scrollPane = new ScrollPane(n);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Do not allow horizontal scrolling.
        scrollPane.addEventFilter(ScrollEvent.SCROLL,
                evt -> { if (evt.getDeltaX() != 0) evt.consume(); });

        return scrollPane;
    }

    public enum FileOptions {
        SAVE("Save Fluke"),
        OPEN("Open Fluke");

        final String title;
        FileOptions(final String title) {
            this.title = title;
        }
    }

    public static File fileChooserDialog(final Stage s, final FileOptions fileOptions) {
        final var fileChooser = new FileChooser();
        fileChooser.setTitle(fileOptions.title);
        final var flukePath = Objects.requireNonNull(getFlukePath());
        fileChooser.setInitialDirectory(flukePath.toFile());
        final var extFilter = new FileChooser.ExtensionFilter("Fluke files", "*.fluke");
        fileChooser.getExtensionFilters().add(extFilter);

        return switch (fileOptions) {
            case SAVE -> fileChooser.showSaveDialog(s);
            case OPEN -> fileChooser.showOpenDialog(s);
        };
    }

    public static boolean confirmationRequest(final String text) {
        final var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(CONFIRMATION_TITLE);
        alert.setHeaderText(null);
        alert.setContentText(text);
        final var response = alert.showAndWait();
        return response.isPresent() && response.get() == ButtonType.OK;
    }

    public static void recoverableError(final String text) {
        final var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(WARNING_TITLE);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void unrecoverableError(final String text) {
        final var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Shared.ERROR_TITLE);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
        Platform.exit();
        System.exit(1);
    }

    /**
     * Convert a `Set<Integer>` representing prompt indices to a human-readable String representation.
     * This consists of the elements of the set surrounded by curly braces.
     * Example: If the set is {1, 2}, this method returns:
     * `"{1}, {2}"`
     * @param set the Set to convert
     * @return    the String representation, which is empty if the set is empty
     */
    public static String promptIndexSetToString(final Set<Integer> set) {
        final var sb = new StringBuilder();
        boolean isNotFirst = false;
        for (final var i : set) {
            if (isNotFirst)
                sb.append(", ");
            sb.append('{');
            sb.append(i);
            sb.append('}');
            isNotFirst = true;
        }
        return sb.toString();
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
}
