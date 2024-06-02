package net.nico.minecraftlauncher.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FtpLauncherUpdater {
  private static final String SERVER = "YOUR FTP SERVER ADRESS";
  private static final int PORT = 21;
  private static final String USER = "USERNAME";
  private static final String PASS = "PASSWORD";
  private static final String NEW_LAUNCHER_FILE = "Launcher.exe";
  private static final String REMOTE_DIRECTORY = "DIRECTORY PATH";

  public static boolean downloadNewLauncher(String downloadPath) {
    FTPClient ftpClient = new FTPClient();
    try {
      ftpClient.connect(SERVER, PORT);
      ftpClient.login(USER, PASS);
      ftpClient.enterLocalPassiveMode();
      ftpClient.changeWorkingDirectory(REMOTE_DIRECTORY);

      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      String newLauncherPath = downloadPath.replace(".exe", "_UPDATE.exe"); // Add _UPDATE at the end of the file name
      File downloadFile = new File(newLauncherPath);
      try (OutputStream outputStream = new FileOutputStream(downloadFile)) {
        return ftpClient.retrieveFile(NEW_LAUNCHER_FILE, outputStream);
      }
    } catch (IOException ex) {
      System.out.println("Error downloading new launcher: " + ex.getMessage());
      return false;
    } finally {
      try {
        if (ftpClient.isConnected()) {
          ftpClient.logout();
          ftpClient.disconnect();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
