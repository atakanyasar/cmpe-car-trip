package solutions.mainsolution.Project3.src;

public class Song {

    public int id;
    public String title;
    public int totalStreams;
    public int[] categoryScore = new int[3];
    public Playlist playlist;
    public boolean[] inEpicBlend = new boolean[3];

    public Song(String line) {
        String[] elements = line.split(" ");
        id = Integer.parseInt(elements[0]);
        title = elements[1];
        totalStreams = Integer.parseInt(elements[2]);
        categoryScore[0] = Integer.parseInt(elements[3]);
        categoryScore[1] = Integer.parseInt(elements[4]);
        categoryScore[2] = Integer.parseInt(elements[5]);
        playlist = null;
        inEpicBlend[0] = false;
        inEpicBlend[1] = false;
        inEpicBlend[2] = false;
    }

    public boolean highScoredInCategory(int category, Song song) {
        return (categoryScore[category] == song.categoryScore[category] && this.title.compareTo(song.title) < 0) ||
                categoryScore[category] > song.categoryScore[category];
    }

}
