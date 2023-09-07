package smiley.javasweeper.intermediary;

import smiley.javasweeper.intermediary.events.file.FileEvent;

public interface FileEventListener {
    void onEvent(FileEvent fe);
}
