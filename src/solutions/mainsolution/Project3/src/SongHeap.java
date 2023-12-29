import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SongHeap {

    List<Song> array;
    HashMap<Song, Integer> index;
    int size;
    boolean isMinHeap;
    int category;

    public SongHeap(boolean isMinHeap, int category) {
        array = new ArrayList<>();
        index = new HashMap<>();
        size = 0;
        this.isMinHeap = isMinHeap;
        this.category = category;
        array.add(null);
    }

    boolean compare(Song s1, Song s2) {
        if (isMinHeap) {
            return s2.highScoredInCategory(category, s1);
        } else {
            return s1.highScoredInCategory(category, s2);
        }
    }

    int size() {
        return size;
    }

    int parent(int i) {
        return i / 2;
    }

    void swap(int i, int j) {
        Song temp = array.get(i);
        array.set(i, array.get(j));
        array.set(j, temp);
        index.put(array.get(i), i);
        index.put(array.get(j), j);
    }

    int left(int i) {
        return 2 * i;
    }

    int right(int i) {
        return 2 * i + 1;
    }

    public void add(Song song) {
        if (song == null) {
            throw new RuntimeException("Song is null");
        }
        array.add(song);
        size++;
        int i = size;
        if (index.containsKey(song)) {
            throw new RuntimeException("Song already in heap");
        }
        index.put(song, i);
        while (i > 1 && compare(array.get(i), array.get(parent(i)))) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    private void heapify(int i) {
        if (size == 0) {
            return;
        }
        int l = left(i);
        int r = right(i);
        int smallest = i;
        if (l <= size && compare(array.get(l), array.get(i))) {
            smallest = l;
        }
        if (r <= size && compare(array.get(r), array.get(smallest))) {
            smallest = r;
        }
        if (smallest != i) {
            swap(i, smallest);
            heapify(smallest);
        }
    }

    public void remove(Song song) {
        if (index.get(song) == null) {
            throw new RuntimeException("Song not in heap");
        }
        int i = index.get(song);

        swap(i, size);
        array.remove(size);
        size--;
        index.remove(song);
        if (i == size + 1) {
            return;
        }
        while (i > 1 && compare(array.get(i), array.get(parent(i)))) {
            swap(i, parent(i));
            i = parent(i);
        }
        heapify(i);
    }

    public Song peek() {
        if (size == 0) {
            throw new RuntimeException("Heap is empty");
        }
        return array.get(1);
    }

    public Song poll() {
        if (size == 0) {
            throw new RuntimeException("Heap is empty");
        }
        Song song = array.get(1);
        remove(song);
        return song;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean testHeap() {
        for (int i = 1; i <= size; i++) {
            int l = left(i);
            int r = right(i);
            if (l <= size && compare(array.get(l), array.get(i))) {
                throw new RuntimeException("Heap property violated");
            }
            if (r <= size && compare(array.get(r), array.get(i))) {
                throw new RuntimeException("Heap property violated");
            }
        }
        return true;
    }

}
