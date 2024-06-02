package net.nico.minecraftlauncher.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadMod {
  private static final String MODS_URL = "https://URL/mods";
  private static final String MAIN_DIR = ".launcher-fx/";
  private static final String MODS_DIR = ".launcher-fx/mods/";

  public void downloadAndUnzipMod() {
    try {
      // Connect to the URL
      HttpURLConnection connection = (HttpURLConnection) new URL(MODS_URL).openConnection();
      connection.setRequestMethod("GET");

      // Check if the connection is successful
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new RuntimeException("Failed to connect: " + connection.getResponseCode());
      }

      // Get the main directory path
      File mainDirFile = Helpers.generateGamePath(MAIN_DIR);
      String mainDir = mainDirFile.getAbsolutePath();
      File modsDirFile = Helpers.generateGamePath(MODS_DIR);
      String modsDir = modsDirFile.getAbsolutePath();

      // Ensure the directories exist
      new File(mainDir).mkdirs();
      new File(modsDir).mkdirs();

      // Download and unzip the file
      try (InputStream inputStream = connection.getInputStream();
          ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream))) {

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          String filePath;
          if (entry.getName().endsWith(".jar")) {
            filePath = modsDir + "/" + entry.getName();
          } else {
            filePath = mainDir + "/" + entry.getName();
          }

          if (!entry.isDirectory()) {
            extractFile(zipInputStream, filePath);
          } else {
            new File(filePath).mkdirs();
          }
          zipInputStream.closeEntry();
        }
      }

      System.out.println("Mod downloaded and unzipped successfully.");
    } catch (Exception e) {
      System.out.println("Error downloading or unzipping mod: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void extractFile(ZipInputStream zipInputStream, String filePath) throws IOException {
    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
      byte[] bytesIn = new byte[4096];
      int read;
      while ((read = zipInputStream.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
    }
  }

  public String getLatestVersion() {
    File mainDirFile = Helpers.generateGamePath(MAIN_DIR);
    String versionFilePath = mainDirFile.getAbsolutePath() + "/version.txt";
    try (BufferedReader reader = new BufferedReader(new FileReader(versionFilePath))) {
      return reader.readLine();
    } catch (IOException e) {
      System.out.println("Error reading version file: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
}
