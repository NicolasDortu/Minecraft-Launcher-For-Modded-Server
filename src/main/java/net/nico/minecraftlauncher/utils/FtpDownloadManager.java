package net.nico.minecraftlauncher.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FtpDownloadManager {
  private static final String SERVER = "YOUR FTP SERVER ADRESS";
  private static final int PORT = 21;
  private static final String USER = "USERNAME";
  private static final String PASS = "PASSWORD";
  private static final String VERSION_FILE = "version.txt";
  private static final String JSON_FILE = "curse_mods_list.json";
  private static final String JAR_FILE = "CustomMod.jar";
  private static final String REMOTE_DIRECTORY = "DIRECTORY PATH";

  public String downloadFiles() {
    FTPClient ftpClient = new FTPClient();
    String latestVersion = null;
    try {
      ftpClient.connect(SERVER, PORT);
      ftpClient.login(USER, PASS);
      ftpClient.enterLocalPassiveMode();

      System.out.println("Connected and logged in to the server");

      ftpClient.changeWorkingDirectory(REMOTE_DIRECTORY);

      // Check the latest version from the version file
      try (InputStream versionStream = ftpClient.retrieveFileStream(VERSION_FILE)) {
        if (versionStream != null) {
          BufferedReader reader = new BufferedReader(new InputStreamReader(versionStream));
          latestVersion = reader.readLine();
          ftpClient.completePendingCommand(); // This is necessary to finalize the command
        }
      }

      // Download JSON file (ASCII or binary mode should work, but we'll use binary to
      // simplify)
      downloadFile(ftpClient, JSON_FILE, ".launcher-fx", FTP.BINARY_FILE_TYPE);

      // Download JAR file in binary mode
      downloadFile(ftpClient, JAR_FILE, ".launcher-fx/mods", FTP.BINARY_FILE_TYPE);

      ftpClient.logout();
    } catch (Exception ex) {
      System.out.println("Error during FTP operation: " + ex.getMessage());
      ex.printStackTrace();
    } finally {
      try {
        if (ftpClient.isConnected()) {
          ftpClient.disconnect();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return latestVersion;
  }

  private void downloadFile(FTPClient ftpClient, String remoteFile, String localDir, int fileType) {
    try {
      File downloadDirFile = Helpers.generateGamePath(localDir);
      String downloadDir = downloadDirFile.getAbsolutePath();
      String downloadFile = downloadDir + "/" + remoteFile;

      // Ensure the directory exists
      new File(downloadDir).mkdirs();

      // Set file type
      ftpClient.setFileType(fileType);

      try (OutputStream outputStream = new FileOutputStream(downloadFile)) {
        boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
        if (success) {
          System.out.println(remoteFile + " has been downloaded successfully.");
        } else {
          System.out.println("Failed to download " + remoteFile);
        }
      }
    } catch (Exception ex) {
      System.out.println("Error downloading " + remoteFile + ": " + ex.getMessage());
      ex.printStackTrace();
    }
  }
}
