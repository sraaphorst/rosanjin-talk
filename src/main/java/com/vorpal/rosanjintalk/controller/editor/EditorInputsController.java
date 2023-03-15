package com.vorpal.rosanjintalk.controller.editor;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.view.editor.EditorRowView;
import com.vorpal.rosanjintalk.view.editor.EditorInputsView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EditorInputsController implements Controller<EditorInputsView> {
    private final EditorInputsView view;
    private final List<EditorRowView> rows;
    private final EditorController editorController;

    // Checkbox action for rows to determine if the delete button should be available.
    private final EventHandler<ActionEvent> cbEventHandler;

    public EditorInputsController(final EditorController editorController) {
        this(editorController, null);
    }

    public EditorInputsController(final EditorController editorController,
                                  final Fluke fluke) {
        view = new EditorInputsView();
        rows = new ArrayList<>();
        this.editorController = editorController;
        cbEventHandler = (final ActionEvent e) -> editorController.editorButtonController.configureDeleteButtonState();

        // If fluke is not null, set the initial rows.
        if (fluke != null)
            fluke.inputs().forEach((idx, prompt) ->
                    rows.add(new EditorRowView(idx, prompt))
            );
    }

    @Override
    public void configure() {
        // If the set of rows is non-empty, then row data was passed in by the Fluke.
        // The rows have been created, but not configured, so call configureRow on each.
        if (!rows.isEmpty()) {
            rows.sort(Comparator.comparingInt(o -> o.idx));
            rows.forEach(this::configureRow);
        }

        // Otherwise, just insert one empty row to give the user an indication on how to work.
        // Note that addRow adds an empty row and calls configureRow on it.
        else
            addRow();
    }

    @Override
    public EditorInputsView getView() {
        return view;
    }

    /**
     * Add a new empty row.
     */
    public void addRow() {
        final var row = new EditorRowView();
        rows.add(row);
        configureRow(row);
        editorController.markModified();
    }

    /**
     * Common method to configure an EditRowView with the proper handlers and it to the view.
     * Calls the EditorButtonController.configureSaveButton method to determine if saving should
     *   be allowed.
     * @param rowView the EditRowView to configure
     */
    private void configureRow(final EditorRowView rowView) {
        rowView.cb.setOnAction(cbEventHandler);
        rowView.prompt.textProperty().addListener((observable, oldValue, newValue) -> {
            editorController.editorButtonController.configureSaveButtonState();
            editorController.markModified();
        });
        view.addRow(view.getRowCount(), rowView.cb, rowView.sub, rowView.prompt);

        // Recalculate the configuration state of the save button based on the contents of the new row.
        editorController.editorButtonController.configureSaveButtonState();
    }

    /**
     * Delete the rows that have their checkboxes checked.
     */
    void deleteRows() {
        // Get the rows that are checked.
        final var checkedRows = rows.stream()
                .filter(row -> row.cb.isSelected())
                .toList();

        // Remove the rows from the view.
        checkedRows.forEach(row -> view.getChildren().removeAll(row.cb, row.sub, row.prompt));

        // Remove the rows from the row array.
        rows.removeAll(checkedRows);

        // Update the button states.
        editorController.editorButtonController.configureSaveButtonState();
        editorController.editorButtonController.configureDeleteButtonState();

        editorController.markModified();
    }

    /**
     * Determine how many checkboxes have been ticked.
     * This determines the state of the deleteButton in the EditorButtonController.
     * @return the number of checkboxes ticked
     */
    long getCheckboxesTicked() {
        return rows.stream()
                .filter(r -> r.cb.isSelected())
                .count();
    }

    /**
     * Returns if this component is incomplete, i.e. there is a prompt that is blank.
     * This is for use by the save button state enable calculation.
     * @return true if incomplete (i.e. any of the prompts blank), false otherwise
     */
    boolean isIncomplete() {
        return rows.stream()
                .anyMatch(r -> r.prompt.getText().isBlank());
    }

    /**
     * Retrieve the list of inputs from the component for use in a Fluke file.
     * @return a Map of index to prompt
     */
    Map<Integer, String> getInputs() {
        return rows.stream()
                .collect(Collectors.toUnmodifiableMap(r -> r.idx, r -> r.prompt.getText()));
    }
}
