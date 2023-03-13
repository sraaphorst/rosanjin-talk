package com.vorpal.rosanjintalk.controller.editor;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.view.editor.StoryView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Set;

public class StoryController implements Controller<StoryView> {
    private final StoryView view;
    private final EditorController editorController;

    public StoryController(final EditorController editorController,
                           final Fluke fluke) {
        view = new StoryView();
        this.editorController = editorController;

        // If the Fluke is not null, then set the text from it.
        // This should probably be done in the configure method, but we don't want to save the Fluke here.
        if (fluke != null) {
            view.title.setText(fluke.title());
            view.story.setText(fluke.story());
        }
    }

    @Override
    public void configure() {
        final var changeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                editorController.editorButtonController.configureSaveButtonState();
                editorController.markModified();
            }
        };

        // Set the handlers to recalculate the state of the save button when typing is recorded.
        view.title.textProperty().addListener(changeListener);
        view.story.textProperty().addListener(changeListener);
    }

    @Override
    public StoryView getView() {
        return view;
    }

    /**
     * Retrieve the trimmed text in the title.
     * @return the trimmed title
     */
    public String getTitle() {
        return view.title.getText().trim();
    }

    /**
     * Retrieve the trimmed text in the story.
     * @return the trimmed story
     */
    public String getStory() {
        return view.story.getText().trim();
    }

    /**
     * Set the title from the trimmed text.
     * @param text the text to trim and set as the title
     */
    public void setTitle(final String text) {
        view.title.setText(text.trim());
    }

    /**
     * Set the story from the trimmed text.
     * @param text the text to trim and set as the story
     */
    public void setStory(final String text) {
        view.story.setText(text.trim());
    }

    /**
     * Returns if this component is in complete, for use by the save button enable calculation.
     * @return true if incomplete (i.e. the title or story is blank), false otherwise
     */
    boolean isIncomplete() {
        return view.title.getText().isBlank() || view.story.getText().isBlank();
    }

    /**
     * Calculate the set of substitutions used in the title and story.
     * @return the set of substitutions represented by their index
     */
    Set<Integer> getSubstitutions() {
        return Fluke.allSubstituations(view.title.getText(), view.story.getText());
    }
}
