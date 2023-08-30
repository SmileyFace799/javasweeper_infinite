package smiley.javasweeper.filestorage;

import smiley.javasweeper.model.Board;

public class BoardLoader {
    private BoardLoader() {
        throw new IllegalStateException("Utility class");
    }

    public static Board loadBoard(String boardString) {
        new Board()
    }
}
