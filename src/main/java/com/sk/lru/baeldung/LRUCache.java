package com.sk.lru.baeldung;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LRUCache<K, V> implements Cache<K, V> {
    private final int size;
    private final Map<K, LinkedListNode<CacheElement<K, V>>> linkedListNodeMap;
    private final DoublyLinkedList<CacheElement<K, V>> doublyLinkedList;

    public LRUCache(int size) {
        this.size = size;
        this.linkedListNodeMap = new ConcurrentHashMap<>(size);
        this.doublyLinkedList = new DoublyLinkedList<>();
    }

    @Override
    public boolean put(K key, V value) {
        CacheElement<K, V> item = new CacheElement<>(key, value);
        LinkedListNode<CacheElement<K, V>> newNode;
        if (this.linkedListNodeMap.containsKey(key)) {
            LinkedListNode<CacheElement<K, V>> node = this.linkedListNodeMap.get(key);
            newNode = doublyLinkedList.updateAndMoveToFront(node, item);
        } else {
            if (this.size() >= this.size) {
                this.evictElement();
            }
            newNode = this.doublyLinkedList.add(item);
        }
        if (newNode.isEmpty()) {
            return false;
        }
        this.linkedListNodeMap.put(key, newNode);
        return true;
    }

    @Override
    public Optional<V> get(K key) {
        LinkedListNode<CacheElement<K, V>> linkedListNode = this.linkedListNodeMap.get(key);
        if (linkedListNode != null && !linkedListNode.isEmpty()) {
            linkedListNodeMap.put(key, this.doublyLinkedList.moveToFront(linkedListNode));
            return Optional.of(linkedListNode.getElement().getValue());
        }
        return Optional.empty();
    }

    @Override
    public int size() {
        return doublyLinkedList.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        linkedListNodeMap.clear();
        doublyLinkedList.clear();
    }


    private void evictElement() {
        LinkedListNode<CacheElement<K, V>> linkedListNode = doublyLinkedList.removeTail();
        if (linkedListNode.isEmpty()) {
            return;
        }
        linkedListNodeMap.remove(linkedListNode.getElement().getKey());
    }
}