package com.vorpal.rosanjintalk.controller.player;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.model.Fluke;
import com.vorpal.rosanjintalk.shared.Shared;
import com.vorpal.rosanjintalk.view.player.PlayerInputsView;
import com.vorpal.rosanjintalk.view.player.PlayerRowView;
import javafx.application.Platform;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerInputsController implements Controller<PlayerInputsView> {
    private final PlayerInputsView view;
    private final List<PlayerRowView> rows;
    private final PlayerController playerController;

    public PlayerInputsController(final PlayerController playerController,
                                  final Fluke fluke) {
        Objects.requireNonNull(fluke);
        view = new PlayerInputsView();
        rows = new ArrayList<>();
        this.playerController = playerController;

        // Set the fixed rows.
        fluke.inputs().forEach((idx, prompt) ->
                rows.add(new PlayerRowView(idx, prompt))
        );

        // Shuffle the rows to make the game less predictable.
        Collections.shuffle(rows);
    }

    @Override
    public void configure() {
        rows.forEach(rowView -> {
            rowView.answer.textProperty().addListener((observable, oldValue, newValue) -> {
                // Any variation of fleurg is not an admissible answer.
                if (newValue.strip().toLowerCase().contains("fleurg")) {
                    Shared.recoverableError("You are being sent to fleurgatory.");
                    Platform.runLater(rowView.answer::clear);
                }

                // In case the text was deleted, reconfigure the play button state.
                playerController.playerButtonController.configurePlayButtonState();
            });
            view.addRow(view.getRowCount(), rowView.prompt, rowView.answer);
        });
    }

    @Override
    public PlayerInputsView getView() {
        return view;
    }

    /**
     * Returns if this component is incomplete, i.e. there is an answer that is blank.
     * This is for use by the play button state enable calculation.
     * @return true if incomplete (i.e. any answers blank), false otherwise
     */
    boolean isIncomplete() {
        return rows.stream()
                .anyMatch(r -> r.answer.getText().isBlank());
    }

    /**
     * Retrieve the list of answers from the component for use in the substitution of a Fluke file.
     * @return a Map of index to answer
     */
    Map<Integer, String> getAnswers() {
        return rows.stream()
                .collect(Collectors.toUnmodifiableMap(r -> r.idx, r -> r.answer.getText()));
    }

    /**
     * Freeze the input fields. This is invoked when the story is completed and
     * the play button is pressed.
     */
    void freeze() {
        rows.forEach(r -> r.answer.setEditable(false));
    }
}
