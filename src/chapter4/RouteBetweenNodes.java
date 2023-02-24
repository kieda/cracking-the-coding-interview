package chapter4;

import common.graph.DirectedGraph;
import common.lists.DoubleLinkedList;
import common.lists.SimpleQueue;
import common.lists.SimpleStack;
import common.lists.SingleLinkedList;

import java.util.HashSet;
import java.util.Set;

/**
 * Given a directed graph, design an algorithm to find out whether there is a
 * route between two nodes.
 */
public class RouteBetweenNodes<V, E> {

    // todo - route between nodes parallel
    //        use graph contraction : https://www.cs.cmu.edu/afs/cs/academic/class/15210-f12/www/lectures/lecture16.pdf

    private boolean routeBetweenNodesDFS(DirectedGraph<V, E>.Vertex v1, DirectedGraph<V, E>.Vertex v2, Set<DirectedGraph<V, E>.Vertex> visited) {
        if(v1 == v2)
            return true;
        visited.add(v1);

        for(DirectedGraph<V, E>.Vertex outgoing : v1.getOutgoingVertices()) {
            if(!visited.contains(outgoing)) {
                if(routeBetweenNodesDFS(outgoing, v2, visited))
                    return true;
            }
        }
        return false;
    }

    public boolean routeBetweenNodesDFS(DirectedGraph<V, E> graph, DirectedGraph<V, E>.Vertex v1, DirectedGraph<V, E>.Vertex v2) {
        SimpleStack<DirectedGraph<V, E>.Vertex> nodesToProcess = new SingleLinkedList<>();
        Set<DirectedGraph<V, E>.Vertex> visited = new HashSet<>();
        nodesToProcess.addFirst(v1);

        while(!nodesToProcess.isEmpty()) {
            DirectedGraph<V, E>.Vertex front = nodesToProcess.getFirst();
            visited.add(front);
            for(DirectedGraph<V, E>.Vertex outgoing : front.getOutgoingVertices()) {
                if(!visited.contains(outgoing)) {
                    nodesToProcess.addFirst(outgoing);
                }
            }
        }

        return false;
    }

    public boolean routeBetweenNodesBFS(DirectedGraph<V, E> graph, DirectedGraph<V, E>.Vertex v1, DirectedGraph<V, E>.Vertex v2) {
        SimpleQueue<DirectedGraph<V, E>.Vertex> nodesToProcess = new DoubleLinkedList<>();
        Set<DirectedGraph<V, E>.Vertex> visited = new HashSet<>();
        nodesToProcess.addFirst(v1);
        while(!nodesToProcess.isEmpty()) {
            DirectedGraph<V, E>.Vertex front = nodesToProcess.getLast();
            if(front == v2)
                return true;
            for(DirectedGraph<V, E>.Vertex outgoing : front.getOutgoingVertices()) {
                if(!visited.contains(outgoing)) {
                    nodesToProcess.addFirst(outgoing);
                    visited.add(outgoing);
                }
            }
        }
        return false;
    }
}
