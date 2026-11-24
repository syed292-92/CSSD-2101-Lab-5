package edu.cssd2101.lab05;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class StructuresTest {
    @Test
    void listEndpointsDuplicatesAndSnapshots() {
        var list = new DoublyLinkedList<String>();
        assertTrue(list.isEmpty());
        assertTrue(list.checkInvariants());
        assertThrows(IndexOutOfBoundsException.class, list::removeFirst);
        assertThrows(IndexOutOfBoundsException.class, list::removeLast);
        list.addFirst("B");
        list.addFirst("A");
        list.addLast("B");
        list.insert(2, "C");
        assertEquals(List.of("A", "B", "C", "B"), list.forward());
        assertEquals(List.of("B", "C", "B", "A"), list.backward());
        assertEquals("A", list.get(0));
        assertEquals("C", list.get(2));
        var snapshot = list.forward();
        var iterator = list.iterator();
        assertTrue(list.remove("B"));
        assertFalse(list.remove("missing"));
        assertEquals(2, list.indexOf("B"));
        assertTrue(list.contains("C"));
        assertEquals("A", list.removeFirst());
        assertEquals("B", list.removeLast());
        assertEquals("C", list.removeAt(0));
        assertTrue(list.checkInvariants());
        assertEquals(List.of("A", "B", "C", "B"), snapshot);
        assertEquals("A", iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add("X"));
        list.addLast("again");
        list.clear();
        assertTrue(list.isEmpty());
        assertTrue(list.checkInvariants());
    }

    @Test
    void listInvalidOperationsPreserveState() {
        var list = new DoublyLinkedList<Integer>();
        list.addLast(10);
        assertThrows(NullPointerException.class, () -> list.insert(0, null));
        assertThrows(NullPointerException.class, () -> list.contains(null));
        assertThrows(NullPointerException.class, () -> list.remove(null));
        for (int index : new int[] {-1, 1, 2}) {
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(index));
            assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(index));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(2, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(-1, 2));
        assertEquals(List.of(10), list.forward());
        assertTrue(list.checkInvariants());
    }

    @Test
    void seededListModelComparisonAfterEveryOperation() {
        var random = new Random(210105L);
        var list = new DoublyLinkedList<Integer>();
        var model = new ArrayList<Integer>();
        for (int step = 0; step < 2500; step++) {
            int value = random.nextInt(31);
            int action = random.nextInt(5);
            if (action < 2 || model.isEmpty()) {
                int index = random.nextInt(model.size() + 1);
                list.insert(index, value);
                model.add(index, value);
            } else if (action == 2) {
                int index = random.nextInt(model.size());
                assertEquals(model.remove(index), list.removeAt(index));
            } else if (action == 3)
                assertEquals(model.remove(Integer.valueOf(value)), list.remove(value));
            else assertEquals(model.contains(value), list.contains(value));
            assertEquals(model, list.forward(), "seed 210105 step " + step);
            assertEquals(model.reversed(), list.backward());
            assertEquals(model.size(), list.size());
            assertTrue(list.checkInvariants(), "step " + step);
        }
    }

    private BinarySearchTree<Integer> example() {
        var tree = new BinarySearchTree<Integer>(Comparator.naturalOrder());
        for (int value : new int[] {8, 3, 10, 1, 6, 14, 4, 7, 13}) assertTrue(tree.insert(value));
        return tree;
    }

    @Test
    void allTreeTraversalsAndHeight() {
        var tree = example();
        assertEquals(List.of(1, 3, 4, 6, 7, 8, 10, 13, 14), tree.inOrder());
        assertEquals(tree.inOrder(), tree.inOrderRecursive());
        assertEquals(List.of(8, 3, 1, 6, 4, 7, 10, 14, 13), tree.preOrder());
        assertEquals(List.of(1, 4, 7, 6, 3, 13, 14, 10, 8), tree.postOrder());
        assertEquals(List.of(8, 3, 10, 1, 6, 14, 4, 7, 13), tree.levelOrder());
        assertEquals(3, tree.height());
        assertEquals(9, tree.size());
        assertTrue(tree.checkInvariants());
        assertFalse(tree.insert(8));
        assertTrue(tree.contains(13));
        assertFalse(tree.contains(12));
        var snapshot = tree.inOrder();
        tree.clear();
        assertEquals(9, snapshot.size());
        assertTrue(tree.isEmpty());
        assertEquals(-1, tree.height());
        assertTrue(tree.checkInvariants());
        assertEquals(List.of(), tree.inOrder());
        assertEquals(List.of(), tree.inOrderRecursive());
        assertEquals(List.of(), tree.preOrder());
        assertEquals(List.of(), tree.postOrder());
        assertEquals(List.of(), tree.levelOrder());
        assertFalse(tree.remove(8));
        assertFalse(tree.contains(8));
    }

    @Test
    void treeDeletionEveryShapeAndSuccessorWithRightChild() {
        for (int[] sequence :
                new int[][] {
                    {8},
                    {8, 3},
                    {8, 10},
                    {8, 3, 10},
                    {8, 3, 14, 10, 12, 16},
                    {8, 3, 10, 1, 6, 14, 4, 7, 13}
                }) {
            var tree = new BinarySearchTree<Integer>(Comparator.naturalOrder());
            var model = new TreeSet<Integer>();
            for (int value : sequence) {
                tree.insert(value);
                model.add(value);
            }
            for (int value : sequence) {
                assertTrue(tree.remove(value));
                model.remove(value);
                assertEquals(new ArrayList<>(model), tree.inOrder());
                assertTrue(tree.checkInvariants());
                assertEquals(model.size(), tree.size());
            }
            assertFalse(tree.remove(999));
            assertTrue(tree.isEmpty());
        }
    }

    @Test
    void comparatorNullAndDuplicatePolicies() {
        var tree = new BinarySearchTree<String>(String.CASE_INSENSITIVE_ORDER);
        assertTrue(tree.insert("Java"));
        assertFalse(tree.insert("JAVA"));
        assertEquals(List.of("Java"), tree.inOrder());
        assertTrue(tree.contains("java"));
        assertTrue(tree.remove("jAvA"));
        assertThrows(NullPointerException.class, () -> tree.insert(null));
        assertThrows(NullPointerException.class, () -> tree.contains(null));
        assertThrows(NullPointerException.class, () -> tree.remove(null));
        assertThrows(NullPointerException.class, () -> new BinarySearchTree<String>(null));
        assertTrue(tree.isEmpty());
        var reverse = new BinarySearchTree<Integer>(Comparator.reverseOrder());
        reverse.insert(2);
        reverse.insert(1);
        reverse.insert(3);
        assertEquals(List.of(3, 2, 1), reverse.inOrder());
        assertTrue(reverse.checkInvariants());
    }

    @Test
    void seededTreeModelComparisonAfterEveryOperation() {
        var random = new Random(210106L);
        var tree = new BinarySearchTree<Integer>(Comparator.naturalOrder());
        var model = new TreeSet<Integer>();
        for (int step = 0; step < 3000; step++) {
            int value = random.nextInt(301) - 150;
            switch (random.nextInt(3)) {
                case 0 -> assertEquals(model.add(value), tree.insert(value));
                case 1 -> assertEquals(model.remove(value), tree.remove(value));
                default -> assertEquals(model.contains(value), tree.contains(value));
            }
            assertEquals(new ArrayList<>(model), tree.inOrder(), "seed 210106 step " + step);
            assertEquals(model.size(), tree.size());
            assertTrue(tree.checkInvariants());
        }
    }

    @Test
    void comparatorFailureBeforeMutationAndCountedDegeneration() {
        var tree =
                new BinarySearchTree<Integer>(
                        (a, b) -> {
                            if (a == 99) throw new IllegalArgumentException("test key");
                            return Integer.compare(a, b);
                        });
        tree.insert(2);
        tree.insert(1);
        tree.insert(3);
        assertThrows(IllegalArgumentException.class, () -> tree.insert(99));
        assertThrows(IllegalArgumentException.class, () -> tree.remove(99));
        assertEquals(List.of(1, 2, 3), tree.inOrder());
        assertTrue(tree.checkInvariants());
        int[] comparisons = {0};
        var chain =
                new BinarySearchTree<Integer>(
                        (a, b) -> {
                            comparisons[0]++;
                            return Integer.compare(a, b);
                        });
        int n = 1000;
        for (int i = 0; i < n; i++) chain.insert(i);
        assertEquals(n - 1, chain.height());
        comparisons[0] = 0;
        assertTrue(chain.contains(n - 1));
        assertEquals(n, comparisons[0]);
        assertEquals(n, chain.inOrder().size());
        assertEquals(n, chain.postOrder().size());
        assertTrue(chain.checkInvariants());
    }

    @Test
    void graphCyclesDuplicatesSelfLoopsAndDisconnectedVertices() {
        var graph = new DirectedGraph<String>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("D", "A");
        graph.addEdge("A", "B");
        graph.addEdge("C", "C");
        graph.addVertex("X");
        assertEquals(List.of("A", "B", "C", "D"), graph.breadthFirst("A"));
        assertEquals(List.of("A", "B", "D", "C"), graph.depthFirst("A"));
        assertEquals(List.of("X"), graph.breadthFirst("X"));
        assertThrows(IllegalArgumentException.class, () -> graph.depthFirst("missing"));
        assertThrows(NullPointerException.class, () -> graph.breadthFirst(null));
        assertThrows(NullPointerException.class, () -> graph.addEdge("Y", null));
        assertThrows(IllegalArgumentException.class, () -> graph.breadthFirst("Y"));
        assertThrows(NullPointerException.class, () -> graph.addEdge(null, "Y"));
        assertThrows(UnsupportedOperationException.class, () -> graph.breadthFirst("A").add("Z"));
    }

    @Test
    void mainRunsTheDocumentedTrace() {
        Main.main(new String[0]);
    }

    @Test
    void structuralSnapshotsShareMutableElementReferences() {
        var value = new StringBuilder("A");
        var list = new DoublyLinkedList<StringBuilder>();
        list.addLast(value);
        var snapshot = list.forward();
        var iterator = list.iterator();
        list.clear();
        value.append("B");
        assertSame(value, snapshot.getFirst());
        assertEquals("AB", snapshot.getFirst().toString());
        assertSame(value, iterator.next());
        assertEquals(1, snapshot.size());
        assertTrue(list.isEmpty());
    }
}
