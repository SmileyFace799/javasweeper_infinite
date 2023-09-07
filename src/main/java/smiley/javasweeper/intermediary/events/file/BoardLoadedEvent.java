package smiley.javasweeper.intermediary.events.file;

import smiley.javasweeper.intermediary.events.model.ModelEvent;
import smiley.javasweeper.model.Board;

public record BoardLoadedEvent(Board board) implements ModelEvent {}
