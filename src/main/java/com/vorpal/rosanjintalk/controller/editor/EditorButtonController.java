package com.vorpal.rosanjintalk.controller.editor;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.view.editor.EditorButtonView;

public class EditorButtonController implements Controller<EditorButtonView> {
    private final EditorButtonView view;
    private final EditorController editorController;

    public EditorButtonController(final EditorController editorController) {
        view = new EditorButtonView();
        this.editorController = editorController;
    }

    @Override
    public void configure() {
        view.addButton.setOnAction(e -> editorController.inputsController.addRow());
        view.deleteButton.setOnAction(e -> editorController.inputsController.deleteRows());
        view.saveButton.setOnAction(e -> editorController.saveFluke());
        configureSaveButtonState();
        configureDeleteButtonState();
    }

    @Override
    public EditorButtonView getView() {
        return view;
    }

    /**
     * Configure the state of the buttons in this panel depending on the other panels.
     */
    void configureSaveButtonState() {
        view.saveButton.setDisable(editorController.inputsController.isIncomplete() ||
                editorController.storyController.isIncomplete() ||
                !editorController.isModified()
        );
    }

    /**
     * Configure the state of the delete button in this panel depending on the InputsController.
     */
    void configureDeleteButtonState() {
        view.deleteButton.setDisable(
                editorController.inputsController.getCheckboxesTicked() == 0
        );
    }
}
