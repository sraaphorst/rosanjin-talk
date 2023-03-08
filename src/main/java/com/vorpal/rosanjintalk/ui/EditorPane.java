package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.model.Fluke;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An input panel to manage the substitution information for the main text.
 */
final public class EditorPane extends BorderPane {
    // The left panel is a fixed width, and the minimum starting width of the story is suggested.
    // These are used to initialize the scene coordinates.
    private static final int LEFT_WIDTH = 300;
    private static final int STORY_WIDTH = 400;
    private static final int HEIGHT = 400;

    // Event handler for all checkboxes in rows.
    final EventHandler<ActionEvent> deleteCheck = (final ActionEvent e) -> processDeleteButton();

    final private class Row {
        // Number of the next row.
        private static int rowIdx = 0;

        final int index;
        final CheckBox cb = new CheckBox();
        final TextField substitution = new TextField();
        final TextField value = new TextField();

        /**
         * Create a row from existing data. Make sure that the roxIdx is higher than the idx.
         * @param idx          the substitution index of the row
         * @param description  the description for the row
         */
        public Row(final int idx, final String description) {
            // Bump up the rowIdx if necessary to make sure that we are not repeating values.
            if (idx > rowIdx)
                rowIdx = idx + 1;

            index = idx;
            substitution.setText("{" + idx + "}");
            value.setText(description);
            common();
        }

        /**
         * Create a new row and increment the rowIdx.
         */
        public Row() {
            index = ++rowIdx;
            substitution.setText("{" + index + "}");
            common();
        }

        private void common() {
            cb.setSelected(false);
            cb.setFocusTraversable(false);
            cb.setOnAction(deleteCheck);
            cb.setAlignment(Pos.CENTER_RIGHT);

            substitution.setAlignment(Pos.CENTER);
            substitution.setDisable(true);
            substitution.setFocusTraversable(false);
        }
    }

    /**
     * The suggested starting width for this pane.
     * @return width
     */
    static int width() {
        return LEFT_WIDTH + STORY_WIDTH;
    }

    /**
     * The suggested starting height for this pane.
     * @return height
     */
    static int height() {
        return HEIGHT;
    }

    private String filename = null;
    private final List<Row> rows = new ArrayList<>();
    private final Button addButton = new Button("Add row");
    private final Button deleteButton = new Button("Delete Row(s)");
    private final Button saveButton = new Button("Save");
    private final StoryPane storyPane;

    public EditorPane() {
        super();
        storyPane = new StoryPane();
        createUI(null);
    }

    public EditorPane(final Fluke fluke) {
        super();
        storyPane = new StoryPane(fluke);
        createUI(fluke);
    }

    private void createUI(final Fluke fluke) {
        // *** SUBSTITUTION GRID
        final var gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10));

        // Constraints on the columns.
        final var c0 = new ColumnConstraints();
        c0.setPercentWidth(10);
        final var c1 = new ColumnConstraints();
        c1.setPercentWidth(20);
        final var c2 = new ColumnConstraints();
        c2.setPercentWidth(70);
        gridPane.getColumnConstraints().addAll(c0, c1, c2);

        final var substitutionLabel = new Label("Sub");
        substitutionLabel.setTooltip(new Tooltip("Substitution marker for response in story"));
        substitutionLabel.setAlignment(Pos.CENTER);

        final var descriptionLabel = new Label("Description");
        descriptionLabel.setTooltip(new Tooltip("Prompt asked to player"));

        gridPane.add(substitutionLabel, 1, 0);
        gridPane.add(descriptionLabel, 2, 0);

        // Add the rows. If a Fluke file was not passed in, then add one new row.
        if (fluke != null) {
            final var existingRows = fluke.inputs().entrySet().stream()
                    .map(e -> new Row(e.getKey(), e.getValue()))
                    .toList();
            rows.addAll(existingRows);
        } else
            rows.add(new Row());
        rows.forEach(row -> gridPane.addRow(gridPane.getRowCount(), row.cb, row.substitution, row.value));

        // Wrap the GridPane in a vertical ScrollPane.
        // We need to set the width, or it just grows out of control.
        final var gridScrollPane = Shared.createStandardScrollPane(gridPane);

        // *** BUTTON PANEL
        addButton.setTooltip(new Tooltip("Add a row to the substitution table."));
        deleteButton.setTooltip(new Tooltip("Delete row(s) from the substitution table."));
        saveButton.setTooltip(new Tooltip("Verify and save Fluke."));

        final var buttonBox = new HBox(addButton, deleteButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(20);

        addButton.setOnAction((final ActionEvent e) -> {
            final var row = new Row();
            rows.add(row);
            gridPane.addRow(gridPane.getRowCount(), row.cb, row.substitution, row.value);

            // Modify the UI.
            processDeleteButton();
        });

        deleteButton.setOnAction((final ActionEvent e) -> {
            // Remove the rows from the UI.
            rows.stream()
                    .filter(row -> row.cb.isSelected())
                    .forEach(row -> gridPane.getChildren().removeAll(row.cb, row.substitution, row.value));

            // Remove the rows from the rows array.
            rows.removeAll(rows
                    .stream()
                    .filter(row -> row.cb.isSelected())
                    .toList());

            // Modify the UI.
            processDeleteButton();
        });

        saveButton.setOnAction(e -> EditorPane.this.getInputs());

        // Put everything together in a BorderPane.
        final var leftPane = new BorderPane();
        leftPane.setCenter(gridScrollPane);
        leftPane.setBottom(buttonBox);
        leftPane.setPrefWidth(LEFT_WIDTH);
        BorderPane.setMargin(leftPane, new Insets(10));
        BorderPane.setMargin(buttonBox, new Insets(10));

        // Connect the storyPane widgets to the state of the Save button.
        storyPane.title.onKeyTypedProperty().set(evt -> checkUnfinished());
        storyPane.story.onKeyTypedProperty().set(evt -> checkUnfinished());

        setLeft(leftPane);
        setCenter(storyPane);
        checkUnfinished();
    }

    /**
     * Change the Delete Row button depending on what is selected.
     */
    private void processDeleteButton() {
        final var checkboxes = checkBoxCount();
        deleteButton.setDisable(checkboxes == 0L);
        deleteButton.setText("Delete Row" + (checkboxes > 1 ? "s" : ""));
    }

    /**
     * Check to see if the basic structure is complete, i.e. we have rows, and a title,
     * and text. If we do, then set the save button.
     */
    private void checkUnfinished() {
        saveButton.setDisable(rows.size() == 0 || storyPane.checkUnfinished());
    }

    /**
     * Count the number of rows with their checkboxes set to true.
     */
    private long checkBoxCount() {
        return rows.stream().filter(row -> row.cb.isSelected()).count();
    }

    /**
     * Check to see if any of the inputs are empty. If they are, give them the focus.
     * @return true if there are no empty inputs, and false otherwise.
     */
    private boolean inputsValid() {
        final var emptyRow = rows.stream()
                .filter(r -> r.value.getText().trim().isEmpty())
                .findFirst();

        // If there is an empty row, then provide it with the focus and do not return.
        if (emptyRow.isPresent())
            emptyRow.ifPresent(r -> r.value.requestFocus());
        return emptyRow.isEmpty();
    }

    /**
     * Convert the inputs into a format that can be stored by a Fluke.
     * We assume that inputsValid has been called before this method.
     * @return the input map for a Fluke.
     */
    private Map<Integer, String> getInputs() {
        return rows.stream()
                .collect(Collectors.toUnmodifiableMap(r -> r.index, r -> r.value.getText()));
    }
}
