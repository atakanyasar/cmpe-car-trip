package solutions.mainsolution.Project3.src;

import java.util.ArrayList;

public class EpicBlend {

    public int[] numCategorySongs = new int[3];
    public ArrayList<SongHeap> minHeapOfAddedSongs = new ArrayList<>();
    public ArrayList<SongHeap> maxHeapOfNextAddableSongs = new ArrayList<>();

    public EpicBlend() {
        for (int i = 0; i < 3; i++) {
            minHeapOfAddedSongs.add(new SongHeap(true, i));
            maxHeapOfNextAddableSongs.add(new SongHeap(false, i));
        }
    }

    public void addSongIfPossible(int category) {
        while (!maxHeapOfNextAddableSongs.get(category).isEmpty()) {
            Song song = maxHeapOfNextAddableSongs.get(category).peek();
            if (minHeapOfAddedSongs.get(category).size() < numCategorySongs[category]) {
                addNextAddableSong(category);
            } else if (song.highScoredInCategory(category, minHeapOfAddedSongs.get(category).peek())) {
                removeWorstSong(category);
            } else {
                break;
            }
        }
    }

    private void addNextAddableSong(int category) {
        if(maxHeapOfNextAddableSongs.get(category).isEmpty()) {
            throw new RuntimeException("No next addable song");
        }
        Song song = maxHeapOfNextAddableSongs.get(category).poll();
        addSong(song, category);
        song.playlist.songAddedToEpicBlend(category);
    }

    private void removeWorstSong(int category) {
        Song song = minHeapOfAddedSongs.get(category).poll();

        if (Data.currentEvent.removedSongs[category] != null) {
            throw new RuntimeException("Removed song already exists");
        }
        Data.currentEvent.removedSongs[category] = song;
        song.inEpicBlend[category] = false;

        song.playlist.songRemovedFromEpicBlend(category);
    }

    public void addSong(Song song, int category) {
        if (minHeapOfAddedSongs.get(category).size() >= numCategorySongs[category]) {
            throw new RuntimeException("Category " + category + " is full");
        }
        minHeapOfAddedSongs.get(category).add(song);

        if (Data.currentEvent.addedSongs[category] != null) {
            throw new RuntimeException("Added song already exists");
        }
        Data.currentEvent.addedSongs[category] = song;
        song.inEpicBlend[category] = true;
    }

    public void removeSong(Song song, int category) {
        minHeapOfAddedSongs.get(category).remove(song);

        if (Data.currentEvent.removedSongs[category] != null) {
            throw new RuntimeException("Removed song already exists");
        }
        Data.currentEvent.removedSongs[category] = song;
        song.inEpicBlend[category] = false;
    }

    public void addToAddableSongs(Song song, int category) {
        maxHeapOfNextAddableSongs.get(category).add(song);
    }

    public void removeFromAddableSongs(Song song, int category) {
        maxHeapOfNextAddableSongs.get(category).remove(song);
    }

}
