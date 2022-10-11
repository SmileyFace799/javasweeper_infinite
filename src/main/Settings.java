package main;

public class Settings {
  private static final JsonMap<Object> json = new JsonMap<>("res/settings.json");

  //Accessors
  public int getDisplayWidth() {
    return (int) json.get("displayWidth");
  }

  public int getDisplayHeight() {
    return (int) json.get("displayHeight");
  }

  public double getMineChance() {
    return (double) json.get("mineChance");
  }

  public double getBoardScale() {
    return (double) json.get("boardScale");
  }

  public double getUiScale() {
    return (double) json.get("uiScale");
  }

  public boolean isFullscreen() {
    return (boolean) json.get("fullscreen");
  }

  //Mutators
  public void setDisplayWidth(int width) {
    if (width >= 0) {
      json.put("displayWidth", width);
      json.save();
    } else {
      System.out.println("Settings.setDisplayWidth: Expected positive value (Got \"" + width + "\")");
    }
  }

  public void setDisplayHeight(int height) {
    if (height >= 0) {
      json.put("displayHeight", height);
      json.save();
    } else {
      System.out.println("Settings.setDisplayHeight: Expected positive value (Got \"" + height + "\")");
    }
  }

  public void setMineChance(double mineChance) {
    if (mineChance >= 0 && mineChance <= 100) {
      json.put("mineChance", mineChance);
      json.save();
    } else {
      System.out.println("Settings.setMineChance: Expected value between 0 and 100 [inclusive] (Got \"" + mineChance + "\")");
    }
  }

  public void setBoardScale(double boardScale) {
    if (boardScale > 0) {
      json.put("boardScale", boardScale);
      json.save();
    } else {
      System.out.println(("Settings.setBoardScale: Expected a value greater than 0 (Got \"" + boardScale + "\")"));
    }
  }

  public void setUiScale(double uiScale) {
    if (uiScale > 0) {
      json.put("uiScale", uiScale);
      json.save();
    } else {
      System.out.println("Settings.setUiScale: Expected a value greater than 0 (Got \"" + uiScale + "\")");
    }
  }

  public void toggleFullscreen() {
    json.put("fullscreen", !isFullscreen());
    json.save();
  }
}
