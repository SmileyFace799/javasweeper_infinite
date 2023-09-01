package smiley.notimplemented;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class Events {
  private final HashMap<String, InputEvent> eventMap = new HashMap<>();

  public InputEvent getEvent(String eventName) {
    InputEvent event = null;
    if (eventMap.containsKey(eventName)) {
      event = eventMap.get(eventName);
    } else {
      System.out.println("Events.getEvent: No event named \"" + eventName + "\"");
    }
    return event;
  }

  public KeyEvent getKeyEvent(String eventName) {
    KeyEvent event = null;
    if (getEvent(eventName) instanceof KeyEvent e) {
      event = e;
    } else {
      System.out.println("Events.getKeyEvent: Event \"" + eventName + "\" is not a KeyEvent");
    }
    return event;
  }

  public MouseEvent getMouseEvent(String eventName) {
    MouseEvent event = null;
    if (getEvent(eventName) instanceof MouseEvent e) {
      event = e;
    } else {
      System.out.println("Events.getMouseEvent: Event \"" + eventName + "\" is not a MouseEvent");
    }
    return event;
  }

  public void putEvent(String eventName, InputEvent event) {
    eventMap.put(eventName, event);
  }
}
