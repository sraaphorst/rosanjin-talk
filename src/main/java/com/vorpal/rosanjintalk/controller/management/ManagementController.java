package com.vorpal.rosanjintalk.controller.management;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.Controller;
import com.vorpal.rosanjintalk.view.management.ManagementView;
import javafx.stage.Stage;

public final class ManagementController implements Controller<ManagementView> {
    private final ManagementView view;
    final FlukeSelectorController flukeSelectorController;
    final ManagementButtonController managementButtonController;

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
}
