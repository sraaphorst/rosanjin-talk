package com.vorpal.rosanjintalk.controller.editor;

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.view.editor.StoryView;

// By Sebastian Raaphorst, 2023.
public class StoryController implements Controller<StoryView> {
    private final StoryView view;

    public StoryController() {
        view = new StoryView();
    }

    @Override
    public void configure() {
    }

    @Override
    public StoryView getView() {
        return view;
    }

    public void setTitle(final String text) {
        view.title.setText(text);
    }

    public void setStory(final String text) {
        view.story.setText(text);
    }

    /**
     * Check if the Save button in the button panel should be active.
     * This occurs if there is no empty data fields.
     */
    public void processSaveButtonState() {
        // TODO: Call the EditorButtonView's processSaveButtonState.
    }

    /**
     * Returns if this component is in complete, for use by the save button enable calculation.
     * @return true if incomplete (i.e. the title or story is blank), false otherwise
     */
    public boolean isIncomplete() {
        return view.title.getText().isBlank() || view.story.getText().isBlank();
    }
}
