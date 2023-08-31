package smiley.javasweeper.filestorage;

import java.util.Objects;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.squares.BombSquare;
import smiley.javasweeper.squares.NumberSquare;
import smiley.javasweeper.squares.Square;

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

    public static Board makeBoard(String boardString) {
        Board board = new Board();
        for (String squareString : boardString.split("\\|")) {
            Square square = makeNumberSquare(squareString);
            board.put(square.getX(), square.getY(), square);
        }
        return board;
    }

    public static String serializeBoard(Board board) {
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
                boardStr.append("&");
                boardStr.append(numSquare.getNumber());
            }
            boardStr.append("|");
        }
        boardStr.deleteCharAt(boardStr.length() - 1);
        return boardStr.toString();
    }
}
