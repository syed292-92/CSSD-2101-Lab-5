package edu.cssd2101.lab05;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A directed, unweighted graph with insertion-order neighbors, no duplicate edges, and permitted
 * self-loops. Vertices must have stable equality and hash codes. JDK collections store adjacency;
 * the lab's list and tree are independently node-based. Returned snapshots share vertex references
 * and do not deep-copy vertices.
 *
 * @param <V> vertex type
 */
public final class DirectedGraph<V> {
    private final Map<V, Set<V>> edges = new LinkedHashMap<>();

    /** Creates an empty graph. */
    public DirectedGraph() {}

    /**
     * Adds a vertex if absent.
     *
     * @param vertex non-null vertex
     * @throws NullPointerException if vertex is null
     */
    public void addVertex(V vertex) {
        edges.computeIfAbsent(Objects.requireNonNull(vertex), ignored -> new LinkedHashSet<>());
    }

    /**
     * Adds one directed edge, automatically registering both endpoints.
     *
     * @param from source vertex
     * @param to destination vertex
     * @throws NullPointerException if either endpoint is null; state is unchanged
     */
    public void addEdge(V from, V to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        addVertex(from);
        addVertex(to);
        edges.get(from).add(to);
    }

    /**
     * Explores reachable vertices breadth first, marking on enqueue.
     *
     * @param start existing start vertex
     * @return unmodifiable discovery-order structural snapshot
     * @throws IllegalArgumentException if start is absent
     * @throws NullPointerException if start is null
     */
    public List<V> breadthFirst(V start) {
        // TODO T4 graph BFS: follow the task contract in the handout.
        throw new UnsupportedOperationException("T4 graph BFS not implemented");
    }

    /**
     * Explores reachable vertices depth first, honoring neighbor insertion order.
     *
     * @param start existing start vertex
     * @return unmodifiable visit-order structural snapshot
     * @throws IllegalArgumentException if start is absent
     * @throws NullPointerException if start is null
     */
    public List<V> depthFirst(V start) {
        // TODO T4 graph DFS: follow the task contract in the handout.
        throw new UnsupportedOperationException("T4 graph DFS not implemented");
    }

    private List<V> traverse(V start, boolean depth) {
        throw new UnsupportedOperationException("T4 traversal helper");
    }
}
