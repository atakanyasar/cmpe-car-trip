import java.util.ArrayList;

public class Playlist {

    public int id;
    public int numSongsInPlaylist;
    public ArrayList<SongHeap> minHeapOfAddedSongs = new ArrayList<>();
    public ArrayList<SongHeap> maxHeapOfRemovedSongs = new ArrayList<>();

    public Playlist(int id, int numSongs) {
        this.id = id;
        this.numSongsInPlaylist = numSongs;

        for (int i = 0; i < 3; i++) {
            minHeapOfAddedSongs.add(new SongHeap(true, i));
            maxHeapOfRemovedSongs.add(new SongHeap(false, i));
        }
    }

    public void addSongToPlaylist(Song song) {
        for (int i = 0; i < 3; i++) {
            addSongToPlaylist(song, i);
        }
    }

    public void removeSongFromPlaylist(Song song) {
        for (int i = 0; i < 3; i++) {
            removeSongFromPlaylist(song, i);
        }
    }

    private boolean isSongSwappable(int category) {
        if (minHeapOfAddedSongs.get(category).size() != Data.categoryLimitInPlaylists) {
            return false;
        }
        return maxHeapOfRemovedSongs.get(category).peek().highScoredInCategory(category, minHeapOfAddedSongs.get(category).peek());
    }

    private boolean isPlaylistAddable(int category) {
        if (maxHeapOfRemovedSongs.get(category).isEmpty()) {
            return false;
        }
        return minHeapOfAddedSongs.get(category).size() < Data.categoryLimitInPlaylists;
    }

    public Song getNextAddableSong(int category) {
        return maxHeapOfRemovedSongs.get(category).peek();
    }

    private void addSongToPlaylist(Song song, int category) {
        if (isPlaylistAddable(category)){
            Data.epicBlend.removeFromAddableSongs(getNextAddableSong(category), category);
        }
        maxHeapOfRemovedSongs.get(category).add(song);
        if (isPlaylistAddable(category)) {
            Data.epicBlend.addToAddableSongs(getNextAddableSong(category), category);
        }

        if (isSongSwappable(category)) {
            swapSongInPlaylist(category);
        }

        Data.epicBlend.addSongIfPossible(category);
    }

    private void removeSongFromPlaylist(Song song, int category) {
        if (maxHeapOfRemovedSongs.get(category).isEmpty() || song.highScoredInCategory(category, getNextAddableSong(category))) {
            Data.epicBlend.removeSong(song, category);
            boolean isAddable = isPlaylistAddable(category);
            minHeapOfAddedSongs.get(category).remove(song);
            if (!isAddable && isPlaylistAddable(category)) {
                Data.epicBlend.addToAddableSongs(getNextAddableSong(category), category);
            }

            Data.epicBlend.addSongIfPossible(category);

        } else if (song.id == getNextAddableSong(category).id && isPlaylistAddable(category)) {
            Data.epicBlend.removeFromAddableSongs(getNextAddableSong(category), category);
            maxHeapOfRemovedSongs.get(category).poll();
            if (isPlaylistAddable(category)) {
                Data.epicBlend.addToAddableSongs(getNextAddableSong(category), category);
            }
        } else {
            maxHeapOfRemovedSongs.get(category).remove(song);
        }
    }

    public void swapSongInPlaylist(int category) {
        Song songRemoved = minHeapOfAddedSongs.get(category).poll();
        Data.epicBlend.removeSong(songRemoved, category);
        Song songAdded = maxHeapOfRemovedSongs.get(category).poll();
        Data.epicBlend.addSong(songAdded, category);
        minHeapOfAddedSongs.get(category).add(songAdded);
        maxHeapOfRemovedSongs.get(category).add(songRemoved);
    }

    public void songAddedToEpicBlend(int category) {
        Song song = maxHeapOfRemovedSongs.get(category).poll();
        minHeapOfAddedSongs.get(category).add(song);
        if (isPlaylistAddable(category)) {
            Data.epicBlend.addToAddableSongs(getNextAddableSong(category), category);
        }
    }

    public void songRemovedFromEpicBlend(int category) {
        if (isPlaylistAddable(category)) {
            Data.epicBlend.removeFromAddableSongs(getNextAddableSong(category), category);
        }
        if (minHeapOfAddedSongs.get(category).isEmpty()){
            throw new RuntimeException("Playlist is empty");
        }
        Song song = minHeapOfAddedSongs.get(category).poll();
        maxHeapOfRemovedSongs.get(category).add(song);
        Data.epicBlend.addToAddableSongs(getNextAddableSong(category), category);
    }

}
