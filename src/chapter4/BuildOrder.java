package chapter4;

import common.graph.DirectedGraph;
import common.lists.DoubleLinkedList;
import common.lists.SimpleQueue;

import java.util.HashMap;
import java.util.Map;

/**
 * You are given a list of projects and a list of dependencies (which is a list of pairs of
 * projects, where the second project is dependent on the first project). All of a project's dependencies
 * must be built before the project is. Find a build order that will allow the projects to be built. If there
 * is no valid build order, return an error.
 * EXAMPLE
 *      Input:
 *          projects: a, b, c, d, e, f
 *          dependencies: (a, d), (f, b), (b, d), (f, a), (d, c)
 *      Output: f, e, a, b, d, c
 */
public class BuildOrder<V, E> {
    public SimpleQueue<DirectedGraph<V, E>.Vertex> topsort(DirectedGraph<V, E> graph) {
        SimpleQueue<DirectedGraph<V, E>.Vertex> result = new DoubleLinkedList<>();
        SimpleQueue<DirectedGraph<V, E>.Vertex> nextVertices = new DoubleLinkedList<>();
        Map<DirectedGraph<V, E>.Vertex, Integer> incomingCount = new HashMap<>();
        for(DirectedGraph<V, E>.Vertex vertex : graph.getVertices()) {
            for(DirectedGraph<V, E>.Vertex outgoing : vertex.getOutgoingVertices()) {
                incomingCount.put(outgoing, incomingCount.getOrDefault(outgoing, 0) + 1);
            }
        }

        incomingCount.forEach((v, count) -> {
            if(count == 0)
                nextVertices.addFirst(v);
        });
        while(!nextVertices.isEmpty()) {
            DirectedGraph<V, E>.Vertex front = nextVertices.getLast();
            nextVertices.removeLast();
            result.addFirst(front);
            for(DirectedGraph<V, E>.Vertex outgoing : front.getOutgoingVertices()) {
                int newCount = incomingCount.get(outgoing) - 1;
                incomingCount.put(outgoing, newCount);
                if(newCount == 0) {
                    nextVertices.addFirst(outgoing);
                }
            }
        }
        if(incomingCount.values().stream().allMatch(count -> count == 0)
           && graph.getVertices().size() == incomingCount.size()) {
            return result;
        }
        return null;
    }
}
