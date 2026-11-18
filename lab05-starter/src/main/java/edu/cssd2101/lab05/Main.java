package edu.cssd2101.lab05;

import java.util.Comparator;

/** Working baseline for the structures exercises. */
public final class Main {
    private Main() {}

    /**
     * Runs provided insertion and inorder operations.
     *
     * @param args unused arguments
     */
    public static void main(String[] args) {
        var list = new DoublyLinkedList<String>();
        list.addLast("A");
        list.addLast("C");
        list.insert(1, "B");
        System.out.println("Starter list: " + list.forward());
        var tree = new BinarySearchTree<Integer>(Comparator.naturalOrder());
        tree.insert(8);
        tree.insert(3);
        tree.insert(10);
        System.out.println("Starter tree: " + tree.inOrder());
    }
}
