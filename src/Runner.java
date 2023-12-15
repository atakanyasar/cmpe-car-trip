import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class Runner {

    public static String getFileExtension(String fileName) {
        String[] fileNameParts = fileName.split("\\.");
        return fileNameParts[fileNameParts.length - 1];
    }
    public static String[] getFilesInDirectory(String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isFile() || !getFileExtension(listOfFiles[i].getName()).equals("txt")) {
                continue;
            }
            fileNames[i] = listOfFiles[i].getName();
        }
        return  fileNames;
    }

    public static void createFolderIfNotExists(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static void createAllParentFolders(String fileName) {
        String[] fileNameParts = fileName.split("/");
        String parentFolder = fileNameParts[0];
        createFolderIfNotExists(parentFolder);
        for (int i = 1; i < fileNameParts.length - 1; i++) {
            parentFolder += "/" + fileNameParts[i];
            createFolderIfNotExists(parentFolder);
        }
    }

    private static String getOutputFileName(String inputFileName, String outputFolder) {
        String[] inputFileNameParts = inputFileName.split("/");
        String outputFileName = inputFileNameParts[inputFileNameParts.length - 1];
        return outputFolder + "/" + outputFileName;
    }

    private static boolean compileSolution(String source, FileWriter logFile) {
        ProcessBuilder processBuilder;
        if (source.endsWith("-cpp")) {
            processBuilder = new ProcessBuilder("g++", "-std=c++11", source + "/main.cpp", "-o", "main");
        } else {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                processBuilder = new ProcessBuilder("javac", source + "/Project3/src/*.java", "-d", "out");
            } else {
                processBuilder = new ProcessBuilder("sh", "-c",
                        "find " + source + "/Project3/src" + " -name '*.java' -exec javac -d out" + " {} +");
            }
        }
        try {
            if (!Files.exists(Path.of("out/"))) {
                Files.createDirectory(new File("out").toPath());
            }
            Process process = processBuilder.start();
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                logFile.write(line + "\n");
            }
            reader.close();

            if (process.exitValue() != 0) {
                logFile.write("Error compiling solution: " + processBuilder.command() + "\n");
                System.out.println("Error compiling solution: " + processBuilder.command());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error compiling solution: " + e);
            return false;
        }
        return true;
    }

    private static void runSolution(String source, String[] solutionArgs, FileWriter logFile, String testCase, int timeLimitSeconds) {
        String command;
        if (source.endsWith("-cpp")) {
            command = "Project3 " + String.join(" ", solutionArgs);
        } else {
            command = "java " + "-cp out " + "solutions." + source + ".Project3.src.Project3 " + String.join(" ", solutionArgs);
        }
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Process process = Runtime.getRuntime().exec(command);
            if (!process.waitFor(timeLimitSeconds, TimeUnit.SECONDS)) {
                process.destroy();
                logFile.write("Time limit exceed: " + testCase + "\n");
                System.out.println("Time limit exceed: " + testCase);
            } else if (process.exitValue() != 0) {
                logFile.write("Error running solution: " + testCase + "\n");
                System.out.println("Error running solution: " + testCase);

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    logFile.write(line + "\n");
                }
                reader.close();

                return;
            }
            logFile.write("Time taken: " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + "ms\n");
            System.out.println("Time taken: " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + "ms");

        } catch (Exception e) {
            System.err.println("Error running solution: " + e);
        }
    }

    public static void run(String solutionName, FileWriter logFile, boolean mainsolution) {

        String testCasesFolder = "test-cases";

        String inputFolder = testCasesFolder + "/inputs";
        String outputFolder = testCasesFolder + "/outputs";

        String songsFile =  testCasesFolder + "/songs.txt";
        String[] inputFiles = getFilesInDirectory(inputFolder);

        String solutionsFolder = "src/solutions/" + solutionName;
        String solutionsOutputFolder = solutionsFolder + "/outputs";

        if (solutionName.equals("mainsolution") && mainsolution) {
            solutionsOutputFolder = outputFolder;
        }

        createFolderIfNotExists(solutionsOutputFolder);

        try {
            if (compileSolution(solutionsFolder, logFile)) {
                for (String inputFile : inputFiles) {
                    logFile.write("Running solution for: " + inputFile + "\n");
                    System.out.println("Running solution for: " + inputFile);

                    String inputFilePath = inputFolder + "/" + inputFile;

                    String outputFile = getOutputFileName(inputFilePath, solutionsOutputFolder);
                    String[] solutionArgs = {songsFile, inputFilePath, outputFile};
                    runSolution(solutionName, solutionArgs, logFile, inputFile, Grader.testCaseTimeLimits.get(inputFile));
                }
            }
        } catch (Exception e) {
            System.err.println("Error running solution: " + e);
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        Runner.run("mainsolution", new FileWriter("src/solutions/mainsolution/log.txt"), true);
    }
}
