package com.vorpal.rosanjintalk.view.shared;

import com.vorpal.rosanjintalk.shared.Shared;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * The top-level view for both the PlayerView and the EditView, since
 * they have the same format.
 */
public final class TopView extends BorderPane {
    private static final int LEFT_WIDTH = 350;

    public TopView(final Pane inputView,
                   final Pane buttonView,
                   final Pane storyView) {
        super();

        final var leftPane = new BorderPane();
        final var scrollPane = Shared.createStandardScrollPane(inputView);
        leftPane.setCenter(scrollPane);
        leftPane.setBottom(buttonView);
        leftPane.setPrefWidth(LEFT_WIDTH);
        BorderPane.setMargin(scrollPane, Shared.PADDING);
        BorderPane.setMargin(buttonView, Shared.PADDING);

        setLeft(leftPane);
        setCenter(storyView);
    }
}
