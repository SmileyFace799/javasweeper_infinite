package smiley.javasweeper.misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Map2D<K1, K2, V> extends HashMap<Map2D.KeyPair<K1, K2>, V> implements Iterable<V> {

    public boolean containsKey(Object key1, Object key2) {
        return containsKey(new KeyPair<>(key1, key2));
    }

    public V get(Object key1, Object key2) {
        return get(new KeyPair<>(key1, key2));
    }

    @Nullable
    public V put(K1 key1, K2 key2, V value) {
        return put(new KeyPair<>(key1, key2), value);
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return values().iterator();
    }

    public Stream<V> stream() {
        return values().stream();
    }

    public void computeIfAbsent(K1 key1, K2 key2, BiFunction<? super K1, ? super K2, ? extends V> mappingFunction) {
        if (!containsKey(key1, key2)) {
            put(key1, key2, mappingFunction.apply(key1, key2));
        }
    }

    public record KeyPair<K1, K2>(K1 key1, K2 key2) {}
}
