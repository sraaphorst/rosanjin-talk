package com.vorpal.rosanjintalk.controller.management;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.RosanjinTalk;
import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.shared.Shared;
import com.vorpal.rosanjintalk.view.management.ManagementButtonView;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public final class ManagementButtonController implements Controller<ManagementButtonView> {
    private final ManagementButtonView view;
    private final Stage stage;
    private final ManagementController managementController;

    public ManagementButtonController(final Stage stage,
                                      final ManagementController managementController) {
        view = new ManagementButtonView();
        this.stage = stage;
        this.managementController = managementController;
        view.playButton.fireEvent(new ActionEvent());
    }

    @Override
    public void configure() {
        final var flukePath = Objects.requireNonNull(Shared.getFlukePath());

        view.playButton.setOnAction(e -> {
            final var selectedFluke = managementController.flukeSelectorController.getSelectedValue();
            final var fluke = Fluke.load(selectedFluke);
            RosanjinTalk.showPlayer(stage, fluke);
        });

        view.addButton.setOnAction(e -> {
            final var fileChooser = new FileChooser();
            fileChooser.setTitle("Select Fluke File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fluke Files", "*.fluke")
            );
            final var selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                final var uri = Paths.get(selectedFile.toURI());
                final var name = selectedFile.getName();

                // Empty string indicates cancellation.
                if (name.equals(""))
                    return;

                try {
                    final var flukeSelectorController = managementController.flukeSelectorController;
                    final var selectedIdx = flukeSelectorController.getSelectedIndex();
                    Files.copy(uri, flukePath.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                    flukeSelectorController.populateFiles();
                    flukeSelectorController.setSelectedIndex(selectedIdx);
                } catch (final IOException ex) {
                    Shared.recoverableError("Could not copy file:\n\n" + uri + "\n\nto:\n\n" + flukePath);
                }
            }
        });

        view.newButton.setOnAction(e -> RosanjinTalk.showEditor(stage, null));

        view.editButton.setOnAction(e -> {
            final var selectedFluke = managementController.flukeSelectorController.getSelectedValue();
            final var fluke = Fluke.load(selectedFluke);
            RosanjinTalk.showEditor(stage, fluke);
        });

        view.deleteButton.setOnAction(e -> {
            final var flukeSelectorController = managementController.flukeSelectorController;
            final var selectedFluke = flukeSelectorController.getSelectedValue();
            final var response = Shared.confirmationRequest("Are you sure you want to delete: " + selectedFluke);
            if (response) {
                final var selectedIdx = flukeSelectorController.getSelectedIndex();
                if (!flukePath.resolve(selectedFluke).toFile().delete())
                    Shared.recoverableError("Could not delete file: " + selectedFluke);
                flukeSelectorController.populateFiles();
                final var newSelectedIdx =
                        selectedIdx >= flukeSelectorController.getNumberFiles() ?
                                selectedIdx - 1 :
                                selectedIdx;
                flukeSelectorController.setSelectedIndex(newSelectedIdx);
            }
        });

        view.fileButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(flukePath.toFile());
            } catch (final IOException ex) {
                Shared.unrecoverableError("Could not open file viewer.");
            }
        });

        setButtonsDisable(true);
    }

    @Override
    public ManagementButtonView getView() {
        return view;
    }

    /**
     * Method to allow enabling / disabling of the play, edit, and delete buttons.
     * Used by the FlukeSelectorController to enable / disable buttons if there is a selection.
     * @param disable true if the buttons should be disabled, and false if they should be enabled
     */
    public void setButtonsDisable(boolean disable) {
        view.playButton.setDisable(disable);
        view.editButton.setDisable(disable);
        view.deleteButton.setDisable(disable);
    }
}
