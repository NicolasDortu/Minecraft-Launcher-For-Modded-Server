package net.nico.minecraftlauncher.ui.panels.pages.content;

import net.nico.minecraftlauncher.Launcher;
import net.nico.minecraftlauncher.game.MinecraftInfos;
import net.nico.minecraftlauncher.ui.PanelManager;
//import net.nico.minecraftlauncher.utils.Helpers;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.io.FileInputStream;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class Home extends ContentPanel {
  private final Saver saver = Launcher.getInstance().getSaver();
  GridPane boxPane = new GridPane();
  ProgressBar progressBar = new ProgressBar();
  Label stepLabel = new Label();
  Label fileLabel = new Label();
  boolean isDownloading = false;

  @Override
  public String getName() {
    return "home";
  }

  @Override
  public String getStylesheetPath() {
    return "css/content/home.css";
  }

  @Override
  public void init(PanelManager panelManager) {
    super.init(panelManager);

    RowConstraints rowConstraints = new RowConstraints();
    rowConstraints.setValignment(VPos.CENTER);
    rowConstraints.setMinHeight(150); // align button to the vertical
    rowConstraints.setMaxHeight(150);
    this.layout.getRowConstraints().addAll(rowConstraints, new RowConstraints());
    boxPane.getStyleClass().add("box-pane");
    setCanTakeAllSize(boxPane);
    boxPane.setPadding(new Insets(20));
    this.layout.add(boxPane, 0, 0); // Change position of button here
    this.layout.getStyleClass().add("home-layout");

    progressBar.getStyleClass().add("download-progress");
    stepLabel.getStyleClass().add("download-status");
    fileLabel.getStyleClass().add("download-status");

    progressBar.setTranslateY(-40);
    setCenterH(progressBar);
    setCanTakeAllWidth(progressBar);

    stepLabel.setTranslateY(-20);
    setCenterH(stepLabel);
    setCanTakeAllSize(stepLabel);

    fileLabel.setTranslateY(-5);
    setCenterH(fileLabel);
    setCanTakeAllSize(fileLabel);

    this.showPlayButton();
  }

  private void showPlayButton() {
    boxPane.getChildren().clear();
    Button playBtn = new Button("Jouer");
    final var playIcon = new MaterialDesignIconView<>(MaterialDesignIcon.G.GAMEPAD);
    playIcon.getStyleClass().add("play-icon");
    setCanTakeAllSize(playBtn);
    setCenterH(playBtn);
    setCenterV(playBtn);
    playBtn.getStyleClass().add("play-btn");
    playBtn.setGraphic(playIcon);
    playBtn.setTranslateY(275);
    playBtn.setOnMouseClicked(e -> this.play());
    boxPane.getChildren().add(playBtn);
  }

  private void play() {
    isDownloading = true;
    boxPane.getChildren().clear();
    setProgress(0, 0);
    boxPane.getChildren().addAll(progressBar, stepLabel, fileLabel);

    new Thread(this::update).start();
  }

  public static List<CurseFileInfo> getFilesFromJson(URL url) throws IOException {
    InputStream is;
    if (url.getProtocol().equals("file")) {
      is = new FileInputStream(url.getPath());
    } else {
      is = url.openStream();
    }
    try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
      Type type = new TypeToken<List<CurseFileInfo>>() {
      }.getType();
      return new Gson().fromJson(reader, type);
    }
  }

  public static List<Mod> getModsFromJson(URL url) throws IOException {
    InputStream is = url.openStream();
    try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
      Type listType = new TypeToken<ArrayList<Mod>>() {
      }.getType();
      return new Gson().fromJson(reader, listType);
    }
  }

  static class MyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
      String filePath = t.getRequestURI().getPath().substring("/files".length());
      Path file = Paths.get("/libs", filePath);
      t.sendResponseHeaders(200, file.toFile().length());
      java.nio.file.Files.copy(file, t.getResponseBody());
      t.close();
    }
  }

  public void update() {
    IProgressCallback callback = new IProgressCallback() {
      private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
      private String stepTxt = "";
      private String percentTxt = "0.0%";

      @Override
      public void step(Step step) {
        Platform.runLater(() -> {
          stepTxt = StepInfo.valueOf(step.name()).getDetails();
          setStatus(String.format("%s (%s)", stepTxt, percentTxt));
        });
      }

      @Override
      public void update(DownloadList.DownloadInfo info) {
        Platform.runLater(() -> {
          percentTxt = decimalFormat.format(info.getDownloadedBytes() * 100.d / info.getTotalToDownloadBytes()) + "%";
          setStatus(String.format("%s (%s)", stepTxt, percentTxt));
          setProgress(info.getDownloadedBytes(), info.getTotalToDownloadBytes());
        });
      }

      @Override
      public void onFileDownloaded(Path path) {
        Platform.runLater(() -> {
          String p = path.toString();
          fileLabel.setText("..." + p.replace(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath(), ""));
        });
      }
    };

    try {
      final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
          .withName(MinecraftInfos.GAME_VERSION)
          .build();

      List<CurseFileInfo> curseMods = getFilesFromJson(MinecraftInfos.CURSE_MODS_LIST_URL);
      // List<Mod> mods = getModsFromJson(MinecraftInfos.MODS_LIST_URL);

      final AbstractForgeVersion forge = new ForgeVersionBuilder(MinecraftInfos.FORGE_VERSION_TYPE)
          .withForgeVersion(MinecraftInfos.FORGE_VERSION)
          .withCurseMods(curseMods)
          // .withMods(mods)
          .withFileDeleter(new ModFileDeleter(true, "nicomod-0.1-1.20.1.jar", "soccermod-0.1-1.20.1.jar"))
          // Put here the custom mods that you want to be ignored by the file deleter
          .build();

      final FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
          .withVanillaVersion(vanillaVersion)
          .withModLoaderVersion(forge)
          .withLogger(Launcher.getInstance().getLogger())
          .withProgressCallback(callback)
          .build();

      updater.update(Launcher.getInstance().getLauncherDir());

      this.startGame(updater.getVanillaVersion().getName());
    } catch (Exception e) {
      Launcher.getInstance().getLogger().printStackTrace(e);
      Platform.runLater(() -> this.panelManager.getStage().show());
    }
  }

  public void startGame(String gameVersion) {
    try {
      NoFramework noFramework = new NoFramework(
          Launcher.getInstance().getLauncherDir(),
          Launcher.getInstance().getAuthInfos(),
          GameFolder.FLOW_UPDATER);

      noFramework.getAdditionalVmArgs().add(this.getRamArgsFromSaver());

      Process p = noFramework.launch(gameVersion, MinecraftInfos.FORGE_VERSION.split("-")[1],
          NoFramework.ModLoader.FORGE);

      Platform.runLater(() -> {
        try {
          p.waitFor();
          Platform.exit();
        } catch (InterruptedException e) {
          Launcher.getInstance().getLogger().printStackTrace(e);
        }
      });
    } catch (Exception e) {
      Launcher.getInstance().getLogger().printStackTrace(e);
    }
  }

  public String getRamArgsFromSaver() {
    int val = 1024;
    try {
      if (saver.get("maxRam") != null) {
        val = Integer.parseInt(saver.get("maxRam"));
      } else {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException error) {
      saver.set("maxRam", String.valueOf(val));
      saver.save();
    }

    return "-Xmx" + val + "M";
  }

  public void setStatus(String status) {
    this.stepLabel.setText(status);
  }

  public void setProgress(double current, double max) {
    this.progressBar.setProgress(current / max);
  }

  public boolean isDownloading() {
    return isDownloading;
  }

  public enum StepInfo {
    READ("Lecture du fichier json..."),
    DL_LIBS("Téléchargement des libraries..."),
    DL_ASSETS("Téléchargement des ressources..."),
    EXTRACT_NATIVES("Extraction des natives..."),
    FORGE("Installation de forge..."),
    FABRIC("Installation de fabric..."),
    MODS("Téléchargement des mods..."),
    EXTERNAL_FILES("Téléchargement des fichier externes..."),
    POST_EXECUTIONS("Exécution post-installation..."),
    MOD_LOADER("Installation du mod loader..."),
    INTEGRATION("Intégration des mods..."),
    END("Fini !");

    final String details;

    StepInfo(String details) {
      this.details = details;
    }

    public String getDetails() {
      return details;
    }
  }
}
