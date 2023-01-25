package smiley.mainapp;

public class Settings {
  private static final JsonMap<Object> json = new JsonMap<>("src/main/resources/settings.json");

  //Constructor
  private Settings() {
    throw new IllegalStateException("Utility class");
  }

  //Accessors
  public static int getDisplayWidth() {
    return (int) json.get("displayWidth");
  }

  public static int getDisplayHeight() {
    return (int) json.get("displayHeight");
  }

  public static double getMineChance() {
    return (double) json.get("mineChance");
  }

  public static double getBoardScale() {
    return (double) json.get("boardScale");
  }

  public static double getUiScale() {
    return (double) json.get("uiScale");
  }

  public static boolean isFullscreen() {
    return (boolean) json.get("fullscreen");
  }

  //Mutators
  public static void setDisplayWidth(int width) {
    if (width >= 0) {
      json.put("displayWidth", width);
      json.save();
    } else {
      System.out.println("Settings.setDisplayWidth: Expected positive value (Got \"" + width + "\")");
    }
  }

  public static void setDisplayHeight(int height) {
    if (height >= 0) {
      json.put("displayHeight", height);
      json.save();
    } else {
      System.out.println("Settings.setDisplayHeight: Expected positive value (Got \"" + height + "\")");
    }
  }

  public static void setMineChance(double mineChance) {
    if (mineChance >= 0 && mineChance <= 100) {
      json.put("mineChance", mineChance);
      json.save();
    } else {
      System.out.println("Settings.setMineChance: Expected value between 0 and 100 [inclusive] (Got \"" + mineChance + "\")");
    }
  }

  public static void setBoardScale(double boardScale) {
    if (boardScale > 0) {
      json.put("boardScale", boardScale);
      json.save();
    } else {
      System.out.println(("Settings.setBoardScale: Expected a value greater than 0 (Got \"" + boardScale + "\")"));
    }
  }

  public static void setUiScale(double uiScale) {
    if (uiScale > 0) {
      json.put("uiScale", uiScale);
      json.save();
    } else {
      System.out.println("Settings.setUiScale: Expected a value greater than 0 (Got \"" + uiScale + "\")");
    }
  }

  public static void toggleFullscreen() {
    json.put("fullscreen", !isFullscreen());
    json.save();
  }
}
