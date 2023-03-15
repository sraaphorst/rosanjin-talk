package com.vorpal.rosanjintalk.view.player;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.shared.Shared;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class PlayerInputsView extends GridPane {
    public PlayerInputsView() {
        super();
        setHgap(Shared.SPACING);
        setPadding(Shared.PADDING);

        final var c0 = new ColumnConstraints();
        c0.setPercentWidth(75);
        final var c1 = new ColumnConstraints();
        c1.setPercentWidth(25);
        getColumnConstraints().addAll(c0, c1);

        final var promptLabel = new Label("Prompt");
        promptLabel.setTooltip(new Tooltip("Prompt for the Fluke."));
        promptLabel.setAlignment(Pos.CENTER);
        add(promptLabel, 0, 0);

        final var answerLabel = new Label("Answer");
        answerLabel.setTooltip(new Tooltip("Answer, and not with fleurg."));
        add(answerLabel, 1, 0);
    }
}
