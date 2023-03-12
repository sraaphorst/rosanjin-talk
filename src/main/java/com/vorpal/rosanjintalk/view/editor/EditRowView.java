package com.vorpal.rosanjintalk.view.editor;

// By Sebastian Raaphorst, 2023.

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class EditRowView {
    // Number of the last row.
    private static int rowIdx = 0;

    public final int idx;
    public final CheckBox cb = new CheckBox();
    public final TextField sub = new TextField();
    public final TextField prompt = new TextField();

    /**
     * Used to create a new RowView.
     */
    public EditRowView() {
        this.idx = ++rowIdx;
        common();
    }
    /**
     * Used to create a Row from data from a Fluke file.
     * @param idx    the row's substitution index
     * @param prompt the row's prompt
     */
    public EditRowView(final int idx, final String prompt) {
        if (idx > rowIdx)
            rowIdx = idx + 1;

        this.idx = idx;
        this.prompt.setText(prompt);
        common();
    }

    private void common() {
        cb.setFocusTraversable(false);
        cb.setAlignment(Pos.CENTER_RIGHT);

        sub.setText("{" + idx + "}");
        sub.setDisable(true);
        sub.setAlignment(Pos.CENTER);
        sub.setFocusTraversable(false);
    }
}
