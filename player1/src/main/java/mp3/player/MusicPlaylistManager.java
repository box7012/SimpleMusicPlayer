package mp3.player;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class MusicPlaylistManager {
    private ObservableList<String> playlist;
    private ListView<String> playlistView;

    public MusicPlaylistManager() {
        // Initialize playlist
        playlist = FXCollections.observableArrayList();
        playlistView = new ListView<>(playlist);

        // Handle drag-and-drop functionality
        playlistView.setOnDragOver(this::handleDragOver);
        playlistView.setOnDragDropped(this::handleDragDropped);
    }

    public ListView<String> getPlaylistView() {
        return playlistView;
    }

    public String getSelectedFile() {
        return playlistView.getSelectionModel().getSelectedItem();
    }

    public void addFiles(List<File> files) {
        for (File file : files) {
            if (file.getName().endsWith(".mp3")) {
                playlist.add(file.toURI().toString());
            }
        }
    }

    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != playlistView && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            addFiles(db.getFiles());
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }
}
