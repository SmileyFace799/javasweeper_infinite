package smiley.javasweeper.intermediary;

import smiley.javasweeper.intermediary.events.ModelEvent;

public interface ModelEventListener {
    void onEvent(ModelEvent me);
}
