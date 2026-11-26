package edu.cssd2101.lab05;

import java.util.*;

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
        return traverse(start, false);
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
        return traverse(start, true);
    }

    private List<V> traverse(V start, boolean depth) {
        Objects.requireNonNull(start, "start must not be null");
        //Rejects a null start argument immediately with NullPointerException interacting with graph.

        if (!edges.containsKey(start)) {
            // rejects any unknown start vertex per graph contract
            throw new IllegalArgumentException("Unknown start vertex: " + start);
        }

        //set to record vertices already marked visited so cycles and self-loops do not repeat work
        Set<V> visited = new HashSet<>();
        //to record reachable vertices in sequential order they are visited
        List<V> order = new ArrayList<>();

        //a double-ended queue used for breadth-first searching
        Deque<V> frontier = new ArrayDeque<>();

        if (!depth) {
            //Marks start as visited when enqueued (BFS enqueue marking rule)
            visited.add(start);

            //Places start at the back of the queue to begin traversal.
            frontier.addLast(start);

            while (!frontier.isEmpty()) {
                V current = frontier.removeFirst();
                order.add(current);
                for (V neighbor : edges.get(current)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        frontier.addLast(neighbor);
                    }
                }
            }
        } else {
            // Push start onto the stack before starting the loop, so while loop can start
            frontier.push(start);

            while (!frontier.isEmpty()) {
                V current = frontier.pop();

                // Skips vertices already marked
                if (visited.contains(current)) {
                    continue;
                }
                visited.add(current);
                order.add(current);

                // Copies outgoing neighbors into a list to iterate in reverse insertion order.
                List<V> neighbors = List.copyOf(edges.get(current));
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    V neighbor = neighbors.get(i);

                    //pushes unvisited neighbors in reverse, so it is in sequential order
                    if (!visited.contains(neighbor)) {
                        frontier.push(neighbor);
                    }
                }
            }
        }

        //list snapshot
        return List.copyOf(order);
    }
}