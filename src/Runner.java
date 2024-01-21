import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class Runner {

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

    private static long runSolution(String source, String[] solutionArgs, FileWriter logFile, String testCase, long timeLimitMilliseconds) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String command;
        if (source.endsWith("-cpp")) {
            command = "Project3 " + String.join(" ", solutionArgs);
        } else {
            command = "java " + "-cp out " + "Project3 " + String.join(" ", solutionArgs);
        }
        try {
            Process process = Runtime.getRuntime().exec(command);

            if (!process.waitFor(timeLimitMilliseconds, TimeUnit.MILLISECONDS)) {
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

            }

            logFile.write("Time taken: " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + "ms\n");
            System.out.println("Time taken: " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + "ms");

        } catch (Exception e) {
            System.err.println("Error running solution: " + e);
        }
        return (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime());
    }

    public static void run(String solutionName, FileWriter logFile, boolean measureTimeLimits, boolean generateOutputs) {

        String solutionFolder = FilesUtil.getFileInsideFolders(solutionName, Grader.solutionsPath);
        String solutionOutputFolder = solutionFolder + "/outputs";

        if (generateOutputs) {
            solutionOutputFolder = Grader.outputsFolder;
        }

        FilesUtil.createFolderIfNotExists(solutionOutputFolder);

        try {
            if (compileSolution(solutionFolder, logFile)) {
                for (String inputFile : Grader.inputFiles) {
                    logFile.write("Running solution for: " + inputFile + "\n");
                    System.out.println("Running solution for: " + inputFile);

                    String inputFilePath = FilesUtil.getFileInsideFolders(inputFile, Grader.inputsFolder);
                    String outputFile = FilesUtil.getOutputFileName(inputFilePath, solutionOutputFolder);

                    String[] solutionArgs = {Grader.songsFile, inputFilePath, outputFile};

                    long timeLimit = (Grader.testCaseTimeLimits.containsKey(inputFile) ? Grader.testCaseTimeLimits.get(inputFile) : 300000) ;
                    long time = runSolution(solutionName, solutionArgs, logFile, inputFile, timeLimit);

                    if (measureTimeLimits) {
                        Grader.testCaseTimeLimits.put(inputFile, time * Grader.timeLimitMultiplier);

                        logFile.write("Time limit set to: " + time * Grader.timeLimitMultiplier + "ms for " + inputFile + "\n");
                        System.out.println("Time limit set to: " + time * Grader.timeLimitMultiplier + "ms for " + inputFile);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error running solution: " + e);
            System.exit(1);
        }
    }

    public static void measureTimeLimits(String solutionName, FileWriter logFile) throws IOException {
        run(solutionName, logFile, true, false);
        logFile.close();
    }

    public static void main(String[] args) throws IOException {
        Runner.run(Grader.mainSolutionName, new FileWriter("src/solutions/mainsolution/log.txt"), false, true);
    }

}
