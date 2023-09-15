package com.sk.lru;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Custom {@link java.util.HashMap} using a LRU policy.
 *
 * @param <K> key
 * @param <V> value
 */
final class LruHashMap<K, V> extends LinkedHashMap<K, V> {

    private final long capacity;

    public LruHashMap(long capacity) {
        // Consider capacity sizing as per LinkedHashMap capacity i.e. within integer range limit
        super((int)capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry entry) {
        return size() > capacity;
    }

}
