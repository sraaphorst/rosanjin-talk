package com.vorpal.rosanjintalk.view;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.controller.ManagementController;
import javafx.scene.layout.BorderPane;

public final class ManagementView extends BorderPane {
    public ManagementView(final FlukeSelectorView flukeSelectorView,
                          final ManagementButtonView managementButtonView) {
        super();
        final var splashPanel = new SplashPanelView();
        setTop(splashPanel);
        setCenter(flukeSelectorView);
        setRight(managementButtonView);
    }
}
