package com.vorpal.rosanjintalk.view.management;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.ui.Shared;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;


/**
 * The panel that contains the fluke files available in the fluke directory.
 */
public final class FlukeSelectorView extends BorderPane {
    public final ObservableList<String> files;
    public final ListView<String> fileList;

    public FlukeSelectorView() {
        super();
        setPadding(Shared.PADDING);

        files = FXCollections.observableArrayList();
        fileList = new ListView<>(files);
        fileList.setEditable(false);

        final var scrollPane = Shared.createStandardScrollPane(fileList);
        setCenter(scrollPane);
    }
}
