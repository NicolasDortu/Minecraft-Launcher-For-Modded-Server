package net.nico.minecraftlauncher.game;

import java.net.URL;
import java.io.File;
import java.net.MalformedURLException;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import net.nico.minecraftlauncher.utils.Helpers;

public class MinecraftInfos {
  public static final String GAME_VERSION = "1.20.1";
  public static final ForgeVersionType FORGE_VERSION_TYPE = ForgeVersionType.NEW;
  public static final String FORGE_VERSION = "1.20.1-47.2.21";
  public static final String modsListFile = Helpers.generateGamePath(".launcher-fx") + "/curse_mods_list.json";

  public static final URL CURSE_MODS_LIST_URL;

  static {
    try {
      File file = new File(modsListFile);
      if (!file.exists()) {
        throw new RuntimeException("Could not find curse_mods_list.json");
      }
      CURSE_MODS_LIST_URL = file.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException("Could not convert file path to URL", e);
    }
  }

  public static final URL MODS_LIST_URL = MinecraftInfos.class.getResource("/mods_list.json");
  static {
    if (MODS_LIST_URL == null) {
      throw new RuntimeException("Could not find mods_list.json");
    }
  }
}
