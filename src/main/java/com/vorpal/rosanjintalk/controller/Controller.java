package com.vorpal.rosanjintalk.controller;

// By Sebastian Raaphorst, 2023.

import javafx.scene.layout.Pane;

public interface Controller<T extends Pane> {
    /**
     * Performs any necessary configuration for the controller.
     * Called after all the controllers and views are created.
     */
    void configure();

    T getView();
}
