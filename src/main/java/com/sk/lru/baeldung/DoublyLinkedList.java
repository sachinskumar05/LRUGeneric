package com.sk.lru.baeldung;


import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class DoublyLinkedList<T> {

    private DummyNode<T> dummyNode;
    private LinkedListNode<T> head;
    private LinkedListNode<T> tail;
    private AtomicInteger size;

    public DoublyLinkedList() {
        this.dummyNode = new DummyNode<T>(this);
        clear();
    }

    public void clear() {
        head = dummyNode;
        tail = dummyNode;
        size = new AtomicInteger(0);
    }

    public int size() {
        return size.get();
    }

    public boolean isEmpty() {
        return head.isEmpty();
    }

    public boolean contains(T value) {
        return search(value).hasElement();
    }

    public LinkedListNode<T> search(T value) {
        return head.search(value);
    }

    public LinkedListNode<T> add(T value) {
        head = new Node<T>(value, head, this);
        if (tail.isEmpty()) {
            tail = head;
        }
        size.incrementAndGet();
        return head;
    }

    public boolean addAll(Collection<T> values) {
        for (T value : values) {
            if (add(value).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public LinkedListNode<T> remove(T value) {
        LinkedListNode<T> linkedListNode = head.search(value);
        if (!linkedListNode.isEmpty()) {
            if (linkedListNode == tail) {
                tail = tail.getPrev();
            }
            if (linkedListNode == head) {
                head = head.getNext();
            }
            linkedListNode.detach();
            size.decrementAndGet();
        }
        return linkedListNode;
    }

    public LinkedListNode<T> removeTail() {
        LinkedListNode<T> oldTail = tail;
        if (oldTail == head) {
            tail = head = dummyNode;
        } else {
            tail = tail.getPrev();
            oldTail.detach();
        }
        if (!oldTail.isEmpty()) {
            size.decrementAndGet();
        }
        return oldTail;
    }

    public LinkedListNode<T> moveToFront(LinkedListNode<T> node) {
        return node.isEmpty() ? dummyNode : updateAndMoveToFront(node, node.getElement());
    }

    public LinkedListNode<T> updateAndMoveToFront(LinkedListNode<T> node, T newValue) {
        if (node.isEmpty() || (this != (node.getListReference()))) {
            return dummyNode;
        }
        detach(node);
        add(newValue);
        return head;
    }

    private void detach(LinkedListNode<T> node) {
        if (node != tail) {
            node.detach();
            if (node == head) {
                head = head.getNext();
            }
            size.decrementAndGet();
        } else {
            removeTail();
        }
    }
}