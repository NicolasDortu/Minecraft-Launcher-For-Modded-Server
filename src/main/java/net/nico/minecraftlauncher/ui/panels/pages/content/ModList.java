package net.nico.minecraftlauncher.ui.panels.pages.content;

import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import java.util.List;

public class ModList {
  private List<CurseFileInfo> curseFiles;
  private List<Mod> mods;

  // getters and setters
  public List<CurseFileInfo> getCurseFiles() {
    return curseFiles;
  }

  public void setCurseFiles(List<CurseFileInfo> curseFiles) {
    this.curseFiles = curseFiles;
  }

  public List<Mod> getMods() {
    return mods;
  }

  public void setMods(List<Mod> mods) {
    this.mods = mods;
  }
}