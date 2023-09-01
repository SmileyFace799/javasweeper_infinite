package smiley.javasweeper.filestorage;

public class Settings {
  private static Settings instance;

  private boolean loaded;
  private MultiTypeMap<String> map;
  private JsonMap json;

  //Constructor
  private Settings() {
    this.loaded = false;
    this.map = new MultiTypeMap<>();
  }

  public static synchronized Settings getInstance() {
    if (instance == null) {
      instance = new Settings();
    }
    return instance;
  }

  public void load() {
    this.json = new JsonMap("settings.json");
    this.map = json;
    this.loaded = true;
  }

  private void verifyLoaded() throws IllegalStateException {
    if (!loaded) {
      throw new IllegalStateException("Settings not loaded");
    }
  }

  //Accessors
  public int getDisplayWidth() {
    return map.get("displayWidth", Integer.class, 1280);
  }

  public int getDisplayHeight() {
    return map.get("displayHeight", Integer.class, 720);
  }

  public double getMineChance() {
    return map.get("mineChance", Double.class, 0.25);
  }

  public double getBoardScale() {
    return map.get("boardScale", Double.class, 1D);
  }

  public double getUiScale() {
    return map.get("uiScale", Double.class, 1D);
  }

  public boolean isFullscreen() {
    return map.get("fullscreen", Boolean.class, false);
  }

  //Mutators
  public void setDisplayWidth(int width) {
    verifyLoaded();
    if (width >= 0) {
      map.put("displayWidth", width);
      json.save();
    } else {
      System.out.println("Settings.setDisplayWidth: Expected positive value (Got \"" + width + "\")");
    }
  }

  public void setDisplayHeight(int height) {
    verifyLoaded();
    if (height >= 0) {
      map.put("displayHeight", height);
      json.save();
    } else {
      System.out.println("Settings.setDisplayHeight: Expected positive value (Got \"" + height + "\")");
    }
  }

  public void setMineChance(double mineChance) {
    verifyLoaded();
    if (mineChance >= 0 && mineChance <= 100) {
      map.put("mineChance", mineChance);
      json.save();
    } else {
      System.out.println("Settings.setMineChance: Expected value between 0 and 100 [inclusive] (Got \"" + mineChance + "\")");
    }
  }

  public void setBoardScale(double boardScale) {
    verifyLoaded();
    if (boardScale > 0) {
      map.put("boardScale", boardScale);
      json.save();
    } else {
      System.out.println(("Settings.setBoardScale: Expected a value greater than 0 (Got \"" + boardScale + "\")"));
    }
  }

  public void setUiScale(double uiScale) {
    verifyLoaded();
    if (uiScale > 0) {
      map.put("uiScale", uiScale);
      json.save();
    } else {
      System.out.println("Settings.setUiScale: Expected a value greater than 0 (Got \"" + uiScale + "\")");
    }
  }

  public void toggleFullscreen() {
    verifyLoaded();
    map.put("fullscreen", !isFullscreen());
    json.save();
  }
}
