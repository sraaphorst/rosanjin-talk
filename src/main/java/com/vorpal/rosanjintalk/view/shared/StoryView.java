package com.vorpal.rosanjintalk.view.shared;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.shared.Shared;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;

public final class StoryView extends BorderPane {
    public final TextField title;
    public final TextArea story;

    public StoryView() {
        super();
        setPadding(Shared.PADDING);
        setMargin(this, Shared.PADDING);

        title = new TextField();

        final var titleLabel = new Label("Title:");
        titleLabel.setLabelFor(title);
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setTextAlignment(TextAlignment.LEFT);

        final var titlePane = new BorderPane();
        titlePane.setLeft(titleLabel);
        titlePane.setCenter(title);
        BorderPane.setMargin(titleLabel, new Insets(Shared.SPACING, Shared.SPACING, Shared.SPACING, 0));
        BorderPane.setAlignment(titleLabel, Pos.CENTER_LEFT);
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        titlePane.prefWidthProperty().bind(widthProperty());
        setTop(titlePane);

        story = new TextArea();
        story.setWrapText(true);
        setCenter(story);
    }
}
