package com.sk.lru.memorybased;

import com.sk.lru.memorybased.LruCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link LruCache}.
 *
 * */
public class LruCachePerfomenceTest {

    private final long maxCapacity = 1000000;
    private LruCache<String, String> cache;
    private static void assertMiss(LruCache<String, String> cache, String key) {
        assertNull(cache.get(key));
    }
    private static void assertHit(LruCache<String, String> cache, String key, String value) {
        assertThat(cache.get(key), is(value));
    }

    private static void assertSnapshot(LruCache<String, String> cache, String... keysAndValues) {
        List<String> actualKeysAndValues = new ArrayList<>();
        for (Map.Entry<String, String> entry : cache.snapshot().entrySet()) {
            actualKeysAndValues.add(entry.getKey());
            actualKeysAndValues.add(entry.getValue());
        }
        assertEquals(Arrays.asList(keysAndValues), actualKeysAndValues);
    }

    @Before
    public void setUp() {
        cache = new LruCache<>(maxCapacity);
    }

    @After
    public void tearDown() {
        cache.clear();
        cache = null;
    }

    @Test
    public void defaultMemorySize() {
        assertThat(cache.getMaxMemorySize(), is(maxCapacity * 1024 * 1024));
    }

    @Test
    public void logic() {
        long startNano = System.nanoTime();
        String first_uuid = java.util.UUID.randomUUID().toString();
        cache.put(first_uuid, first_uuid);
        assertHit(cache, first_uuid, first_uuid);

        String second_uuid = java.util.UUID.randomUUID().toString();
        cache.put(second_uuid, second_uuid);
        assertHit(cache, second_uuid, second_uuid);

        String uuid = null;
        for (int i = 0; i < 999998; i++) {
            uuid = java.util.UUID.randomUUID().toString();
            cache.put(uuid, uuid);
//            assertHit(cache, uuid, uuid);
        }
        String one_millth_uuid = uuid;

        System.out.println("insert into cache time taken in micro = " + (System.nanoTime() - startNano)/1000  );

        String last_uuid = java.util.UUID.randomUUID().toString();
        long start = System.nanoTime();
        cache.put(last_uuid, last_uuid);
        System.out.println("insertion and eviction cache time taken in micro = " + (System.nanoTime() - start)/1000  );
        assertHit(cache, last_uuid, last_uuid);
        assertMiss(cache, first_uuid);

        for (int i = 0; i < 1000; i++) {
            last_uuid = java.util.UUID.randomUUID().toString();
            start = System.nanoTime();
            cache.put(last_uuid, last_uuid);
            System.out.println("insertion and eviction cache time taken in micro = " + (System.nanoTime() - start)/1000  );
        }
        assertHit(cache, last_uuid, last_uuid);
        assertMiss(cache, second_uuid);

        assertHit(cache, one_millth_uuid, one_millth_uuid);

    }

    @Test
    public void constructorDoesNotAllowZeroCacheSize() {
        try {
            new LruCache(0);
            fail();
        } catch (IllegalArgumentException expected) {
            //nothing
        }
    }

    @Test
    public void cannotPutNullKey() {
        try {
            cache.put(null, "a");
            fail();
        } catch (NullPointerException expected) {
            // nothing
        }
    }

    @Test
    public void cannotPutNullValue() {
        try {
            cache.put("a", null);
            fail();
        } catch (NullPointerException expected) {
            // nothing
        }
    }

    @Test
    public void evictionWithSingletonCache() {
        LruCache<String, String> cache = new LruCache<>(1);
        cache.put("a", "A");
        cache.put("b", "B");
        assertSnapshot(cache, "b", "B");
    }

    @Test
    public void removeOneItem() {
        LruCache<String, String> cache = new LruCache<>(1);
        cache.put("a", "A");
        cache.put("b", "B");
        assertNull(cache.remove("a"));
        assertSnapshot(cache, "b", "B");
    }

    @Test
    public void cannotRemoveNullKey() {
        try {
            cache.remove(null);
            fail();
        } catch (NullPointerException expected) {
            // nothing
        }
    }

    /**
     * Replacing the value for a key doesn't cause an eviction but it does bring the replaced entry to
     * the front of the queue.
     */
    @Test
    public void putCauseEviction() {
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");
        cache.put("b", "D");
        assertSnapshot(cache, "a", "A", "c", "C", "b", "D");
    }

    @Test
    public void throwsWithNullKey() {
        try {
            cache.get(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // nothing
        }
    }

    @Test
    public void clear() {
        cache.put("a", "a");
        cache.put("b", "b");
        cache.put("c", "c");
        cache.clear();
        assertThat(cache.snapshot().size(), is(0));
    }

}