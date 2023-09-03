package smiley.javasweeper.filestorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.model.squares.BombSquare;
import smiley.javasweeper.model.squares.NumberSquare;
import smiley.javasweeper.model.squares.Square;

public class BoardLoader {
    private BoardLoader() {
        throw new IllegalStateException("Utility class");
    }

    private static Square makeNumberSquare(String squareString) {
        String[] squareArr = squareString.split("&");
        Square square;
        int x = Integer.parseInt(squareArr[1]);
        int y = Integer.parseInt(squareArr[2]);
        if (Objects.equals(squareArr[0], "number")) {
            square = new NumberSquare(x, y);
            NumberSquare numSquare = (NumberSquare) square;
            if (Boolean.parseBoolean(squareArr[4])) {
                numSquare.setRevealedTrue(Integer.parseInt(squareArr[5]));
            }
        } else if (Objects.equals(squareArr[0], "bomb")) {
            square = new BombSquare(x, y);
        } else {
            throw new IllegalArgumentException("Cannot recognize square: " + squareArr[0]);
        }
        if (Boolean.parseBoolean(squareArr[3])) {
            square.toggleFlagged();
        }
        return square;
    }

    public static Board makeBoard(String boardString, String filename) {
        Board board = new Board(filename);
        for (String squareString : boardString.split("\\|")) {
            Square square = makeNumberSquare(squareString);
            board.put(square.getX(), square.getY(), square);
        }
        return board;
    }

    public static Board loadBoard(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return makeBoard(br.readLine(), filename);
        }
    }

    public static boolean boardExists(String filename) {
        File f = new File(filename);
        return f.exists() && !f.isDirectory();
    }

    private static String serializeBoard(Board board) {
        StringBuilder boardStr = new StringBuilder();
        for (Square square : board) {
            if (square instanceof NumberSquare) {
                boardStr.append("number");
            } else if (square instanceof BombSquare) {
                boardStr.append("bomb");
            }
            boardStr.append("&");
            boardStr.append(square.getX());
            boardStr.append("&");
            boardStr.append(square.getY());
            boardStr.append("&");
            boardStr.append(square.isFlagged());
            if (square instanceof NumberSquare numSquare) {
                boardStr.append("&");
                boardStr.append(numSquare.isRevealed());
                if (numSquare.isRevealed()) {
                    boardStr.append("&");
                    boardStr.append(numSquare.getNumber());
                }
            }
            boardStr.append("|");
        }
        boardStr.deleteCharAt(boardStr.length() - 1);
        return boardStr.toString();
    }

    public static void saveBoard(Board board) throws IOException {
        String filename = board.getFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Board \"board\" has no filename");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(serializeBoard(board));
        }
    }
}
