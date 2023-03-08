package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;

public final class Shared {
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
}
