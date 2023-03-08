package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;

import java.util.Set;

public final class Shared {
    static final String TITLE = "RosanjinTalk";
    static final String WARNING_TITLE = TITLE + " Warning";
    static final String ERROR_TITLE = TITLE + " Error";
    static final String CONFIRMATION_TITLE = TITLE + " Confirmation";

    /**
     * Create a standard vertical ScrollPane to manage n.
     * @param n the Node managed by the ScrollPane
     * @return  the configured ScrollPane
     */
    static ScrollPane createStandardScrollPane(final Node n) {
        final var scrollPane = new ScrollPane(n);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);

        // Do not allow horizontal scrolling.
        scrollPane.addEventFilter(ScrollEvent.SCROLL,
                evt -> { if (evt.getDeltaX() != 0) evt.consume(); });

        return scrollPane;
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

    public static String setToString(final Set<Integer> set) {
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
}
