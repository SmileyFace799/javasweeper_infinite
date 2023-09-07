package smiley.javasweeper.intermediary.events.file;

public class SettingUpdatedEvent implements FileEvent {
    private final String setting;
    private final Object value;

    public SettingUpdatedEvent(String setting, Object value) {
        this.setting = setting;
        this.value = value;
    }

    public String setting() {
        return setting;
    }

    public <V> V value(Class<V> valueType) throws ClassCastException {
        return valueType.cast(value);
    }
}