package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.model.Fluke;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
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
final public class Editor extends BorderPane {
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

    private Fluke fluke;
    private final List<Row> rows = new ArrayList<>();
    private final Button addButton = new Button("Add row");
    private final Button deleteButton = new Button("Delete Row(s)");
    private final Button saveButton = new Button("Save");
    private final TextField title = new TextField();
    private final TextArea text = new TextArea();

    public Editor() {
        super();
        this.fluke = null;
        createUI();
    }

    public Editor(final Fluke fluke) {
        super();
        this.fluke = fluke;
        createUI();
    }

    private void createUI() {
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

        final var substitutionLabel = new Label("Substitute");
        substitutionLabel.setTextAlignment(TextAlignment.CENTER);
        gridPane.add(substitutionLabel, 1, 0);
        final var descriptionLabel = new Label("Description");
        descriptionLabel.setTextAlignment(TextAlignment.CENTER);
        gridPane.add(descriptionLabel, 2, 0);

        // Add the rows.
        if (fluke != null) {
            final var existingRows = fluke.inputs().entrySet().stream()
                    .map(e -> new Row(e.getKey(), e.getValue()))
                    .toList();
            rows.addAll(existingRows);
        }
        rows.forEach(row -> gridPane.getChildren().addAll(row.cb, row.substitution, row.value));

        // Wrap the GridPane in a vertical ScrollPane.
        final var gridScrollPane = createScrollPane();
        gridScrollPane.setContent(gridPane);

        // *** BUTTON PANEL
        addButton.setTooltip(new Tooltip("Add a row to the substitution table."));
        deleteButton.setTooltip(new Tooltip("Delete row(s) from the substitution table."));
        saveButton.setTooltip(new Tooltip("Verify and save Fluke."));

        final var buttonBox = new HBox(addButton, deleteButton, saveButton);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(100);

        addButton.setOnAction((final ActionEvent e) -> {
            final var row = new Row();
            rows.add(row);
            gridPane.addRow(gridPane.getRowConstraints().size(), row.cb, row.substitution, row.value);

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

        saveButton.setOnAction(e -> Editor.this.getInputs());

        // Put everything together in a BorderPane.
        final var leftPane = new BorderPane();
        leftPane.setPadding(new Insets(20));
        leftPane.setCenter(gridScrollPane);
        leftPane.setBottom(buttonBox);

        // *** Create the central pane for the title and story.
        final var titleBox = new HBox();
        titleBox.setSpacing(10);
        titleBox.setPadding(new Insets(10));
        final var titleLabel = new Label("Title:");
        titleLabel.setTextAlignment(TextAlignment.RIGHT);
        titleLabel.setLabelFor(title);
        titleBox.getChildren().addAll(titleLabel, title);

        // *** Create the center pane.
        text.setWrapText(true);
        final var textScrollPane = createScrollPane();
        textScrollPane.setContent(text);

        final var centerPane = new BorderPane();
        centerPane.setTop(titleBox);
        centerPane.setCenter(textScrollPane);

        title.onKeyTypedProperty().set(evt -> checkFinished());
        text.onKeyTypedProperty().set(evt -> checkFinished());

        setLeft(leftPane);
        setCenter(centerPane);
        checkFinished();
    }

    private ScrollPane createScrollPane() {
        final var scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);

        // Do not allow horizontal scrolling.
        scrollPane.addEventFilter(ScrollEvent.SCROLL,
                evt -> { if (evt.getDeltaX() != 0) evt.consume(); });

        return scrollPane;
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
    private void checkFinished() {
        saveButton.setDisable(rows.size() == 0 || title.getText().isBlank() || text.getText().isBlank());
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
