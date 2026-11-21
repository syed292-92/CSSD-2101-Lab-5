package edu.cssd2101.lab05;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class BaselineTest {
    @Test
    void providedInsertionsAndSnapshotsWork() {
        var list = new DoublyLinkedList<Integer>();
        list.addFirst(2);
        list.addLast(4);
        list.insert(1, 3);
        assertEquals(List.of(2, 3, 4), list.forward());
        assertTrue(list.checkInvariants());
        var tree = new BinarySearchTree<Integer>(Comparator.naturalOrder());
        tree.insert(8);
        tree.insert(3);
        tree.insert(10);
        assertEquals(List.of(3, 8, 10), tree.inOrder());
        assertTrue(tree.checkInvariants());
        Main.main(new String[0]);
    }

    @Test
    void t1RemovalEndpointsDuplicatesAndSnapshots() {
        var list = new DoublyLinkedList<String>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(0));
        assertThrows(IndexOutOfBoundsException.class, list::removeFirst);
        assertThrows(IndexOutOfBoundsException.class, list::removeLast);

        // Build [A, B, C, B]
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        list.addLast("B");
        assertTrue(list.checkInvariants());

        // Snapshot and iterator isolation before removals
        var snapshot = list.forward();
        var iterator = list.iterator();

        // Duplicate removal removes only the first matching "B" -> [A, C, B]
        assertTrue(list.remove("B"));
        assertFalse(list.remove("absent"));
        assertEquals(List.of("A", "C", "B"), list.forward());
        assertEquals(List.of("B", "C", "A"), list.backward());
        assertTrue(list.checkInvariants());

        // Middle removal -> [A, B]
        assertEquals("C", list.removeAt(1));
        assertEquals(List.of("A", "B"), list.forward());
        assertEquals(List.of("B", "A"), list.backward());
        assertTrue(list.checkInvariants());

        // Head removal -> [B]
        assertEquals("A", list.removeFirst());
        assertEquals(List.of("B"), list.forward());
        assertEquals(List.of("B"), list.backward());
        assertTrue(list.checkInvariants());

        // Singleton removal -> []
        assertEquals("B", list.removeLast());
        assertTrue(list.isEmpty());
        assertEquals(List.of(), list.forward());
        assertEquals(List.of(), list.backward());
        assertTrue(list.checkInvariants());

        // Verify earlier snapshot and iterator were unaffected by removals
        assertEquals(List.of("A", "B", "C", "B"), snapshot);
        assertEquals("A", iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void t1InvalidIndexesAndNullsPreserveState() {
        var list = new DoublyLinkedList<Integer>();
        list.addLast(10);
        list.addLast(20);

        assertThrows(NullPointerException.class, () -> list.remove(null));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(2));

        // Verify state and reciprocal links are intact after failed operations
        assertEquals(2, list.size());
        assertEquals(List.of(10, 20), list.forward());
        assertEquals(List.of(20, 10), list.backward());
        assertTrue(list.checkInvariants());
    }
}
