import java.io.FileWriter;
import java.io.IOException;

public class Project3 {
    public static void main(String[] args) throws IOException {

            String songs_file = args[0];
            String input_file = args[1];
            String output_file = args[2];
            Data.fileWriter = new FileWriter(output_file);

            Data.readSongsFromFile(songs_file);
            Data.readTestCasesFromFile(input_file);

            Data.fileWriter.close();

    }
}