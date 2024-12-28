package mp3.player;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MusicPlayerUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        String filePath = "C:/Users/box70/Desktop/Java_Programs/dltkdgksep/moonlightdrive.mp3"; 
        MusicPlayer musicPlayer = new MusicPlayer(filePath);

        // Play Button
        Button playButton = new Button("Play");
        playButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        playButton.setOnAction(e -> musicPlayer.play());

        // Pause Button
        Button pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        pauseButton.setOnAction(e -> musicPlayer.pause());

        // 10 Seconds Forward Button
        Button forwardButton = new Button("10 Seconds Forward");
        forwardButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        forwardButton.setOnAction(e -> musicPlayer.forward());

        // Progress Bar
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setStyle("-fx-accent: #4CAF50;");

        // Update Progress Bar while media is playing
        musicPlayer.getMediaPlayer().currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!progressBar.isPressed()) {
                progressBar.setProgress(newTime.toMillis() / musicPlayer.getDuration().toMillis());
            }
        });

        // Make the ProgressBar interactive
        progressBar.setOnMouseClicked(e -> {
            double clickPosition = e.getX() / progressBar.getWidth();
            musicPlayer.seekTo(clickPosition);
        });

        // Layout
        HBox controls = new HBox(10, playButton, pauseButton, forwardButton);
        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, controls, progressBar);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20px;");

        // Scene
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Music Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}