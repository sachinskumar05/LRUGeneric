package com.sk.lru.baeldung;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class LRUCacheUnitPerfNoThreadSafeTest {

    private LRUCache<String, String> cache;
    private final int maxCapacity = 1000000;

    @Before
    public void setUp() {
        cache = new LRUCache<>(maxCapacity);
    }

    @After
    public void tearDown() {
        cache.clear();
        cache = null;
    }

    private static void assertMiss(LRUCache<String, String> cache, String key) {
        assertFalse(cache.get(key).isPresent());
    }
    private static void assertHit(LRUCache<String, String> cache, String key, String value) {
        assertTrue(cache.get(key).isPresent());
        assertThat(cache.get(key).get(), is(value));
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
    public void addSomeDataToCache_WhenGetData_ThenIsEqualWithCacheElement() {
        LRUCache<String, String> lruCache = new LRUCache<>(3);
        lruCache.put("1", "test1");
        lruCache.put("2", "test2");
        lruCache.put("3", "test3");
        assertEquals("test1", lruCache.get("1").get());
        assertEquals("test2", lruCache.get("2").get());
        assertEquals("test3", lruCache.get("3").get());
    }

    @Test
    public void addDataToCacheToTheNumberOfSize_WhenAddOneMoreData_ThenLeastRecentlyDataWillEvict() {
        LRUCache<String, String> lruCache = new LRUCache<>(3);
        lruCache.put("1", "test1");
        lruCache.put("2", "test2");
        lruCache.put("3", "test3");
        lruCache.put("4", "test4");
        assertFalse(lruCache.get("1").isPresent());
    }

}