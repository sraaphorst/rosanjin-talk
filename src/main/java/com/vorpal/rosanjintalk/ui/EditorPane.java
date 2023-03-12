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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An input panel to manage the substitution information for the main story.
 */
final public class EditorPane extends BorderPane {
    // General spacing.
    private static final int SPACING = 10;
    private static final int BUTTON_SPACING = 20;

    // The left panel is a fixed width, and the minimum starting width of the story is suggested.
    // These are used to initialize the scene coordinates.
    private static final int LEFT_WIDTH = 300;
    private static final int STORY_WIDTH = 400;
    private static final int HEIGHT = 400;

    // Column properties for the GridPanel.
    private static final int CB_COLUMN_WIDTH = 10;
    private static final int SUB_COLUMN_WIDTH = 20;
    private static final int DESC_COLUMN_WIDTH = 70;

    // Event handler for all checkboxes in rows.
    final EventHandler<ActionEvent> deleteCheck = (final ActionEvent e) -> processDeleteButton();

    final private class Row {
        // Number of the next row.
        private static int rowIdx = 0;

        final int index;
        final CheckBox cb = new CheckBox();
        final TextField substitution = new TextField();
        final TextField prompt = new TextField();

        /**
         * Create a row from existing data. Make sure that the roxIdx is higher than the idx.
         * @param idx          the substitution index of the row
         * @param prompt  the description for the row
         */
        public Row(final int idx, final String prompt) {
            // Bump up the rowIdx if necessary to make sure that we are not repeating values.
            if (idx > rowIdx)
                rowIdx = idx + 1;

            index = idx;
            substitution.setText("{" + idx + "}");
            this.prompt.setText(prompt);
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

            // Disable save button if there are empty rows.
            prompt.setOnKeyTyped(e -> EditorPane.this.modifySaveButtonState());
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

    private String filename;
    private final Stage stage;
    private final List<Row> rows = new ArrayList<>();
    private final Button addButton = new Button("Add row");
    private final Button deleteButton = new Button("Delete Row(s)");
    private final Button saveButton = new Button("Save");
    private final StoryPane storyPane;

    public EditorPane(final Stage stage) {
        super();
        this.stage = stage;
        filename = null;
        storyPane = new StoryPane();
        createUI(null);
    }

    public EditorPane(final Stage stage, final Fluke fluke) {
        super();
        this.stage = stage;
        filename = fluke.filename();
        storyPane = new StoryPane(fluke);
        createUI(fluke);
    }

    private void createUI(final Fluke fluke) {
        // *** SUBSTITUTION GRID
        final var gridPane = new GridPane();
        gridPane.setHgap(SPACING);
        gridPane.setPadding(new Insets(SPACING));

        // Constraints on the columns.
        final var c0 = new ColumnConstraints();
        c0.setPercentWidth(CB_COLUMN_WIDTH);
        final var c1 = new ColumnConstraints();
        c1.setPercentWidth(SUB_COLUMN_WIDTH);
        final var c2 = new ColumnConstraints();
        c2.setPercentWidth(DESC_COLUMN_WIDTH);
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
        rows.forEach(row -> gridPane.addRow(gridPane.getRowCount(), row.cb, row.substitution, row.prompt));

        // Wrap the GridPane in a vertical ScrollPane.
        // We need to set the width, or it just grows out of control.
        final var gridScrollPane = Shared.createStandardScrollPane(gridPane);

        // *** BUTTON PANEL
        addButton.setTooltip(new Tooltip("Add a row to the substitution table."));
        deleteButton.setTooltip(new Tooltip("Delete row(s) from the substitution table."));
        saveButton.setTooltip(new Tooltip("Verify and save Fluke."));

        final var buttonBox = new HBox(addButton, deleteButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(BUTTON_SPACING);

        addButton.setOnAction((final ActionEvent e) -> {
            final var row = new Row();
            rows.add(row);
            gridPane.addRow(gridPane.getRowCount(), row.cb, row.substitution, row.prompt);

            // Modify the UI.
            processDeleteButton();
            modifySaveButtonState();
        });

        deleteButton.setOnAction((final ActionEvent e) -> {
            // Remove the rows from the UI.
            rows.stream()
                    .filter(row -> row.cb.isSelected())
                    .forEach(row -> gridPane.getChildren().removeAll(row.cb, row.substitution, row.prompt));

            // Remove the rows from the rows array.
            rows.removeAll(rows
                    .stream()
                    .filter(row -> row.cb.isSelected())
                    .toList());

            // Modify the UI.
            processDeleteButton();
            modifySaveButtonState();
        });

        saveButton.setOnAction(e -> {
            // This should never happen: the save button should prevent it..
            if (!inputsValid()) {
                Shared.recoverableError("There are empty prompts.");
                return;
            }

            // Check to make sure that the substitutions are correct.
            final var inputs = getInputs();
            final var title = storyPane.title.getText().trim();
            final var story = storyPane.story.getText().trim();
            final var substitutionSet = Fluke.allSubstituations(title, story);

            // Check to see if we are missing any keys that are defined in the story.
            // Cannot save if this is the case.
            if (!inputs.keySet().containsAll(substitutionSet)) {
                substitutionSet.removeAll(inputs.keySet());
                Shared.recoverableError("There are undefined keys used in the story:\n\n" +
                        Shared.setToString(substitutionSet)
                );
                return;
            }

            // Check to see if we have extra keys.
            if (!substitutionSet.containsAll(inputs.keySet())) {
                final var keySet = new HashSet<>(inputs.keySet());
                keySet.removeAll(substitutionSet);
                final var response = Shared.confirmationRequest("There are extra substitutions defined:\n\n" +
                        Shared.setToString(keySet) +
                        "\n\nSave anyways?");
                if (!response)
                    return;
            }

            if (filename == null) {
                // Display a file save box.
                final var fileChooser = new FileChooser();
                fileChooser.setTitle("Save Fluke");
                final var flukePath = Objects.requireNonNull(RosanjinTalk.getFlukePath());
                fileChooser.setInitialDirectory(flukePath.toFile());
                final var extFilter = new FileChooser.ExtensionFilter("Fluke files", "*.fluke");
                fileChooser.getExtensionFilters().add(extFilter);
                final var selectedFile = fileChooser.showSaveDialog(stage);
                if (selectedFile == null)
                    return;
                if (!Objects.equals(selectedFile.getParent(), flukePath.toString())) {
                    Shared.recoverableError("Fluke files must be saved to the fluke directory.");
                    return;
                }
                filename = selectedFile.getName();
            }

            new Fluke(filename, title, inputs, story).save();
        });

        // Put everything together in a BorderPane.
        final var leftPane = new BorderPane();
        leftPane.setCenter(gridScrollPane);
        leftPane.setBottom(buttonBox);
        leftPane.setPrefWidth(LEFT_WIDTH);
        BorderPane.setMargin(leftPane, new Insets(SPACING));
        BorderPane.setMargin(buttonBox, new Insets(SPACING));

        // Connect the storyPane widgets to the state of the Save button.
        storyPane.title.onKeyTypedProperty().set(evt -> modifySaveButtonState());
        storyPane.story.onKeyTypedProperty().set(evt -> modifySaveButtonState());

        setLeft(leftPane);
        setCenter(storyPane);
        modifySaveButtonState();
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
     * and story. If we do, then set the save button.
     */
    private void modifySaveButtonState() {
        final var emptyRows = rows.stream()
                .anyMatch(r -> r.prompt.getText().isBlank());
        saveButton.setDisable(rows.size() == 0 || emptyRows || storyPane.checkUnfinished());
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
                .filter(r -> r.prompt.getText().trim().isEmpty())
                .findFirst();

        // If there is an empty row, then provide it with the focus and do not return.
        emptyRow.ifPresent(r -> r.prompt.requestFocus());
        return emptyRow.isEmpty();
    }

    /**
     * Convert the inputs into a format that can be stored by a Fluke.
     * We assume that inputsValid has been called before this method.
     * @return the input map for a Fluke.
     */
    private Map<Integer, String> getInputs() {
        return rows.stream()
                .collect(Collectors.toUnmodifiableMap(r -> r.index, r -> r.prompt.getText()));
    }
}
