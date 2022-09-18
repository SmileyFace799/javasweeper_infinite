package main;

import java.util.HashMap;

public class StateHandler {
  final HashMap<Integer, State> states = new HashMap<>();
  private int active;

  public State getState(int id) {
    return states.get(id);
  }

  public State getActive() {
    return getState(active);
  }

  public void addState(int id, State state) {
    states.put(id, state);
  }

  public void setActive(int id) {
    if (states.containsKey(id)) {
      active = id;
    } else {
      System.out.println("StateHandler.setActive: Unrecognized state id \"" + id + "\"");
    }
  }
}
