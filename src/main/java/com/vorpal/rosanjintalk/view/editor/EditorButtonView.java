package com.vorpal.rosanjintalk.view.editor;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.ui.Shared;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public class EditorButtonView extends HBox {
    public final Button addButton;
    public final Button deleteButton;
    public final Button saveButton;

    public EditorButtonView() {
        super();
        setAlignment(Pos.CENTER);
        setSpacing(Shared.HORIZONTAL_SPACING);

        addButton = new Button("Add");
        addButton.setTooltip(new Tooltip("Add a row to the substitution table."));
        deleteButton = new Button("Delete");
        deleteButton.setTooltip(new Tooltip("Delete row(s) from the substitution table."));
        saveButton = new Button("Save");
        saveButton.setTooltip(new Tooltip("Verify and save Fluke."));

        getChildren().addAll(addButton, deleteButton, saveButton);
    }
}
