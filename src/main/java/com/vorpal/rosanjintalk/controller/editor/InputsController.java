package com.vorpal.rosanjintalk.controller.editor;

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.view.editor.EditRowView;
import com.vorpal.rosanjintalk.view.editor.InputsView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InputsController implements Controller<InputsView> {
    private final InputsView view;
    private final List<EditRowView> rows;

    // Filename has to be mutable to save without prompting.
    private String filename;

    // Checkbox action for rows to determine if the delete button should be available.
    private final EventHandler<ActionEvent> cbEventHandler =
            (final ActionEvent e) -> processDeleteButtonState();

    public InputsController(final Fluke fluke) {
        view = new InputsView();
        filename = fluke.filename();
        rows = new ArrayList<>();
        fluke.inputs().forEach(this::addRow);
    }

    public InputsController() {
        view = new InputsView();
        filename = null;
        rows = new ArrayList<>();

        // Add one blank row to give the user an indication on how to work.
        addRow();
    }

    @Override
    public void configure() {
        // Set up the rows upon initial configuration.
        rows.forEach(this::configureRow);
    }

    @Override
    public InputsView getView() {
        return view;
    }

    /**
     * Add a new empty row.
     */
    public void addRow() {
        final var row = new EditRowView();
        rows.add(row);
        configureRow(row);
    }

    /**
     * Add an existing row, i.e. from a supplied Fluke.
     * @param idx    the idx of the substitution
     * @param prompt the prompt for the substitution
     */
    public void addRow(final int idx, final String prompt) {
        final var row = new EditRowView(idx, prompt);
        rows.add(row);
        configureRow(row);
    }

    /**
     * Common method to configure rowViews with the proper handlers.
     * @param rowView the EditRowView to configure
     */
    private void configureRow(final EditRowView rowView) {
        rowView.cb.setOnAction(cbEventHandler);
        rowView.prompt.setOnKeyTyped(e -> processSaveButtonState());
    }

    /**
     * Check if the Delete button in the button panel should be active.
     * This occurs if there is a checkbox checked.
     */
    public void processDeleteButtonState() {
        final var checkboxesTicked = rows.stream()
                .filter(r -> r.cb.isSelected())
                .count();
        // TODO: Send the output to the EditorButtonView's setDeleteButtonState.
    }

    /**
     * Check if the Save button in the button panel should be active.
     * This occurs if there is no empty data fields.
     */
    public void processSaveButtonState() {
        // TODO: Call the EditorButtonView's processSaveButtonState.
    }

    /**
     * Returns if this component is incomplete, for use by the save button enable calculation.
     * If a prompt is found that is blank, the first one found grabs the focus.
     * @return true if incomplete (i.e. any of the prompts blank), false otherwise
     */
    public boolean isIncomplete() {
        final var emptyRow = rows.stream()
                .filter(r -> r.prompt.getText().isBlank())
                .findFirst();

        emptyRow.ifPresent(r -> r.prompt.requestFocus());
        return emptyRow.isPresent();
    }

    /**
     * Retrieve the list of inputs from the component for use in a Fluke file.
     * @return a Map of index to prompt
     */
    public Map<Integer, String> getInputs() {
        return rows.stream()
                .collect(Collectors.toUnmodifiableMap(r -> r.idx, r -> r.prompt.getText()));
    }
}
