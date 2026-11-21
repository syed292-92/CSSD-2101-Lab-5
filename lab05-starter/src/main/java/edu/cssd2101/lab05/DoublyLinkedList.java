package edu.cssd2101.lab05;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A generic doubly linked list with private nodes and unmodifiable structural
 * snapshot iteration.
 * Null elements are rejected. Equal elements are allowed; removal by value
 * removes the first.
 * Snapshots copy links into a list but share element references; mutable
 * element changes remain
 * visible. This mutable structure is not thread-safe.
 *
 * @param <E> element type
 */
public final class DoublyLinkedList<E> implements Iterable<E> {
    private static final class Node<E> {
        final E value;
        Node<E> previous;
        Node<E> next;

        Node(E value) {
            this.value = value;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    /** Creates an empty list. */
    public DoublyLinkedList() {
    }

    /**
     * Reports the element count.
     *
     * @return number of linked nodes
     */
    public int size() {
        return size;
    }

    /**
     * Reports whether there are no elements.
     *
     * @return true exactly when size is zero
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Prepends an element in constant time.
     *
     * @param value non-null element
     * @throws NullPointerException if value is null
     */
    public void addFirst(E value) {
        insert(0, value);
    }

    /**
     * Appends an element in constant time.
     *
     * @param value non-null element
     * @throws NullPointerException if value is null
     */
    public void addLast(E value) {
        insert(size, value);
    }

    /**
     * Inserts before an index; size appends. Validation precedes mutation.
     *
     * @param index position from zero through size inclusive
     * @param value non-null element
     * @throws IndexOutOfBoundsException if the position is invalid
     * @throws NullPointerException      if value is null
     */
    public void insert(int index, E value) {
        Objects.checkIndex(index, size + 1);
        Objects.requireNonNull(value);
        Node<E> successor = index == size ? null : node(index);
        Node<E> predecessor = successor == null ? tail : successor.previous;
        Node<E> added = new Node<>(value);
        added.previous = predecessor;
        added.next = successor;
        if (predecessor == null)
            head = added;
        else
            predecessor.next = added;
        if (successor == null)
            tail = added;
        else
            successor.previous = added;
        size++;
    }

    /**
     * Reads by index, walking from the nearer endpoint.
     *
     * @param index position from zero through size minus one
     * @return element at index
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public E get(int index) {
        return node(index).value;
    }

    /**
     * Removes and returns an indexed element.
     *
     * @param index existing position
     * @return removed element
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public E removeAt(int index) {
        // TODO T1 list unlink: follow the task contract in the handout.
        Node<E> target = node(index);
        Node<E> predecessor = target.previous;
        Node<E> successor = target.next;

        if (predecessor == null) {
            head = successor;
        } else {
            predecessor.next = successor;
        }

        if (successor == null) {
            tail = predecessor;
        } else {
            successor.previous = predecessor;
        }

        target.previous = null;
        target.next = null;
        size--;
        return target.value;
    }

    /**
     * Removes the first element.
     *
     * @return removed element
     * @throws IndexOutOfBoundsException if empty
     */
    public E removeFirst() {
        return removeAt(0);
    }

    /**
     * Removes the last element.
     *
     * @return removed element
     * @throws IndexOutOfBoundsException if empty
     */
    public E removeLast() {
        return removeAt(size - 1);
    }

    /**
     * Finds the first equal element.
     *
     * @param value non-null search value
     * @return first index, or minus one if absent
     * @throws NullPointerException if value is null
     */
    public int indexOf(E value) {
        Objects.requireNonNull(value);
        int index = 0;
        for (Node<E> current = head; current != null; current = current.next, index++)
            if (value.equals(current.value))
                return index;
        return -1;
    }

    /**
     * Removes the first equal element, leaving an absent value unchanged.
     *
     * @param value non-null search value
     * @return whether an element was removed
     * @throws NullPointerException if value is null
     */
    public boolean remove(E value) {
        int index = indexOf(value);
        if (index < 0)
            return false;
        removeAt(index);
        return true;
    }

    /**
     * Checks membership using equality.
     *
     * @param value non-null search value
     * @return whether an equal element exists
     * @throws NullPointerException if value is null
     */
    public boolean contains(E value) {
        return indexOf(value) >= 0;
    }

    /**
     * Produces a forward unmodifiable structural snapshot.
     *
     * @return elements from head to tail
     */
    public List<E> forward() {
        List<E> result = new ArrayList<>(size);
        for (Node<E> current = head; current != null; current = current.next)
            result.add(current.value);
        return List.copyOf(result);
    }

    /**
     * Produces a backward unmodifiable structural snapshot.
     *
     * @return elements from tail to head
     */
    public List<E> backward() {
        List<E> result = new ArrayList<>(size);
        for (Node<E> current = tail; current != null; current = current.previous)
            result.add(current.value);
        return List.copyOf(result);
    }

    /** Drops all node references; existing snapshots remain valid. */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Checks endpoints, reciprocal links, counts, and cycles in both directions.
     *
     * @return true if the representation is valid
     */
    public boolean checkInvariants() {
        if (size == 0)
            return head == null && tail == null;
        if (head == null || tail == null || head.previous != null || tail.next != null)
            return false;
        int count = 0;
        Node<E> prior = null;
        for (Node<E> current = head; current != null; current = current.next) {
            if (++count > size || current.previous != prior || current.value == null)
                return false;
            prior = current;
        }
        if (prior != tail || count != size)
            return false;
        count = 0;
        Node<E> after = null;
        for (Node<E> current = tail; current != null; current = current.previous) {
            if (++count > size || current.next != after)
                return false;
            after = current;
        }
        return after == head && count == size;
    }

    /**
     * Returns an iterator over an unmodifiable structural snapshot. Removal is
     * unsupported.
     *
     * @return iterator unaffected by subsequent list mutations
     */
    @Override
    public Iterator<E> iterator() {
        return forward().iterator();
    }

    private Node<E> node(int index) {
        Objects.checkIndex(index, size);
        if (index < size / 2) {
            Node<E> current = head;
            for (int i = 0; i < index; i++)
                current = current.next;
            return current;
        }
        Node<E> current = tail;
        for (int i = size - 1; i > index; i--)
            current = current.previous;
        return current;
    }
}
