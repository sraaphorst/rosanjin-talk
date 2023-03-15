package com.vorpal.rosanjintalk.view.player;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.shared.Shared;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public final class PlayerButtonView extends HBox {
    public final Button playButton;
    public final Button copyButton;
    public final Button saveButton;

    public PlayerButtonView() {
        super();
        setAlignment(Pos.CENTER);
        setSpacing(Shared.HORIZONTAL_SPACING);

        playButton = new Button("Play");
        final var playButtonTooltip = new Tooltip(
                "Take your two peeled potatoes and play.\n" +
                "Shove them into that fluke!");
        playButtonTooltip.setWrapText(true);
        playButton.setTooltip(playButtonTooltip);

        copyButton = new Button("Copy");
        copyButton.setTooltip(new Tooltip("Copy story to clipboard."));

        saveButton = new Button("Save");
        final var saveButtonTooltip = new Tooltip(
                "Save it to force it on someone else.\n" +
                "For when you don't want to lose the erection."
        );
        saveButtonTooltip.setWrapText(true);
        saveButton.setTooltip(saveButtonTooltip);

        getChildren().addAll(playButton, copyButton, saveButton);
    }
}
