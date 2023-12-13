package solutions.mainsolution.src;

import java.util.ArrayList;
import java.util.List;

public class MergeSort {

    public static int compareStreams(Song s1, Song s2) {
        if (s1.totalStreams == s2.totalStreams) {
            return s1.title.compareTo(s2.title);
        }
        return s2.totalStreams - s1.totalStreams;
    }


   public static List<Song> merge(List<Song> left, List<Song> right) {
        int leftIndex = 0;
        int rightIndex = 0;
        List<Song> merged = new ArrayList<>();
        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (compareStreams(left.get(leftIndex), right.get(rightIndex)) < 0) {
                merged.add(left.get(leftIndex));
                leftIndex++;
            } else {
                merged.add(right.get(rightIndex));
                rightIndex++;
            }
        }
        while (leftIndex < left.size()) {
            merged.add(left.get(leftIndex));
            leftIndex++;
        }
        while (rightIndex < right.size()) {
            merged.add(right.get(rightIndex));
            rightIndex++;
        }
        return merged;
    }

    public static List<Song> mergeSort(List<Song> songs) {
        if (songs.size() <= 1) {
            return songs;
        }
        int mid = songs.size() / 2;
        List<Song> left = songs.subList(0, mid);
        List<Song> right = songs.subList(mid, songs.size());
        left = mergeSort(left);
        right = mergeSort(right);
        return merge(left, right);
   }

}
