package com.vorpal.rosanjintalk.controller.player;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.model.InputEmptyException;
import com.vorpal.rosanjintalk.shared.Shared;
import com.vorpal.rosanjintalk.view.shared.TopView;
import javafx.stage.Stage;

public final class PlayerController implements Controller<TopView> {
    private final TopView view;
    final Stage stage;
    final PlayerInputsController playerInputsController;
    final PlayerButtonController playerButtonController;
    final PlayerStoryController playerStoryController;
    final Fluke fluke;
    private static final String MISSING_PROMPTS = "Answers to prompts incomplete.";

    public PlayerController(final Stage stage,
                            final Fluke fluke) {
        this.stage = stage;
        this.fluke = fluke;

        playerInputsController = new PlayerInputsController(this, fluke);
        playerButtonController = new PlayerButtonController(this);
        playerStoryController = new PlayerStoryController(this);
        view = new TopView(
                playerInputsController.getView(),
                playerButtonController.getView(),
                playerStoryController.getView()
        );
    }

    @Override
    public void configure() {
        playerInputsController.configure();
        playerStoryController.configure();
        playerButtonController.configure();
    }

    @Override
    public TopView getView() {
        return view;
    }

    /**
     * Create the story and populate the story view's title and story text.
     * Technically, this method should always return true since the play button should
     * only be enabled if the inputs are all populated.
     * @return true if populating the story was successful, and false otherwise.
     */
    boolean displayStory() {
        // This should never happen.
        if (playerInputsController.isIncomplete()) {
            Shared.recoverableError(MISSING_PROMPTS);
            return false;
        }

        final var answers = playerInputsController.getAnswers();
        try {
            final var title = Fluke.substitute(answers, fluke.title());
            final var story = Fluke.substitute(answers, fluke.story());
            playerStoryController.setTitle(title);
            playerStoryController.setStory(story);
            return true;
        } catch (final InputEmptyException ex) {
            // This should never happen.
            Shared.recoverableError(MISSING_PROMPTS);
            return false;
        }
    }
}
