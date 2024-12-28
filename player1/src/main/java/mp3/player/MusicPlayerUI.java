package mp3.player;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerUI extends Application {
    private MusicPlayer musicPlayer;
    private List<File> musicFiles = new ArrayList<>();  // 파일 목록 저장
    private ListView<String> musicListView = new ListView<>();  // 음악 목록 표시
    private ProgressBar progressBar = new ProgressBar(0);  // ProgressBar 추가

    @Override
    public void start(Stage primaryStage) {
        // Choose MP3 Button
        Button chooseFileButton = new Button("Choose MP3 File");
        chooseFileButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        // Play Button
        Button playButton = new Button("Play");
        playButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        playButton.setDisable(true);

        // Pause Button
        Button pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        pauseButton.setDisable(true);

        // FileChooser to select MP3 file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));

        // ProgressBar 스타일
        progressBar.setStyle("-fx-accent: #4CAF50;");

        // Choose File Button action
        chooseFileButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                addFileToPlaylist(selectedFile);
            }
        });

        // Handle drag-and-drop event to add files
        musicListView.setOnDragOver(event -> {
            if (event.getGestureSource() != musicListView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        musicListView.setOnDragDropped(event -> {
            var db = event.getDragboard();
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    addFileToPlaylist(file);
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

        // Handle music item selection and play
        musicListView.setOnMouseClicked(e -> {
            if (musicListView.getSelectionModel().getSelectedItem() != null) {
                String selectedFilePath = musicListView.getSelectionModel().getSelectedItem();
                musicPlayer = new MusicPlayer(selectedFilePath);
                playButton.setDisable(false);
                pauseButton.setDisable(false);

                // Bind progress bar to the MediaPlayer's current time
                musicPlayer.getMediaPlayer().currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (!progressBar.isPressed() && musicPlayer.getDuration() != null) {
                        progressBar.setProgress(newTime.toMillis() / musicPlayer.getDuration().toMillis());
                    }
                });
            }
        });

        // Play Button action
        playButton.setOnAction(e -> {
            if (musicPlayer != null) {
                musicPlayer.play();
            }
        });

        // Pause Button action
        pauseButton.setOnAction(e -> {
            if (musicPlayer != null) {
                musicPlayer.pause();
            }
        });

        // Layout
        HBox controls = new HBox(10, playButton, pauseButton);
        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, chooseFileButton, musicListView, controls, progressBar); // ProgressBar 위치 수정
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20px;");

        // Scene
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("Music Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addFileToPlaylist(File file) {
        // Add file to list and update ListView
        if (file != null && file.getName().endsWith(".mp3")) {
            musicFiles.add(file);
            musicListView.getItems().add(file.getAbsolutePath()); // Display file path in ListView
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
