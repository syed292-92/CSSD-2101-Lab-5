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
}
