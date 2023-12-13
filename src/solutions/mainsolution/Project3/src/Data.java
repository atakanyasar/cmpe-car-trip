package solutions.mainsolution.src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Data {

    public static int numSongs;
    public static Song[] songs;
    public static int numPlaylists;
    public static Playlist[] playlists;
    public static int numEvents;
    public static int categoryLimitInPlaylists;
    public static EpicBlend epicBlend = new EpicBlend();
    public static Event currentEvent;
    public static FileWriter fileWriter;

    public static void readSongsFromFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            Data.numSongs = Integer.parseInt(line);
            Data.songs = new Song[Data.numSongs];
            for (int i = 0; i < Data.numSongs; i++) {
                line = reader.readLine();
                Data.songs[i] = new Song(line);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading from file: " + filename);
            System.exit(1);
        }
    }

    public static void readTestCasesFromFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            String[] elements = line.split(" ");

            Data.categoryLimitInPlaylists = Integer.parseInt(elements[0]);
            Data.epicBlend.numCategorySongs[0] = Integer.parseInt(elements[1]);
            Data.epicBlend.numCategorySongs[1] = Integer.parseInt(elements[2]);
            Data.epicBlend.numCategorySongs[2] = Integer.parseInt(elements[3]);

            line = reader.readLine();
            Data.numPlaylists = Integer.parseInt(line);
            Data.playlists = new Playlist[Data.numPlaylists];

            for (int i = 0; i < numPlaylists; i++) {
                line = reader.readLine();
                elements = line.split(" ");

                int playlistId = Integer.parseInt(elements[0]);
                int numSongsInPlaylist = Integer.parseInt(elements[1]);
                Data.playlists[i] = new Playlist(playlistId, numSongsInPlaylist);

                line = reader.readLine();
                elements = line.split(" ");

                for (int j = 0; j < numSongsInPlaylist; j++) {
                    Song song = Data.songs[Integer.parseInt(elements[j]) - 1];
                    currentEvent = new Event(-1, "ADD", song, playlists[i]);
                    currentEvent.executeEvent();
                }
            }

            line = reader.readLine();
            Data.numEvents = Integer.parseInt(line);

            for (int i = 0; i < numEvents; i++) {
                line = reader.readLine();
                elements = line.split(" ");

                switch (elements[0]) {
                    case "ASK" -> {
                        currentEvent = new Event(i, "ASK", null, null);
                    }
                    case "REM" -> {
                        Song song = Data.songs[Integer.parseInt(elements[1]) - 1];
                        Playlist playlist = song.playlist;
                        currentEvent = new Event(i, "REM", song, playlist);
                    }
                    case "ADD" -> {
                        Song song = Data.songs[Integer.parseInt(elements[1]) - 1];
                        Playlist playlist = Data.playlists[Integer.parseInt(elements[2]) - 1];
                        currentEvent = new Event(i, "ADD", song, playlist);
                    }
                    default -> throw new RuntimeException("Invalid event type: " + elements[0]);
                }
                currentEvent.executeEvent();
                currentEvent.printEvent();
            }

            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printOutputToFile(String output) throws IOException {
        fileWriter.write(output);
    }

}