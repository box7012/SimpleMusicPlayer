package mp3.player;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerUI extends Application {
    private MusicPlayer musicPlayer;
    private List<File> musicFiles = new ArrayList<>();  // 파일 목록 저장
    private ListView<String> musicListView = new ListView<>();  // 음악 목록 표시
    private ProgressBar progressBar = new ProgressBar(0);  // ProgressBar 추가
    private Slider volumeSlider = new Slider(0, 1, 0.5);
    private File selectedFile;
    private double startPoint = -1; // 시작 지점 (초)
    private double endPoint = -1;   // 끝 지점 (초)
    private Button saveTrimmedButton;
    
    
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

        saveTrimmedButton = new Button("Save Selected Part");
        saveTrimmedButton.setDisable(true); // 파일을 선택하지 않으면 버튼 비활성화

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
                selectedFile = new File(selectedFilePath);  // selectedFile을 현재 선택된 파일로 설정
                
                // 이전 음악이 있다면 멈추기
                if (musicPlayer != null) {
                    musicPlayer.stop();  // 이전 음악 정지
                }
                
                // 새로운 음악 파일로 음악 플레이어 업데이트
                musicPlayer = new MusicPlayer(selectedFilePath);
                
                // Play 버튼 활성화
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

        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setMajorTickUnit(0.2);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setValue(0.5);

        // 볼륨 슬라이더 리스너
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (musicPlayer != null) {
                musicPlayer.getMediaPlayer().setVolume(newValue.doubleValue()); // 볼륨을 슬라이더 값에 맞게 설정
            }
        });
    
        // 선택된 구간 저장 버튼
        saveTrimmedButton.setOnAction(e -> {
            if (selectedFile != null && startPoint >= 0 && endPoint > startPoint) {
                saveTrimmedMP3(selectedFile, startPoint, endPoint);
            } else {
                showAlert("Error", "Invalid selection range. Please select a valid range.");
            }
        });

        progressBar.setOnMouseClicked(this::handleProgressBarClick);

        // Layout
        HBox controls = new HBox(10, playButton, pauseButton, progressBar, saveTrimmedButton);
        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, chooseFileButton, musicListView, controls, volumeSlider); // ProgressBar 위치 수정
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:rgb(219, 162, 162); -fx-padding: 20px;");

        // Scene
        Scene scene = new Scene(root, 500, 500);

        // css 파일 적용시키는 법
        // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
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


    // ProgressBar 클릭 이벤트 처리
    private void handleProgressBarClick(MouseEvent e) {
        if (musicPlayer != null && musicPlayer.getDuration() != null) {
            double clickPosition = e.getX() / progressBar.getWidth();  // 클릭 위치의 비율 (0.0 ~ 1.0)
            double durationInSeconds = musicPlayer.getDuration().toSeconds();  // 실제 음악의 길이 (초 단위)
            double clickedTime = clickPosition * durationInSeconds;  // 클릭된 위치의 시간 (초 단위)

            if (e.isControlDown()) {
                // Ctrl 키가 눌린 상태: 구간 설정
                startPoint = clickedTime;  // 시작 지점 설정
                System.out.println(startPoint);
   
            } else if (e.isAltDown()) {
                if (clickedTime > startPoint) {
                    endPoint = clickedTime;  // 끝 지점 설정
                    System.out.println(endPoint);
                }
            } else {
                // Ctrl 키가 눌리지 않은 상태: 음악 위치 이동
                musicPlayer.seek(javafx.util.Duration.seconds(clickedTime));
            }
        }

        if (startPoint != -1 && endPoint != -1) {
            saveTrimmedButton.setDisable(false);  // Save 버튼 활성화
            updateProgressBarVisual();  // ProgressBar 시각적 업데이트
        }
    }


    // ProgressBar 시각적 업데이트
    private void updateProgressBarVisual() {
        // if (startPoint >= 0 && endPoint > startPoint) {
        if (startPoint >= 0) {
            double startRatio = startPoint / musicPlayer.getDuration().toSeconds(); // 음악의 길이에 맞춰 비율 계산
            double endRatio = endPoint / musicPlayer.getDuration().toSeconds();

            // startRatio와 endRatio를 사용하여 구간을 표시할 수 있도록 스타일 업데이트
            progressBar.setStyle(String.format(
                "-fx-accent: linear-gradient(to right, #4CAF50 %.2f%%, #FF0000 %.2f%%, #4CAF50 %.2f%%);", 
                startRatio * 100, startRatio * 100, endRatio * 100));
        }
    }
    // FFmpeg를 사용해 MP3 구간 저장
    private void saveTrimmedMP3(File inputFile, double start, double end) {
        File outputFile = new File(inputFile.getParent(), "trimmed_" + inputFile.getName());
        String startStr = String.format("%.2f", start);
        String durationStr = String.format("%.2f", end - start);

        String command = String.format("ffmpeg -i \"%s\" -ss %s -t %s -c copy \"%s\"",
                inputFile.getAbsolutePath(), startStr, durationStr, outputFile.getAbsolutePath());

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            showAlert("Success", "Trimmed file saved at: " + outputFile.getAbsolutePath());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to trim the MP3 file.");
        }
    }

    // 경고창 표시
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }




    public static void main(String[] args) {
        launch(args);
    }
}