import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesUtil {
    public static String getFileExtension(String fileName) {
        String[] fileNameParts = fileName.split("\\.");
        return fileNameParts[fileNameParts.length - 1];
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

    public static String getOutputFileName(String inputFileName, String outputFolder) {
        String[] inputFileNameParts = inputFileName.split("/");
        String outputFileName = inputFileNameParts[inputFileNameParts.length - 1];
        return outputFolder + "/" + outputFileName;
    }

    public static String[] getFolders(String path) {
        return new File(path).list((current, name) -> new File(current, name).isDirectory());
    }

    public static void removeFolder(String path) {
        File folder = new File(path);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    removeFolder(file.getAbsolutePath());
                }
            }
            folder.delete();
        }
    }

    public static String[] getFilesInDirectoryWithExtension(String directory, String extension) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isFile() || !FilesUtil.getFileExtension(listOfFiles[i].getName()).equals(extension)) {
                continue;
            }
            fileNames[i] = listOfFiles[i].getName();
        }
        return  fileNames;
    }

    public static String getFileInsideFolders(String fileName, String... folders) {
        StringBuilder path = new StringBuilder();
        for (String folder : folders) {
            path.append(folder).append("/");
        }
        path.append(fileName);
        return path.toString();
    }

    public static void unzip(String zipFilePath, String destDir, String specificFolder) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {

            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                if (!entryName.startsWith(specificFolder) || !entryName.endsWith(".java")) {
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }
                try {
                    FilesUtil.createAllParentFolders(destDir + "/" + entryName);
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

}
