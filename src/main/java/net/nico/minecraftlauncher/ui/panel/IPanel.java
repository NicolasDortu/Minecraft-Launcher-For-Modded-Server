package net.nico.minecraftlauncher.ui.panel;

import net.nico.minecraftlauncher.ui.PanelManager;
import javafx.scene.layout.GridPane;

public interface IPanel {
  void init(PanelManager panelManager);

  GridPane getLayout();

  void onShow();

  String getName();

  String getStylesheetPath();
}
