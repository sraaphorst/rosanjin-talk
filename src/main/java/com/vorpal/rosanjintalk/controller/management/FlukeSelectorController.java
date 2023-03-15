package com.vorpal.rosanjintalk.controller.management;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.shared.Shared;
import com.vorpal.rosanjintalk.view.management.FlukeSelectorView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class FlukeSelectorController implements Controller<FlukeSelectorView> {
    private final FlukeSelectorView view;
    private final ManagementController managementController;
    private final Path flukePath;

    public FlukeSelectorController(final ManagementController managementController) {
        view = new FlukeSelectorView();
        this.managementController = managementController;
        flukePath = Objects.requireNonNull(Shared.getFlukePath());
    }

    @Override
    public void configure() {
        view.fileList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            final var disabled = newValue == null;
            managementController.managementButtonController.setButtonsDisable(disabled);
        });
        populateFiles();
    }

    @Override
    public FlukeSelectorView getView() {
        return view;
    }

    /**
     * Get the selected index in the file list.
     * @return the selected index in the file list, or -1 if no index is selected
     */
    int getSelectedIndex() {
        return view.fileList.getSelectionModel().getSelectedIndex();
    }

    /**
     * Get the number of files in the list.
     * @return the number of files in the list
     */
    int getNumberFiles() {
        return view.files.size();
    }

    /**
     * Set the selected index in the file list.
     * @param index the index to select in the file list
     */
    void setSelectedIndex(final int index) {
        view.fileList.getSelectionModel().selectIndices(index);
    }

    /**
     * Return the selected value in the file list.
     * @return the selected value, or null if nothing is selected
     */
    String getSelectedValue() {
        return view.fileList.getSelectionModel().getSelectedItem();
    }

    /**
     * Repopulate the file view by reading in the file list from the fluke path.
     */
    void populateFiles() {
        final List<String> result;
        try (final var walk = Files.walk(flukePath, 1)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.toFile().getName().endsWith(".fluke"))
                    .map(p -> p.toFile().getName())
                    .sorted()
                    .toList();
            view.files.setAll(result);
        } catch (final IOException e) {
            Shared.unrecoverableError("Could not populate fluke file list.");
        }
    }
}
