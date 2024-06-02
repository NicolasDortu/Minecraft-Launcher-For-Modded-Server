package net.nico.minecraftlauncher;

import javafx.application.Application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.*;
//import net.nico.minecraftlauncher.utils.FtpDownloadManager; //FTP DOWNLOAD METHOD
//import net.nico.minecraftlauncher.utils.FtpLauncherUpdater; //FTP DOWNLOAD METHOD
import net.nico.minecraftlauncher.utils.DownloadMod;
import net.nico.minecraftlauncher.utils.DownloadUpdate;

import java.io.PrintWriter;

public class Main {
  private static final String CURRENT_VERSION = "1.0";

  //// FTP DOWNLOAD METHOD////
  // public static void main(String[] args) {
  // String launcherPath = getApplicationPath(); // Get the path of the current
  //// running application
  // if (launcherPath == null) {
  // JOptionPane.showMessageDialog(null, "Error locating the launcher
  //// executable.", "Startup Error",
  // JOptionPane.ERROR_MESSAGE);
  // return;
  // }
  // System.out.println("Launcher path: " + launcherPath);
  // // Initialize the new FTP download manager
  // FtpDownloadManager ftpDownloadManager = new FtpDownloadManager();
  // // Call the downloadFiles method and print the current version of the
  //// launcher
  // String latestVersion = ftpDownloadManager.downloadFiles();
  // System.out.println("Latest Version: " + latestVersion);
  // if (latestVersion != null && !latestVersion.equals(CURRENT_VERSION)) {
  // int response = JOptionPane.showConfirmDialog(null,
  // "A new version of the launcher is available. Do you want to update?", "Update
  //// Available",
  // JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
  // if (response == JOptionPane.YES_OPTION) {
  // boolean success = launcherUpdater.downloadNewLauncher(launcherPath);
  // if (success) {
  // System.out.println("New launcher version downloaded successfully.");
  // String launcherName = new File(launcherPath).getName(); // Get the name of
  //// the current running application
  // restartLauncher(launcherPath, launcherName);
  // } else {
  // JOptionPane.showMessageDialog(null, "Failed to download the new launcher
  //// version.", "Update Error",
  // JOptionPane.ERROR_MESSAGE);
  // }
  // } else {
  // launchApplication(args);
  // }
  // } else {
  // launchApplication(args);
  // }
  // }

  public static void main(String[] args) {
    String launcherPath = getApplicationPath(); // Get the path of the current running application
    if (launcherPath == null) {
      JOptionPane.showMessageDialog(null, "Error locating the launcher executable.", "Startup Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    System.out.println("Launcher path: " + launcherPath);

    // Initialize the new DownloadMod and DownloadUpdate
    DownloadMod downloadMod = new DownloadMod();
    DownloadUpdate downloadUpdate = new DownloadUpdate();

    // Download and unzip mod files
    downloadMod.downloadAndUnzipMod();

    // Get the latest version from the downloaded version.txt file
    String latestVersion = downloadMod.getLatestVersion();
    System.out.println("Latest Version: " + latestVersion);

    if (latestVersion != null && !latestVersion.equals(CURRENT_VERSION)) {
      int response = JOptionPane.showConfirmDialog(null,
          "A new version of the launcher is available. Do you want to update?", "Update Available",
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (response == JOptionPane.YES_OPTION) {
        String newLauncherPath = launcherPath.replace(".exe", "_UPDATE.exe");
        boolean success = downloadUpdate.downloadNewLauncher(newLauncherPath);
        if (success) {
          System.out.println("New launcher version downloaded successfully.");
          String launcherName = new File(launcherPath).getName(); // Get the name of the current running application
          restartLauncher(launcherPath, launcherName);
        } else {
          JOptionPane.showMessageDialog(null, "Failed to download the new launcher version.", "Update Error",
              JOptionPane.ERROR_MESSAGE);
        }
      } else {
        launchApplication(args);
      }
    } else {
      launchApplication(args);
    }
  }

  // Custom cmd script to replace the current launcher with the new launcher
  // (Windows)
  private static void restartLauncher(String launcherPath, String launcherName) {
    String folderPath = getFolderPath();
    System.out.println("Folder Path: " + folderPath);
    try {
      String script = "timeout /t 2 /nobreak > NUL\n" + // Wait for 2 seconds to make sure the current program has
                                                        // exited
          "del \"" + launcherName + "\"\n" + // Delete the current launcher
          "ren FunBlockLauncher_UPDATE.exe FunBlockLauncher.exe\n" + // Rename the new launcher
          "echo Set objShell = CreateObject(\"WScript.Shell\") > msgbox.vbs\n" +
          "echo messageText = \"Launcher update done, restart the launcher?\" >> msgbox.vbs\n" +
          "echo result = objShell.Popup(messageText, 0, \"Update\", 4 + 32) >> msgbox.vbs\n" +
          "echo If result = 6 Then >> msgbox.vbs\n" +
          "echo   objShell.Run \"FunBlockLauncher.exe\" >> msgbox.vbs\n" +
          "echo End If >> msgbox.vbs\n" +
          "cscript msgbox.vbs\n" +
          "del msgbox.vbs\n" +
          "exit"; // Close the command prompt window

      File batchFile = new File(folderPath + "\\update.bat");
      try (PrintWriter writer = new PrintWriter(batchFile)) {
        writer.println(script);
      }

      System.out.println("Batch file path: " + batchFile.getAbsolutePath());

      Runtime.getRuntime().exec("cmd /c start \"\" \"" + batchFile.getAbsolutePath() + "\"");
      System.exit(0);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Failed to restart the launcher: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private static void launchApplication(String[] args) {
    try {
      // Launch the JavaFX application
      Class.forName("javafx.application.Application");
      Application.launch(Launcher.class, args);
    } catch (ClassNotFoundException e) {
      JOptionPane.showMessageDialog(
          null,
          "Error:\n" + e.getMessage() + " not found",
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private static String getApplicationPath() {
    try {
      return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
    } catch (URISyntaxException e) {
      JOptionPane.showMessageDialog(null, "Failed to locate the launcher executable: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }
  }

  private static String getFolderPath() {
    try {
      return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
    } catch (URISyntaxException e) {
      JOptionPane.showMessageDialog(null, "Failed to locate the launcher executable: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }
  }
}