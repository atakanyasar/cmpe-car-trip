import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.min;

public class InputGenerator {

    HashSet<String> songNames = new HashSet<>();
    static Random random = new Random();

    static String songsFile = "test-cases/songs.txt";

    static String inputsFolder = "test-cases/inputs";
    static String outputsFolder = "test-cases/outputs";

    private class Song {

        public int id;
        public String title;
        public int totalStreams;
        public int[] categoryScore = new int[3];
        public boolean[] inEpicBlend = new boolean[3];

        public Song(String line) {
            String[] elements = line.split(" ");
            id = Integer.parseInt(elements[0]);
            title = elements[1];
            totalStreams = Integer.parseInt(elements[2]);
            categoryScore[0] = Integer.parseInt(elements[3]);
            categoryScore[1] = Integer.parseInt(elements[4]);
            categoryScore[2] = Integer.parseInt(elements[5]);
            inEpicBlend[0] = false;
            inEpicBlend[1] = false;
            inEpicBlend[2] = false;
        }

        public boolean highScoredInCategory(int category, Song song) {
            return (categoryScore[category] == song.categoryScore[category] && this.title.compareTo(song.title) < 0) ||
                    categoryScore[category] > song.categoryScore[category];
        }

    }

    private static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private String randomString(int length) {
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomString.append((char) (randomInt(0, 25) + 97));
        }
        if (songNames.contains(randomString.toString())) {
            return randomString(length);
        }
        songNames.add(randomString.toString());
        return randomString.toString();
    }

    private List<Integer> getSortedSongs(String songs_file, int numSongs, int category) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(songs_file));
        int tmpNumSongs = Integer.parseInt(reader.readLine());
        List<Song> songs = new ArrayList<>();
        List<Integer> songIds = new ArrayList<>();

        for (int i = 0; i < numSongs; i++) {
            String songInfo = reader.readLine();
            Song song = new Song(songInfo);
            songs.add(song);
            songIds.add(song.id);
        }

        songIds.sort((o1, o2) -> {
            Song song1 = songs.get(o1 - 1);
            Song song2 = songs.get(o2 - 1);
            if (song1.categoryScore[category] == song2.categoryScore[category]) {
                return song1.title.compareTo(song2.title);
            }
            return song2.categoryScore[category] - song1.categoryScore[category];
        });

        return songIds;
    }

    private void createSongs(String songs_file, int numSongs) throws IOException {
        StringBuilder buffer = new StringBuilder();

        buffer.append(numSongs).append("\n");

        for (int i = 0; i < numSongs; i++) {
            buffer.append((i + 1)).append(" ");
            buffer.append(randomString(5)).append(" ");
            buffer.append(randomInt(0, 10000)).append(" ");
            buffer.append(randomInt(0, 100)).append(" ");
            buffer.append(randomInt(0, 100)).append(" ");
            buffer.append(randomInt(0, 100)).append("\n");
        }

        try {
            FileWriter fileWriter = new FileWriter(songs_file);
            fileWriter.write(buffer.toString());
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing to file: " + songs_file);
            System.exit(1);
        }
    }
    private void randomTestCaseCreator(String input_file, int numSongs, int numPlaylists, int maxSongsInPlaylist, int l, int[] c, int q, boolean onlyAdd, boolean maxSongsInPlaylistRestricted) throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(l).append(" ").append(c[0]).append(" ").append(c[1]).append(" ").append(c[2]).append("\n");
        buffer.append(numPlaylists).append("\n");

        HashSet<Integer> remainingSongIds = new HashSet<>();
        HashMap<Integer, Integer> songIdToPlaylistId = new HashMap<>();
        HashMap<Integer, Integer> playlistIdToNumSongs = new HashMap<>();

        for (int i = 1; i <= numSongs; i++) {
            remainingSongIds.add(i);
        }

        ArrayList<List<Integer>> sortedSongs = new ArrayList<>();
        sortedSongs.add(getSortedSongs(songsFile, numSongs, 0));
        sortedSongs.add(getSortedSongs(songsFile, numSongs, 1));
        sortedSongs.add(getSortedSongs(songsFile, numSongs, 2));


        System.out.println("Creating playlists...");

        for (int i = 1; i <= numPlaylists; i++) {
            int numSongsInPlaylist = min(remainingSongIds.size(), maxSongsInPlaylist);
            numSongsInPlaylist = randomInt((numSongsInPlaylist+1)/2, numSongsInPlaylist);

            playlistIdToNumSongs.put(i, numSongsInPlaylist);
            buffer.append(i).append(" ").append(numSongsInPlaylist).append("\n");

            for (int j = 0; j < numSongsInPlaylist; j++) {
                int songId = randomInt(1, numSongs);
                while (!remainingSongIds.contains(songId)) {
                    songId = randomInt(1, numSongs);
                }
                remainingSongIds.remove(songId);
                songIdToPlaylistId.put(songId, i);
                buffer.append(songId).append(" ");
            }
            buffer.append("\n");

        }

        System.out.println("Creating events...");

        buffer.append(q).append("\n");

        if (!onlyAdd)
            buffer.append("ASK\n");
        else
            q += 2;

        int askCount = 3;

        for (int i = 0; i < q-2; i++) {
            if (!onlyAdd && askCount > 0 && randomInt(1, q/2) == 1) {
                buffer.append("ASK\n");
                askCount--;
                continue;
            }
            int category = randomInt(0, 2);
            int songIndex = randomInt(0, min(c[category], numSongs) - 1);
            int songId;
            if (randomInt(1, 2) == 1) {
                songId = sortedSongs.get(category).get(songIndex);
            } else {
                songId = randomInt(1, numSongs);
            }
            if (remainingSongIds.contains(songId)) {
                int playlistId = randomInt(1, numPlaylists);
                if (maxSongsInPlaylistRestricted && playlistIdToNumSongs.get(playlistId) >= maxSongsInPlaylist) {
                    i--;
                    continue;
                }
                buffer.append("ADD ").append(songId).append(" ").append(playlistId).append("\n");
                remainingSongIds.remove(songId);
                songIdToPlaylistId.put(songId, playlistId);
                playlistIdToNumSongs.put(playlistId, playlistIdToNumSongs.get(playlistId) + 1);
            } else if(!onlyAdd) {
                buffer.append("REM ").append(songId).append(" ").append(songIdToPlaylistId.get(songId)).append("\n");
                remainingSongIds.add(songId);
                playlistIdToNumSongs.put(songIdToPlaylistId.get(songId), playlistIdToNumSongs.get(songIdToPlaylistId.get(songId)) - 1);
                songIdToPlaylistId.remove(songId);
            } else {
                i--;
            }
        }
        if (q > 1 && !onlyAdd)
            buffer.append("ASK\n");

        System.out.println("Writing to file...");

        try {
            FileWriter fileWriter = new FileWriter(input_file);
            fileWriter.write(buffer.toString());
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing to file: " + input_file);
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        random.setSeed(System.currentTimeMillis());
        InputGenerator inputGenerator = new InputGenerator();
        if (false) {
            inputGenerator.createSongs(songsFile, 1000000);
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/ask_small.txt", 50, 5, 10, 3, new int[]{3, 3, 4}, 1, false, false
            );
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/ask_large.txt", 1000000, 1000, 1000, 100, new int[]{100, 10000, 500000}, 1, false, false
            );
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/one_playlist_small.txt", 50, 1, 100, 30, new int[]{35, 15, 5}, 100, false, false
            );
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/one_playlist_large.txt", 1000000, 1, 1000000, 400000, new int[]{100, 500000, 10000}, 1000000, false, false
            );
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/ten_playlists_small.txt", 100, 10, 100, 75, new int[]{85, 40, 5}, 100, false, false
            );
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/ten_playlists_large.txt", 1000000, 10, 200000, 50000, new int[]{100, 600000, 100000}, 1000000, false, false
            );

            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/general_small.txt", 100, 5, 10, 10, new int[]{10, 25, 75}, 100, false, false
            );
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/general_large.txt", 1000000, 1000, 1000, 300, new int[]{300, 30000, 300000}, 1000000, false, false
            );
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/add_small.txt", 50, 5, 0, 10, new int[]{4, 20, 50}, 50, true, false
            );
            inputGenerator.randomTestCaseCreator(
                    inputsFolder + "/add_large.txt", 1000000, 1000, 0, 300, new int[]{300, 10000, 500000}, 1000000, true, false
            );
        }

        inputGenerator.randomTestCaseCreator(
                inputsFolder + "/tiny_playlists_small.txt", 50, 20, 2, 1, new int[]{2, 10, 35}, 50, false, true
        );
        inputGenerator.randomTestCaseCreator(
                inputsFolder + "/tiny_playlists_large.txt", 1000000, 1000000, 10, 3, new int[]{100, 10000, 500000}, 1000000, false, true
        );

//        inputGenerator.randomTestCaseCreator(inputsFolder + "/testcase00.txt", 100, 10, 3, 30, 30, 30, 100);
//        inputGenerator.randomTestCaseCreator(inputsFolder + "/testcase01.txt", 2000, 100, 10, 100, 100, 100, 1000);
//        inputGenerator.randomTestCaseCreator(inputsFolder + "/testcase03.txt", 1000000, 1000, 30, 50000, 1000000, 100, 500000);
 /*       for (int i = 0; i < 10; i++) {
            inputGenerator.randomTestCaseCreator(inputsFolder + String.format("/small%02d.txt", i),
                    20,
                    randomInt(1, 5),
                    randomInt(1, 5),
                    randomInt(1, 20),
                    randomInt(1, 20),
                    randomInt(1, 20),
                    randomInt(10, 30),
                    false);
        }*/
      /*  for (int i = 5; i < 30; i++) {
            int numSongs = randomInt(10, 50);
            int numPlaylists = randomInt(1, 8);
            inputGenerator.randomTestCaseCreator(inputsFolder + String.format("/small%02d.txt", i),
                    numSongs,
                    numPlaylists,
                    randomInt(1, 10),
                    randomInt(1, numSongs),
                    randomInt(1, numSongs),
                    randomInt(1, numSongs),
                    randomInt(20, 50),
                    false);
        }*/
        // inputs/songs.txt inputs/testcase00.txt outputs/output00.txt
        // sample/songs.txt sample/inputs/1.txt sample/outputs/1.txt
    }

}
