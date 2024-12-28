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

    public void seekTo(double position) {
        mediaPlayer.seek(media.getDuration().multiply(position));
    }

    public Duration getCurrentTime() {
        return mediaPlayer.getCurrentTime();
    }

    public Duration getDuration() {
        return media.getDuration();
    }
}
