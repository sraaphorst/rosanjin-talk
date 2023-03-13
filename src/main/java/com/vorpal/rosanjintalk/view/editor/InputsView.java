package com.vorpal.rosanjintalk.view.editor;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.shared.Shared;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public final class InputsView extends GridPane {
    public InputsView() {
        super();
        setHgap(Shared.SPACING);
        setPadding(Shared.PADDING);

        final var c0 = new ColumnConstraints();
        c0.setPercentWidth(10);
        final var c1 = new ColumnConstraints();
        c1.setPercentWidth(20);
        final var c2 = new ColumnConstraints();
        c2.setPercentWidth(70);
        getColumnConstraints().addAll(c0, c1, c2);

        final var substitutionLabel = new Label("Sub");
        substitutionLabel.setTooltip(new Tooltip("Substitution marker for response in story."));
        substitutionLabel.setAlignment(Pos.CENTER);
        add(substitutionLabel, 1, 0);

        final var promptLabel = new Label("Prompt");
        promptLabel.setTooltip(new Tooltip("Prompt to ask player for substitution marker."));
        add(promptLabel, 2, 0);
    }
}


