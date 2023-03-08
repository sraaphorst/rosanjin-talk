package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public class SplashScreen extends BorderPane {
    // The file list.
    private final ObservableList<String> files = FXCollections.observableArrayList();

    // The path to the fluke files. Required by multiple methods and event handlers.
    private final Path flukePath = Objects.requireNonNull(RosanjinTalk.getFlukePath());

    public SplashScreen(final Stage stage) {
        super();

        // *** TOP PANE: SPLASH ***
        final var top = new HBox(30);
        top.setAlignment(Pos.CENTER);
        final var rosanjinImageStream = getClass().getResourceAsStream("/rosanjin.jpeg");
        assert rosanjinImageStream != null;
        final var rosanjinImage = new Image(rosanjinImageStream);
        final var rosanjinImageView = new ImageView(rosanjinImage);

        final var vbox = new VBox(5);
        final var rosanjinLabel = new Label("RosanjinTalk");
        rosanjinLabel.setFont(Font.font("Comic Sans MS", 30));
        final var authorLabel = new Label("By Sebastian Raaphorst");
        final var userLabel = new Label("for smol boi");
        vbox.getChildren().addAll(rosanjinLabel, authorLabel, userLabel);
        top.getChildren().addAll(rosanjinImageView, vbox);
        setTop(top);

        // *** CENTER PANE: FILE LIST ***
        populateFiles();

        final ListView<String> fileList = new ListView<>(files);
        fileList.setEditable(false);

        final var scrollPane = new ScrollPane(fileList);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);

        // Do not allow horizontal scrolling.
        scrollPane.addEventFilter(ScrollEvent.SCROLL,
                evt -> { if (evt.getDeltaX() != 0) evt.consume(); });

        // *** BOTTOM PANE: BUTTONS ***
        final var buttonPlay = new Button("Play");
        buttonPlay.setTooltip(new Tooltip("Play the selected Fluke."));
        buttonPlay.setDisable(true);

        final var buttonNew = new Button("New");
        buttonNew.setTooltip(new Tooltip("Add a new Fluke."));

        final var buttonAdd = new Button("Add");
        buttonAdd.setTooltip(new Tooltip("Add an existing Fluke from a file."));

        final var buttonDelete = new Button("Delete");
        buttonDelete.setDisable(true);
        buttonDelete.setTooltip(new Tooltip("Delete the selected Fluke."));

        final var buttonFiles = new Button("Files");
        buttonFiles.setTooltip(new Tooltip("Open the Fluke directory."));

        buttonAdd.setOnAction(e -> {
            final var fileChooser = new FileChooser();
            fileChooser.setTitle("Select Fluke File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fluke Files", "*.fluke")
            );
            final var selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                final var uri = Paths.get(selectedFile.toURI());
                final var name = selectedFile.getName();

                // Empty string indicates cancellation.
                if (name.equals(""))
                    return;

                try {
                    final var selectedIdx = fileList.getSelectionModel().getSelectedIndex();
                    Files.copy(uri, flukePath.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                    populateFiles();
                    fileList.getSelectionModel().selectIndices(selectedIdx);
                } catch (final IOException ex) {
                    Shared.recoverableError("Could not copy file:\n\n" + uri + "\n\nto:\n\n" + flukePath);
                }
            }
        });

        buttonDelete.setOnAction(e -> {
            // Delete the currently active file in the list after prompting.
            final var filename = fileList.getSelectionModel().selectedItemProperty().getValue();
            final var response = Shared.confirmationRequest("Are you sure you want to delete: " + filename);
            if (response) {
                final var selectedIdx = fileList.getSelectionModel().getSelectedIndex();
                if (!flukePath.resolve(filename).toFile().delete())
                    Shared.recoverableError("Could not delete file: " + filename);
                populateFiles();
                final var newSelectedIdx = selectedIdx >= files.size() ? selectedIdx - 1 : selectedIdx;
                fileList.getSelectionModel().selectIndices(newSelectedIdx);
            }
        });

        buttonFiles.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(flukePath.toFile());
            } catch (final IOException ex) {
                Shared.unrecoverableError("Could not open file viewer.");
            }
        });

        final var bottom = new HBox(30);
        bottom.setPadding(new Insets(20, 20, 20, 20));
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(buttonPlay, buttonNew, buttonAdd, buttonDelete, buttonFiles);
        setBottom(bottom);

        // Make the fileList selection affect the enabled buttons.
        fileList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            final var buttonsDisabled = newValue == null;
            buttonPlay.setDisable(buttonsDisabled);
            buttonDelete.setDisable(buttonsDisabled);
        });
    }

    public void populateFiles() {
        final List<String> result;
        // Now read in the FLUKE files from the directory and populate the lst.
        try (final var walk = Files.walk(flukePath, 1)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.toFile().getName().endsWith(".fluke"))
                    .map(p -> p.toFile().getName())
                    .sorted()
                    .toList();
            files.setAll(result);
        } catch (final IOException e) {
            Shared.unrecoverableError("Could not populate fluke file list.");
        }
    }
}
