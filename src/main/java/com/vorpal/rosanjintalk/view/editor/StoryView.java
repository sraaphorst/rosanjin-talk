package com.vorpal.rosanjintalk.view.editor;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.ui.Shared;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;

public class StoryView extends BorderPane {
    private static final int TITLE_HEIGHT = 50;

    public final TextField title;
    public final TextArea story;

    public StoryView() {
        super();
        BorderPane.setMargin(this, Shared.PADDING);
        setPadding(new Insets(0, 20, 20, 20));

        title = new TextField();

        final var titleLabel = new Label("Title:");
        titleLabel.setLabelFor(title);
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setTextAlignment(TextAlignment.LEFT);

        final var titlePane = new BorderPane();
        titlePane.setLeft(titleLabel);
        titlePane.setCenter(title);
        BorderPane.setMargin(title, new Insets(0, 0, 0, 10));
        BorderPane.setAlignment(titleLabel, Pos.CENTER_LEFT);
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        titlePane.setPadding(new Insets(10));
        titlePane.setMinHeight(TITLE_HEIGHT);
        titlePane.setMaxHeight(TITLE_HEIGHT);
        titlePane.setPrefHeight(TITLE_HEIGHT);
        titlePane.prefWidthProperty().bind(widthProperty());
        setTop(titlePane);

        story = new TextArea();
        story.setWrapText(true);
        final var scrollPane = Shared.createStandardScrollPane(story);
        story.prefHeightProperty().bind(scrollPane.heightProperty());
        setCenter(scrollPane);
    }
}
