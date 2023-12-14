import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Grader {

    static HashMap<String, Integer> testCasePoints = new HashMap<>() {{
        put("ask_small.txt", 6);
        put("ask_large.txt", 4);
        put("one_playlist_small.txt", 9);
        put("one_playlist_large.txt", 6);
        put("tiny_playlists_small.txt", 9);
        put("tiny_playlists_large.txt", 6);
        put("ten_playlists_small.txt", 12);
        put("ten_playlists_large.txt", 8);
        put("add_small.txt", 12);
        put("add_large.txt", 8);
        put("general_small.txt", 12);
        put("general_large.txt", 8);
    }};

    static public HashMap<String, Integer> testCaseTimeLimits = new HashMap<>() {{
        put("ask_small.txt", 60);
        put("ask_large.txt", 60);
        put("one_playlist_small.txt", 60);
        put("one_playlist_large.txt", 60);
        put("tiny_playlists_small.txt", 60);
        put("tiny_playlists_large.txt", 60);
        put("ten_playlists_small.txt", 60);
        put("ten_playlists_large.txt", 60);
        put("add_small.txt", 60);
        put("add_large.txt", 60);
        put("general_small.txt", 60);
        put("general_large.txt", 60);
    }};

    public static String[] getFolders(String path) {
        return new File(path).list((current, name) -> new File(current, name).isDirectory());
    }

    public static String[] getFilesInDirectory(String directory, String extension) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isFile() || !Runner.getFileExtension(listOfFiles[i].getName()).equals(extension)) {
                continue;
            }
            fileNames[i] = listOfFiles[i].getName();
        }
        return  fileNames;
    }

    public static boolean diffChecker(String outputFileName, String expectedOutputFileName) {
        StringBuilder outputBuffer = new StringBuilder();
        StringBuilder expectedOutputBuffer = new StringBuilder();

        try {
            BufferedReader outputReader = new BufferedReader(new FileReader(outputFileName));
            BufferedReader expectedOutputReader = new BufferedReader(new FileReader(expectedOutputFileName));

            String line;
            while ((line = outputReader.readLine()) != null) {
                outputBuffer.append(line.trim());
                outputBuffer.append("\n");
            }

            while ((line = expectedOutputReader.readLine()) != null) {
                expectedOutputBuffer.append(line.trim());
                expectedOutputBuffer.append("\n");
            }

            outputReader.close();
            expectedOutputReader.close();
        } catch (Exception e) {
            System.out.println("Error reading output file: " + e);
            return false;
        }

        return outputBuffer.toString().contentEquals(expectedOutputBuffer);
    }

    private static void unzip(String zipFilePath, String destDir, String specificFolder) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {

            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                if (!entryName.startsWith(specificFolder) || !entryName.endsWith(".java")) {
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }
                try {
                    Runner.createAllParentFolders(destDir + "/" + entryName);
                    File newFile = new File(destDir + "/" + entryName);
                    if (!zipEntry.isDirectory()) {
                        Files.copy(zipInputStream, newFile.toPath());
                        System.out.println("Extracted: " + newFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    System.out.println("Error extracting file: " + e);
                    e.printStackTrace();
                }

                zipEntry = zipInputStream.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        boolean run_main_solution = false;
        if (args.length > 0) {
            run_main_solution = args[0].equals("main");
        }
        FileWriter gradesFile = new FileWriter("grades.txt", true);
        String testCasesFolder = "test-cases";

        String outputFolder = testCasesFolder + "/outputs";

        String[] outputFiles = getFilesInDirectory(outputFolder, "txt");

        String path = "src/solutions";

        for (String zipFile : getFilesInDirectory("zips", "zip")) {
            if (zipFile.endsWith(".zip")) {
                String zipName = zipFile.substring(0, zipFile.length() - 4);
                if (Files.isDirectory(new File("src/graded/" + zipName).toPath())) {
                    continue;
                }
                if (Files.isDirectory(new File("src/solutions/" + zipName).toPath())) {
                    continue;
                }
                System.err.println("Unzipping " + zipFile);
                unzip( "zips/" + zipFile, path + "/" + zipName, "Project3/src");
            }
        }

        String[] folders = getFolders(path);

        for (String folder : folders) {
            if ((folder.equals("mainsolution") && !run_main_solution) || folder.equals("solution-cpp")) {
                continue;
            }

            System.out.println("Grading " + folder);
            FileWriter logFile = new FileWriter(path + "/" + folder + "/log.txt");
            Runner.run(folder, logFile, false);

            String solutionOutputFolder = path + "/" + folder + "/outputs";

            int totalPoints = 0;

            for (String outputFile : outputFiles) {
                String outputFileName = outputFolder + "/" + outputFile;
                String expectedOutputFileName = solutionOutputFolder + "/" +  outputFile ;

                if (diffChecker(outputFileName, expectedOutputFileName)) {
                    logFile.write("Test case " + outputFile + " passed\n");
                    System.out.println("Test case " + outputFile + " passed");
                    totalPoints += testCasePoints.get(outputFile);
                } else {
                    logFile.write("Test case " + outputFile + " failed\n");
                    System.out.println("Test case " + outputFile + " failed");
                }
            }

            logFile.write("Total points: " + totalPoints + "\n");
            gradesFile.write(folder + ": " + totalPoints + "\n");
            System.out.println("Total points: " + totalPoints);

            logFile.close();

            Runner.createFolderIfNotExists("src/graded");

            if (folder.equals("mainsolution")) {
                continue;
            }

            Files.move(
                    new File(path + "/" + folder).toPath(),
                    new File("src/graded/" + folder).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

        }

        gradesFile.close();

    }
}
