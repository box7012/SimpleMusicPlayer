package mp3.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

import javafx.util.Duration;

public class MusicPlayer {
    private final MediaPlayer mediaPlayer;
    private final Media media;

    public MusicPlayer(String filePath) {
        media = new Media(new File(filePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void play() {
        mediaPlayer.play();
    }
 
    public void pause() {
        mediaPlayer.pause();
    }
    
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void forward() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }

    public void seek(Duration position) {
        // position이 비율일 경우, Duration에 해당하는 시간으로 계산
        double newTimeInMillis = position.toMillis();
        mediaPlayer.seek(javafx.util.Duration.millis(newTimeInMillis));
    }

    public Duration getCurrentTime() {
        return mediaPlayer.getCurrentTime();
    }

    public Duration getDuration() {
        return media.getDuration();
    }

}
