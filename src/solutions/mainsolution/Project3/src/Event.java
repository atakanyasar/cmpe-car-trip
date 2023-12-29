import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Event {

    int id;
    String type;
    Song song;
    Playlist playlist;
    Song[] addedSongs = new Song[3];
    Song[] removedSongs = new Song[3];

    public Event(int id, String type, Song song, Playlist playlist) {
        this.id = id;
        this.type = type;
        this.song = song;
        this.playlist = playlist;
        addedSongs[0] = addedSongs[1] = addedSongs[2] = null;
        removedSongs[0] = removedSongs[1] = removedSongs[2] = null;
    }

    public void executeEvent() throws IOException {
        switch (type) {
            case "ASK" -> {
                // printEvent();
            }
            case "REM" -> {
                if (song.playlist == null) {
                    throw new RuntimeException("Song " + song.id + " is not in a playlist");
                }
                playlist.removeSongFromPlaylist(song);
                song.playlist = null;
            }
            case "ADD" -> {
                if (song.playlist != null) {
                    throw new RuntimeException("Song " + song.id + " is already in playlist " + song.playlist.id);
                }
                song.playlist = playlist;
                playlist.addSongToPlaylist(song);
            }
            default -> throw new RuntimeException("Invalid event type: " + type);
        }
    }

    public void printEvent() throws IOException {
        if (type.equals("ASK")) {
            List<Song> addedSongsList = new ArrayList<>();
            for (int i = 0; i < Data.numSongs; i++) {
                if (Data.songs[i].inEpicBlend[0] || Data.songs[i].inEpicBlend[1] || Data.songs[i].inEpicBlend[2]) {
                    addedSongsList.add(Data.songs[i]);
                }
            }
            // sort by total streams from greatest to least
            addedSongsList = MergeSort.mergeSort(addedSongsList);

            for (int i = 0; i < addedSongsList.size(); i++) {
                Data.printOutputToFile(""+addedSongsList.get(i).id);
                if (i != addedSongsList.size() - 1) {
                    Data.printOutputToFile(" ");
                }
            }
            Data.printOutputToFile("\n");
            return;
        }
        for (int i = 0; i < 3; i++) {
            if (addedSongs[i] != null) {
                Data.printOutputToFile(""+addedSongs[i].id);
            } else {
                Data.printOutputToFile("0");
            }
            if (i != 2) {
                Data.printOutputToFile(" ");
            }
        }
        Data.printOutputToFile("\n");

        for (int i = 0; i < 3; i++) {
            if (removedSongs[i] != null) {
                Data.printOutputToFile(""+ removedSongs[i].id);
            } else {
                Data.printOutputToFile("0");
            }
            if (i != 2) {
                Data.printOutputToFile(" ");
            }
        }
        Data.printOutputToFile("\n");
    }

}
