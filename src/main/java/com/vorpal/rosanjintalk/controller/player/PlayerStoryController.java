package com.vorpal.rosanjintalk.controller.player;

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.shared.Shared;
import com.vorpal.rosanjintalk.view.shared.StoryView;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Files;

public class PlayerStoryController implements Controller<StoryView> {
    private final StoryView view;
    private final PlayerController playerController;

    public PlayerStoryController(final PlayerController playerController) {
        view = new StoryView();
        this.playerController = playerController;
    }

    @Override
    public void configure() {
        view.title.setEditable(false);
//        view.title.setDisable(true);
        view.title.setFocusTraversable(false);
        view.story.setEditable(false);
//        view.story.setDisable(true);
        view.story.setFocusTraversable(false);
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
     * Produce a formatted version of the story, i.e. the title with two
     * platform-dependent line breaks and then the story followed by a line break.
     * @return the String representing the formatted story.
     */
    private String getFormattedStory() {
        final var lineSep = System.lineSeparator();
        return view.title.getText() + lineSep + lineSep + view.story.getText() + lineSep;
    }
    /**
     * Copy the story to the clipboard. This involves the header, followed
     * by two returns, and then the body of the story text.
     */
    void copyToClipboard() {
        final var formattedStory = getFormattedStory();
        final var selection  = new StringSelection(formattedStory);
        final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    /**
     * Save the story to a file.
     */
    void saveStory() {
        final var formattedStory = getFormattedStory();
        final var file = Shared.textFileChooserDialog(playerController.stage);

        if (file == null)
            return;

        try {
            final var path = file.toPath();
            Files.writeString(path, formattedStory);
        } catch (final IOException ex) {
            Shared.recoverableError("Could not write story to:\n\n" + file);
        }
    }
}
