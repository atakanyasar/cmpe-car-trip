import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class Grader {

    static String testCasesFolder = "test-cases";
    static String songsFile = testCasesFolder + "/songs.txt";
    static String inputsFolder = testCasesFolder + "/inputs";
    static String outputsFolder = testCasesFolder + "/outputs";
    static String submissionsPath = "src/solutions";
    static String mainSolutionName = "mainsolution";
    static String[] inputFiles = FilesUtil.getFilesInDirectoryWithExtension(Grader.inputsFolder, "txt");


    static public HashMap<String, Long> testCaseTimeLimits = new HashMap<>();
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

    public static void main(String[] args) throws IOException {

        Runner.measureTimeLimits(Grader.mainSolutionName, new FileWriter("src/solutions/mainsolution/log.txt"));
        FilesUtil.removeFolder("out");

        String[] outputFiles = FilesUtil.getFilesInDirectoryWithExtension(outputsFolder, "txt");
        FileWriter gradesFile = new FileWriter("grades.txt", true);

        for (String zipFile : FilesUtil.getFilesInDirectoryWithExtension("zips", "zip")) {
            if (zipFile.endsWith(".zip")) {
                String zipName = zipFile.substring(0, zipFile.length() - 4);
                if (Files.isDirectory(new File("src/graded/" + zipName).toPath())) {
                    continue;
                }
                if (Files.isDirectory(new File("src/solutions/" + zipName).toPath())) {
                    continue;
                }
                System.err.println("Unzipping " + zipFile);
                FilesUtil.unzip( "zips/" + zipFile, submissionsPath + "/" + zipName, "Project3/src");
            }
        }

        String[] submissions = FilesUtil.getFolders(submissionsPath);

        for (String submissionName : submissions) {
            if (submissionName.equals(mainSolutionName) || submissionName.equals("solution-cpp")) {
                continue;
            }

            System.out.println("Grading " + submissionName);
            FileWriter logFile = new FileWriter(submissionsPath + "/" + submissionName + "/log.txt");
            Runner.run(submissionName, logFile, false, false);

            String solutionOutputFolder = submissionsPath + "/" + submissionName + "/outputs";

            int totalPoints = 0;

            for (String outputFile : outputFiles) {
                String outputFileName = outputsFolder + "/" + outputFile;
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
            gradesFile.write(submissionName + ": " + totalPoints + "\n");
            System.out.println("Total points: " + totalPoints);

            logFile.close();

            FilesUtil.createFolderIfNotExists("src/graded");
            FilesUtil.removeFolder("out");

            if (submissionName.equals(mainSolutionName)) {
                continue;
            }

            Files.move(
                    new File(submissionsPath + "/" + submissionName).toPath(),
                    new File("src/graded/" + submissionName).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

        }

        gradesFile.close();

    }
}
