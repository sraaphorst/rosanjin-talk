package com.vorpal.rosanjintalk.view.management;

// By Sebastian Raaphorst, 2023.

import com.vorpal.rosanjintalk.shared.Shared;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.Objects;

/**
 * The SplashPanel at the top of the opening window.
 */
public final class SplashPanelView extends HBox {
    public SplashPanelView() {
        super(Shared.HORIZONTAL_SPACING);
        setPadding(Shared.PADDING);

        final ImageView rosanjinImageView;
        setAlignment(Pos.TOP_LEFT);
        try (final var rosanjinImageStream = getClass().getResourceAsStream("/rosanjin.jpeg")) {
            Objects.requireNonNull(rosanjinImageStream);
            final var rosanjinImage = new Image(rosanjinImageStream);
            rosanjinImageView = new ImageView(rosanjinImage);
        } catch (final IOException ex) {
            Shared.unrecoverableError("Could not load splash screen image.");
            return;
        }

        final var vBox = new VBox(Shared.VERTICAL_SPACING);
        final var rosanjinLabel = new Label(Shared.TITLE);
        rosanjinLabel.setFont(Font.font("Comic Sans MS", 30));
        final var authorLabel = new Label("By Sebastian Raaphorst, 2023.");
        final var userLabel = new Label("for smol boi");
        vBox.getChildren().addAll(rosanjinLabel, authorLabel, userLabel);
        getChildren().addAll(rosanjinImageView, vBox);
    }
}
