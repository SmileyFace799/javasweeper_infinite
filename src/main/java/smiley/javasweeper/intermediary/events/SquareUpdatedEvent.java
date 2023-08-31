package smiley.javasweeper.intermediary.events;

import smiley.javasweeper.squares.Square;

public record SquareUpdatedEvent(Square square) implements ModelEvent {}
