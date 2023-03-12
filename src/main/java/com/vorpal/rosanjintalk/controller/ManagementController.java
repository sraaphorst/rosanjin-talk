package com.vorpal.rosanjintalk.controller;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.view.ManagementView;
import javafx.stage.Stage;

public final class ManagementController implements Controller<ManagementView> {
    private final ManagementView view;
    private final FlukeSelectorController flukeSelectorController;
    private final ManagementButtonController managementButtonController;

    public ManagementController(final Stage stage) {
        flukeSelectorController = new FlukeSelectorController(this);
        managementButtonController = new ManagementButtonController(stage, this);
        view = new ManagementView(
                flukeSelectorController.getView(),
                managementButtonController.getView()
        );
    }

    @Override
    public void configure() {
        flukeSelectorController.configure();
        managementButtonController.configure();
    }

    @Override
    public ManagementView getView() {
        return view;
    }

    public FlukeSelectorController getFlukeSelectorController() {
        return flukeSelectorController;
    }

    public ManagementButtonController getManagementButtonController() {
        return managementButtonController;
    }
}
