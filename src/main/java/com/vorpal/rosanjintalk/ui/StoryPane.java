package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.model.Fluke;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;

public final class StoryPane extends BorderPane {
    final int TITLE_HEIGHT = 50;
    final int STORY_HEIGHT = 200;

    final TextField title = new TextField();
    final TextArea story = new TextArea();

    public StoryPane() {
        super();
        createUI(null);
    }

    public StoryPane(final Fluke fluke) {
        super();
        createUI(fluke);
    }

    private void createUI(final Fluke fluke) {
        BorderPane.setMargin(this, new Insets(10));

        // Create the title bar.
        setPadding(new Insets(0, 20, 20, 20));
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

        story.setWrapText(true);
        final var scrollPane = Shared.createStandardScrollPane(story);
        scrollPane.prefWidthProperty().bind(widthProperty());
        story.prefHeightProperty().bind(scrollPane.heightProperty());
        setCenter(scrollPane);

        if (fluke != null) {
            title.setText(fluke.title());
            story.setText(fluke.story());
        }
    }

    boolean checkUnfinished() {
        return title.getText().isBlank() || story.getText().isBlank();
    }
}
