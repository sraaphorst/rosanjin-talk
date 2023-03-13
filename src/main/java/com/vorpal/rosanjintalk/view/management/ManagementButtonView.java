package com.vorpal.rosanjintalk.view.management;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.shared.Shared;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public final class ManagementButtonView extends VBox {
    public final Button playButton;
    public final Button addButton;
    public final Button newButton;
    public final Button editButton;
    public final Button deleteButton;
    public final Button fileButton;

    private Button createStandardButton(final String name, final String toolTip) {
        final var button = new Button(name);
        button.setTooltip(new Tooltip(toolTip));
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    public ManagementButtonView() {
        super(Shared.VERTICAL_SPACING);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(20));

        playButton = createStandardButton("Play", "Play the selected Fluke file.");
        addButton = createStandardButton("Add", "Add an existing Fluke file to the list.");
        newButton = createStandardButton("New", "Create a new Fluke file.");
        editButton = createStandardButton("Edit", "Edit the selected Fluke file.");
        deleteButton = createStandardButton("Delete", "Delete the selected Fluke file.");
        fileButton = createStandardButton("Files", "Open a file manager to the Fluke file repository.");

        getChildren().addAll(playButton, addButton, newButton, editButton, deleteButton, fileButton);
    }
}
