package edu.cssd2101.lab05;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * An unbalanced, node-based search tree. Comparator equivalence defines duplicates; insertion of an
 * equivalent value returns false and preserves the original object. Null values are rejected, and
 * stored values must not change their ordering keys. Returned snapshots share element references;
 * they do not deep-copy elements. This mutable structure is not thread-safe.
 *
 * @param <E> element type
 */
public final class BinarySearchTree<E> {
    private static final class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;

        Node(E value) {
            this.value = value;
        }
    }

    private record Frame<E>(Node<E> node, E lower, E upper) {}

    private final Comparator<? super E> comparator;
    private Node<E> root;
    private int size;

    /**
     * Creates an empty tree with an explicit stable total ordering.
     *
     * @param comparator ordering used by every tree operation
     * @throws NullPointerException if comparator is null
     */
    public BinarySearchTree(Comparator<? super E> comparator) {
        this.comparator = Objects.requireNonNull(comparator);
    }

    /**
     * Reports the node count.
     *
     * @return number of comparator-distinct elements
     */
    public int size() {
        return size;
    }

    /**
     * Reports whether the tree is empty.
     *
     * @return true exactly when the size is zero
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Inserts iteratively without exposing nodes.
     *
     * @param value non-null element
     * @return true if a new equivalence class was added
     * @throws NullPointerException if value is null
     */
    public boolean insert(E value) {
        Objects.requireNonNull(value);
        if (root == null) {
            comparator.compare(value, value);
            root = new Node<>(value);
            size++;
            return true;
        }
        Node<E> current = root;
        while (true) {
            int order = comparator.compare(value, current.value);
            if (order == 0) return false;
            if (order < 0) {
                if (current.left == null) {
                    current.left = new Node<>(value);
                    size++;
                    return true;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node<>(value);
                    size++;
                    return true;
                }
                current = current.right;
            }
        }
    }

    /**
     * Searches iteratively in time proportional to height.
     *
     * @param value non-null comparison key
     * @return true if a comparator-equivalent element exists
     * @throws NullPointerException if value is null
     */
    public boolean contains(E value) {
        Objects.requireNonNull(value);
        Node<E> current = root;
        while (current != null) {
            int order = comparator.compare(value, current.value);
            if (order == 0) return true;
            current = order < 0 ? current.left : current.right;
        }
        return false;
    }

    /**
     * Deletes iteratively, using the inorder successor for a node with two children.
     *
     * @param value non-null comparison key
     * @return true if an element was removed
     * @throws NullPointerException if value is null
     */
    public boolean remove(E value) {
        // TODO T2 tree deletion: follow the task contract in the handout.
        throw new UnsupportedOperationException("T2 tree deletion not implemented");
    }

    /**
     * Traverses left, root, right with an explicit stack.
     *
     * @return unmodifiable ascending structural snapshot according to the comparator
     */
    public List<E> inOrder() {
        List<E> result = new ArrayList<>(size);
        ArrayDeque<Node<E>> stack = new ArrayDeque<>();
        Node<E> current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.value);
            current = current.right;
        }
        return List.copyOf(result);
    }

    /**
     * Traverses recursively for comparison with the iterative algorithm. Use only on modest
     * heights; Java does not guarantee tail-call elimination.
     *
     * @return unmodifiable ascending structural snapshot
     * @throws StackOverflowError if tree height exceeds the available call stack
     */
    public List<E> inOrderRecursive() {
        List<E> result = new ArrayList<>(size);
        visit(root, result);
        return List.copyOf(result);
    }

    private void visit(Node<E> node, List<E> result) {
        if (node == null) return;
        visit(node.left, result);
        result.add(node.value);
        visit(node.right, result);
    }

    /**
     * Traverses root, left, right using an explicit stack.
     *
     * @return unmodifiable preorder structural snapshot
     */
    public List<E> preOrder() {
        // TODO T3 preorder: follow the task contract in the handout.
        return List.copyOf(depthFirst(false));
    }

    /**
     * Traverses left, right, root using reverse root-right-left order.
     *
     * @return unmodifiable postorder structural snapshot
     */
    public List<E> postOrder() {
        // TODO T3 postorder: follow the task contract in the handout.
        List<E> result = depthFirst(true);
        Collections.reverse(result);
        return List.copyOf(result); 
    }

    private List<E> depthFirst(boolean reverse) {
        List<E> result = new ArrayList<>(size);
        ArrayDeque<Node<E>> stack = new ArrayDeque<>();

        if (root != null) {
            stack.push(root);
        }

        while (!stack.isEmpty()) {
            Node<E> current = stack.pop();
            result.add(current.value);

            if (reverse) {
                if (current.left != null) {
                    stack.push(current.left);
                }

                if (current.right != null) {
                    stack.push(current.right);
                }
            }
            else {
                if (current.right != null) {
                    stack.push(current.right);
                }

                if (current.left != null) {
                    stack.push(current.left);
                }
            }
        }

        return result;
    }

    /**
     * Traverses breadth first, enqueueing left before right.
     *
     * @return unmodifiable level-order structural snapshot
     */
    public List<E> levelOrder() {
        // TODO T3 breadth first: follow the task contract in the handout.
        List<E> result = new ArrayList<>(size);
        ArrayDeque<Node<E>> queue = new ArrayDeque();

        if (root != null) {
            queue.add(root);
        }

        while (!queue.isEmpty()) {
            Node<E> current = queue.remove();
            result.add(current.value);

            if (current.left != null) {
                queue.add(current.left);
            }
        }

        return List.copyOf(result);
    }

    /**
     * Counts edges on the longest root-to-leaf path using breadth-first levels.
     *
     * @return minus one for empty, zero for a leaf, otherwise maximum edge depth
     */
    public int height() {
        int height = -1;
        ArrayDeque<Node<E>> queue = new ArrayDeque<>();
        if (root != null) queue.add(root);
        while (!queue.isEmpty()) {
            int width = queue.size();
            for (int i = 0; i < width; i++) {
                Node<E> current = queue.remove();
                if (current.left != null) queue.add(current.left);
                if (current.right != null) queue.add(current.right);
            }
            height++;
        }
        return height;
    }

    /**
     * Checks strict ancestor bounds, identity cycles, null payloads, and size.
     *
     * @return true if the complete representation satisfies its invariant
     */
    public boolean checkInvariants() {
        if (root == null) return size == 0;
        Set<Node<E>> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        ArrayDeque<Frame<E>> stack = new ArrayDeque<>();
        stack.push(new Frame<>(root, null, null));
        while (!stack.isEmpty()) {
            Frame<E> frame = stack.pop();
            Node<E> current = frame.node();
            if (!seen.add(current) || current.value == null) return false;
            if (frame.lower() != null && comparator.compare(current.value, frame.lower()) <= 0)
                return false;
            if (frame.upper() != null && comparator.compare(current.value, frame.upper()) >= 0)
                return false;
            if (current.left != null)
                stack.push(new Frame<>(current.left, frame.lower(), current.value));
            if (current.right != null)
                stack.push(new Frame<>(current.right, current.value, frame.upper()));
        }
        return seen.size() == size;
    }

    /** Drops all nodes; previously returned snapshots remain valid. */
    public void clear() {
        root = null;
        size = 0;
    }
}
