package com.vorpal.rosanjintalk.view.editor;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.ui.Shared;
import javafx.scene.layout.BorderPane;

public class EditorView extends BorderPane {
    public EditorView(final InputsView inputsView,
                      final EditorButtonView editorButtonView,
                      final StoryView storyView) {
        super();

        final var leftPane = new BorderPane();
        final var scrollPane = Shared.createStandardScrollPane(inputsView);
        leftPane.setCenter(scrollPane);
        leftPane.setBottom(editorButtonView);
        leftPane.setPrefWidth(250);
        BorderPane.setMargin(scrollPane, Shared.PADDING);
        BorderPane.setMargin(editorButtonView, Shared.PADDING);


        setLeft(leftPane);
        setCenter(storyView);
    }
}
