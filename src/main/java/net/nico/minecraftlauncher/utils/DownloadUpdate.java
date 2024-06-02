package net.nico.minecraftlauncher.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUpdate {
  private static final String UPDATE_URL = "https://URL/update";
  private static final String DOWNLOAD_FILE = "FunBlockLauncher_UPDATE.exe";

  public boolean downloadNewLauncher(String downloadPath) {
    try {
      // Connect to the URL
      HttpURLConnection connection = (HttpURLConnection) new URL(UPDATE_URL).openConnection();
      connection.setRequestMethod("GET");

      // Check if the connection is successful
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new RuntimeException("Failed to connect: " + connection.getResponseCode());
      }

      // Download the file
      try (InputStream inputStream = new BufferedInputStream(connection.getInputStream());
          FileOutputStream outputStream = new FileOutputStream(downloadPath)) {

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
        }
      }

      System.out.println("New launcher version downloaded successfully.");
      return true;
    } catch (Exception e) {
      System.out.println("Error downloading new launcher: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}