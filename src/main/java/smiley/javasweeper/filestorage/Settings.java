package smiley.javasweeper.filestorage;

import java.io.IOException;

public class Settings {
  private static Settings instance;

  private boolean loaded;
  private JsonMap<Object> json;

  //Constructor
  private Settings() {
    this.loaded = false;
  }

  public static synchronized Settings getInstance() {
    if (instance == null) {
      instance = new Settings();
    }
    return instance;
  }

  public void load() throws IOException {
    this.json = new JsonMap<>("settings.json");
    this.loaded = true;
  }

  private void verifyLoaded() throws IllegalStateException {
    if (!loaded) {
      throw new IllegalStateException("Settings not loaded");
    }
  }

  //Accessors
  public int getDisplayWidth() {
    verifyLoaded();
    return (int) json.get("displayWidth");
  }

  public int getDisplayHeight() {
    verifyLoaded();
    return (int) json.get("displayHeight");
  }

  public double getMineChance() {
    verifyLoaded();
    return (double) json.get("mineChance");
  }

  public double getBoardScale() {
    verifyLoaded();
    return (double) json.get("boardScale");
  }

  public double getUiScale() {
    verifyLoaded();
    return (double) json.get("uiScale");
  }

  public boolean isFullscreen() {
    verifyLoaded();
    return (boolean) json.get("fullscreen");
  }

  //Mutators
  public void setDisplayWidth(int width) {
    verifyLoaded();
    if (width >= 0) {
      json.put("displayWidth", width);
      json.save();
    } else {
      System.out.println("Settings.setDisplayWidth: Expected positive value (Got \"" + width + "\")");
    }
  }

  public void setDisplayHeight(int height) {
    verifyLoaded();
    if (height >= 0) {
      json.put("displayHeight", height);
      json.save();
    } else {
      System.out.println("Settings.setDisplayHeight: Expected positive value (Got \"" + height + "\")");
    }
  }

  public void setMineChance(double mineChance) {
    verifyLoaded();
    if (mineChance >= 0 && mineChance <= 100) {
      json.put("mineChance", mineChance);
      json.save();
    } else {
      System.out.println("Settings.setMineChance: Expected value between 0 and 100 [inclusive] (Got \"" + mineChance + "\")");
    }
  }

  public void setBoardScale(double boardScale) {
    verifyLoaded();
    if (boardScale > 0) {
      json.put("boardScale", boardScale);
      json.save();
    } else {
      System.out.println(("Settings.setBoardScale: Expected a value greater than 0 (Got \"" + boardScale + "\")"));
    }
  }

  public void setUiScale(double uiScale) {
    verifyLoaded();
    if (uiScale > 0) {
      json.put("uiScale", uiScale);
      json.save();
    } else {
      System.out.println("Settings.setUiScale: Expected a value greater than 0 (Got \"" + uiScale + "\")");
    }
  }

  public void toggleFullscreen() {
    verifyLoaded();
    json.put("fullscreen", !isFullscreen());
    json.save();
  }
}
