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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SplashScreen extends BorderPane {
    public record RosanjinTalkEntry(String name, String path) implements Comparable<RosanjinTalkEntry> {
        @Override
        public int compareTo(RosanjinTalkEntry other) {
            return name.compareTo(other.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // The file list.
    private final ObservableList<RosanjinTalkEntry> files = FXCollections.observableArrayList();

    public SplashScreen() {
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
        try {
            populateFiles();
        } catch (final URISyntaxException | IOException ex) {
            ex.getStackTrace();
        }

        final ListView<RosanjinTalkEntry> fileList = new ListView<>(files);
        fileList.setEditable(false);

        final var scrollPane = new ScrollPane(fileList);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);

        // *** BOTTOM PANE: BUTTONS ***
        final var buttonPlay = new Button("Play");
        buttonPlay.setDisable(true);
        final var buttonNew = new Button("New");
        final var buttonAdd = new Button("Add");
        final var buttonDelete = new Button("Delete");
        buttonDelete.setDisable(true);
        final var buttonFiles = new Button("Files");

        buttonFiles.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(RosanjinTalk.getFlukePath());
            } catch (final IOException ex) {
                RosanjinTalk.unrecoverableError("Could not open file viewer.");
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

    public void populateFiles() throws URISyntaxException, IOException {
        final var repo = RosanjinTalk.getFlukePath();
        System.out.println("***REPO:***");
        System.out.println(repo);

        final List<RosanjinTalkEntry> result;
        // Now read in the FLUKE files from the directory and populate the lst.
        try (final var walk = Files.walk(Paths.get(repo.toURI()), 1)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith(".fluke"))
                    .map(f -> {
                        final int filenameIdx = f.lastIndexOf(File.separator);
                        final String filename = f.substring(filenameIdx + 1);
                        return new RosanjinTalkEntry(filename, f);
                    })
                    .sorted()
                    .toList();
        }

        files.setAll(result);
    }
}
