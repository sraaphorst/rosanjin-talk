package com.vorpal.rosanjintalk.ui;

// By Sebastian Raaphorst, 2023.

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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

    private final ListView<RosanjinTalkEntry> fileList = new ListView<>();

    public SplashScreen() {
        super();

        // *** CENTER PANE: FILE LIST ***
        fileList.setEditable(false);
        try {
            populateFiles();
        } catch (final URISyntaxException | IOException ex) {
            ex.getStackTrace();
        }
        setCenter(fileList);

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

        // *** BOTTOM PANE: BUTTONS ***
        final var bottom = new HBox(30);
        bottom.setAlignment(Pos.CENTER);

    }

    public void populateFiles() throws URISyntaxException, IOException {
        final var repo = RosanjinTalk.getPath();
        System.out.println("***REPO:***");
        System.out.println(repo);

        // The directory is a file instead of a path?
        if (repo.isFile()) {
            final var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Directory is corrupt");
            alert.setHeaderText(null);
            alert.setContentText("The directory:\n\n" + repo.getPath() + "\n\nthat is supposed to contain fluke files\n"
                    + "is not a directory!");
            alert.showAndWait();
            System.exit(1);
        }

        if (!repo.exists())
            repo.mkdirs();

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

        final ObservableList<RosanjinTalkEntry> lst = FXCollections.observableArrayList(result);
        fileList.setItems(lst);
    }
}
