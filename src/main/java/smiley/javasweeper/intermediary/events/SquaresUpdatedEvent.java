package smiley.javasweeper.intermediary.events;

import java.util.List;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.model.squares.Square;

public record SquaresUpdatedEvent(Board board, List<Square> squares) implements ModelEvent {}
