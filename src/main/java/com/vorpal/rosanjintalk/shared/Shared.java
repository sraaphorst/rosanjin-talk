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
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Shared {
    public static final String TITLE = "RosanjinTalk";
    public static final String WARNING_TITLE = TITLE + " Warning";
    public static final String ERROR_TITLE = TITLE + " Error";
    public static final String CONFIRMATION_TITLE = TITLE + " Confirmation";

    // Spacing information for JavaFX.
    public static final double SPACING = 10;
    public static final double HORIZONTAL_SPACING = 20;
    public static final double VERTICAL_SPACING = 20;
    public static final Insets PADDING = new Insets(SPACING);

    // Time of day in ms.
    private static final long dayMs = TimeUnit.DAYS.toMillis(1);

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

    public static File flukeFileChooserDialog(final Stage s, final FileOptions fileOptions) {
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

    public static File textFileChooserDialog(final Stage s) {
        final var fileChooser = new FileChooser();
        fileChooser.setTitle("Save Story");

        // Start with trying the Documents directory, which is the default
        // on Mac, Windows, and Linux, and fall back to home.
        final var userHome = System.getProperty("user.home");
        final var documentsPath = Paths.get(userHome, "Documents");
        final var savePath = documentsPath.toFile().exists() ? documentsPath.toFile() : new File(userHome);
        fileChooser.setInitialDirectory(savePath);

        final var extFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showSaveDialog(s);
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

    public static void infoMessage(final String title, final String text) {
        System.out.println(title);
        System.out.println(text);
        final var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
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

    /**
     * Calculates the duration between now and the next instance of time.
     * @return a long representing the milliseconds until the next event
     */
    private static long getDelay(final LocalTime time) {
        final var delay = Duration.between(LocalTime.now(), time).toMillis();
        return delay >= 0 ? delay : delay + dayMs;
    }

    /**
     * A hidden gem: configuration of several alert panes at 11:11 and 23:11 to make messages appear about
     * Walnut the crane.
     */
    public static void configureWalnut() {
        var executorService = Executors.newScheduledThreadPool(1);
            final Runnable walnut = () -> Platform.runLater(() -> {
                infoMessage("Uh oh...",
                        """
                                Off in the distance
                                Bird talons upon pavement
                                She cannot be stopped.
                                """);
                infoMessage("X_X",
                        """
                                It is Walnut the crane
                                Justifiably insane
                                The blood is still fresh
                                She dines upon flesh
                                Not a modicum of bird shame.
                                """);
                infoMessage("...",
                        """
                                The task is now complete.
                                She has accomplished her feat.
                                Her cloaca is proud and puffy
                                She needs her life mate most scruffy
                                Only one man is her beau
                                The zookeeper known as Crowe.
                                """);
            });

            final var delay_11_11 = getDelay(LocalTime.of(11, 11));
            final var delay_23_11 = getDelay(LocalTime.of(23, 11));

            executorService.scheduleAtFixedRate(walnut, delay_11_11, dayMs, TimeUnit.MILLISECONDS);
            executorService.scheduleAtFixedRate(walnut, delay_23_11, dayMs, TimeUnit.MILLISECONDS);
    }
}
