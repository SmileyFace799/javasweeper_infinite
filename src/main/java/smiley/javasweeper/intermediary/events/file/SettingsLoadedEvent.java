package smiley.javasweeper.intermediary.events.file;

import smiley.javasweeper.filestorage.Settings;

public record SettingsLoadedEvent(Settings settings) implements FileEvent {}
