package com.vorpal.rosanjintalk.controller.player;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.view.player.PlayerButtonView;

public final class PlayerButtonController implements Controller<PlayerButtonView> {
    private final PlayerButtonView view;
    private final PlayerController playerController;

    public PlayerButtonController(final PlayerController playerController) {
        view = new PlayerButtonView();
        this.playerController = playerController;
    }

    @Override
    public void configure() {
        view.playButton.setOnAction(e -> {
            final var success = playerController.displayStory();
            if (success) {
                playerController.playerInputsController.freeze();
                view.playButton.setDisable(true);
                view.copyButton.setDisable(false);
                view.saveButton.setDisable(false);
            }
        });

        view.copyButton.setOnAction(e -> playerController.playerStoryController.copyToClipboard());
        view.saveButton.setOnAction(e -> playerController.playerStoryController.saveStory());
        configurePlayButtonState();

        // The state of the copy and save buttons are enabled once the game is played.
        view.copyButton.setDisable(true);
        view.saveButton.setDisable(true);
    }

    @Override
    public PlayerButtonView getView() {
        return view;
    }

    /**
     * Configure the state of the play button in this panel
     * depending on the PromptsController and if the Fluke has been played.
     */
    void configurePlayButtonState() {
        view.playButton.setDisable(playerController.playerInputsController.isIncomplete());
    }

    /**
     * Configure the state of the copy and save button in this panel,
     * which is enabled after the Fluke has been played.
     */
    void configureCopySaveButtonState() {
        // TODO: save button should be shown when the story has been generated.
    }
}
