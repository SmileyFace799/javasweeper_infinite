package smiley.javasweeper.intermediary;

import smiley.javasweeper.intermediary.events.model.ModelEvent;

public interface ModelEventListener {
    void onEvent(ModelEvent me);
}
