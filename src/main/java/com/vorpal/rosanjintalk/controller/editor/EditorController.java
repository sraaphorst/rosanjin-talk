package com.vorpal.rosanjintalk.controller.editor;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.shared.Shared;
import com.vorpal.rosanjintalk.view.editor.EditRowView;
import com.vorpal.rosanjintalk.view.editor.EditorView;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;

/**
 * The top-level editor controller that links the other controllers together.
 */
public class EditorController implements Controller<EditorView> {
    private final EditorView view;
    private final Stage stage;
    final InputsController inputsController;
    final EditorButtonController editorButtonController;
    final EditStoryController editStoryController;
    final Path flukePath;

    // The filename must be mutable so that if a new Fluke is created,
    // the user is prompted once for a filename. Otherwise, the old file is overwritten.
    private String filename;

    // Determine if any modifications have been made.
    private boolean modified;

    public EditorController(final Stage stage, final Fluke fluke) {
        this.stage = stage;

        // Reset the indices so when editing a new Fluke file, we begin the substitutions at 1.
        EditRowView.resetIndex();

        inputsController = new InputsController(this, fluke);
        editorButtonController = new EditorButtonController(this);
        editStoryController = new EditStoryController(this, fluke);
        view = new EditorView(
                inputsController.getView(),
                editorButtonController.getView(),
                editStoryController.getView()
        );
        flukePath = Objects.requireNonNull(Shared.getFlukePath());
        filename = fluke == null ? null : fluke.filename();
        modified = false;
    }

    @Override
    public void configure() {
        inputsController.configure();
        editStoryController.configure();

        // The EditorButtonController must be configured last as, to set the initial state of the
        // buttons, it depends on values configured in the other two controllers.
        editorButtonController.configure();
    }

    @Override
    public EditorView getView() {
        return view;
    }

    /**
     * Attempt to save the Fluke represented in the editor.
     * Invoked by the Save button in the EditorButtonController.
     */
    void saveFluke() {
        // TODO: Previously checked if the inputs were valid, i.e. each input had a prompt.
        // TODO: Now we should not have to do this because blank prompts disable the save button.
        // Check to make sure the substitutions are correct.
        final var inputs = inputsController.getInputs();
        final var substitutions = editStoryController.getSubstitutions();

        // If we are missing inputs that are used in the story, alert and abort.
        if (!inputs.keySet().containsAll(substitutions)) {
            substitutions.removeAll(inputs.keySet());
            Shared.recoverableError("There are undefined substitutions used in the story:\n\n" +
                    Shared.promptIndexSetToString(substitutions)
            );
            return;
        }

        // If we have extra inputs that are unneeded, warn and prompt.
        if (!substitutions.containsAll(inputs.keySet())) {
            final var keySet = new HashSet<>(inputs.keySet());
            keySet.removeAll(substitutions);
            final var response = Shared.confirmationRequest("There are extra substitutions defined:\n\n" +
                    Shared.promptIndexSetToString(keySet) +
                    "\n\nSave anyways?");
            if (!response)
                return;
        }

        // If the filename is null, prompt for a filename, which must be in the fluke directory.
        if (filename == null) {
            // Get the save filename.
            final var file = Shared.fileChooserDialog(stage, Shared.FileOptions.SAVE);

            if (file == null)
                return;

            if (!Objects.equals(file.getParent(), flukePath.toString())) {
                Shared.recoverableError("Fluke files must be saved to the fluke directory:\n\n" + flukePath);
                return;
            }

            filename = file.getName();
        }

        new Fluke(filename, editStoryController.getTitle(), inputs, editStoryController.getStory()).save();
        markUnmodified();

        // The focus shifts to the title since the save button is focused and ends up disabled
        // after the story is saved, so remove the focus by doing this.
        this.view.requestFocus();
    }

    /**
     * Mark the edited Fluke as being modified.
     */
    void markModified() {
        modified = true;
        editorButtonController.configureSaveButtonState();
    }

    /**
     * Mark the edited Fluke as being unmodified.
     */
    void markUnmodified() {
        modified = false;
        editorButtonController.configureSaveButtonState();
    }

    /**
     * Return whether the Fluke has been modified at all.
     * @return modified status of Fluke
     */
    public boolean isModified() {
        return modified;
    }
}
