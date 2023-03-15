package com.vorpal.rosanjintalk.view.player;

// By Sebastian Raaphorst, 2023.

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextAlignment;

public final class PlayerRowView {
    public final int idx;
    public final Label prompt;
    public final TextField answer;

    public PlayerRowView(final int idx, final String promptText) {
        this.idx = idx;

        prompt = new Label(promptText);
        prompt.setTooltip(new Tooltip(promptText));
        prompt.setTextAlignment(TextAlignment.RIGHT);
        prompt.setAlignment(Pos.CENTER_LEFT);

        answer = new TextField();
        answer.setAlignment(Pos.CENTER_LEFT);
    }
}
