package smiley.javasweeper.intermediary.events;

import java.util.List;
import smiley.javasweeper.model.squares.Square;

public record SquaresUpdatedEvent(List<Square> squares) implements ModelEvent {}
