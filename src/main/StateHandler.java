package main;

import java.util.HashMap;

public class StateHandler {
  final HashMap<GameState, State> states = new HashMap<>();
  private GameState active;

  public State getState(GameState stateName) {
    return states.get(stateName);
  }

  public State getActive() {
    return getState(active);
  }

  public void addState(GameState stateName, State state) {
    states.put(stateName, state);
  }

  public void setActive(GameState stateName) {
    if (states.containsKey(stateName)) {
      active = stateName;
    } else {
      System.out.println("StateHandler.setActive: Unrecognized state \"" + stateName + "\"");
    }
  }
}
