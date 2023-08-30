package smiley.javasweeper.intermediary.events;

import smiley.javasweeper.model.Board;

public record BoardLoadedEvent(Board board) implements ModelEvent {}
