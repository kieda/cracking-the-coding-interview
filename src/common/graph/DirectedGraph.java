package common.graph;

import common.tuple.Tuple2;

import java.util.*;

/**
 *
 * @param <V> type stored on each vertex
 * @param <E> type stored on each edge
 */
public class DirectedGraph<V, E> {
    public DirectedGraph() {
        this(true);
    }

    private final boolean vertexEqualityByData;

    private Map<Vertex, Set<Vertex>> vertices;
    private Map<Tuple2<Vertex, Vertex>, Edge> edges;
    private Map<Vertex, Set<Edge>> graph;

    /**
     * if false, we can have multiple vertices with the same data but will be represented by different vertices.
     * The library user needs to keep track of the vertex pointers in this case. This is mainly useful if we want to
     * swap out the data on each vertex during a traversal
     *
     * If this is true, we disallow setting vertex data after it's been added, as it could change the hashCode position
     * where we would expect it in the graph
     */
    public DirectedGraph(boolean vertexEqualityByData) {
        this.vertexEqualityByData = vertexEqualityByData;
        this.graph = new HashMap<>();
        this.edges = new HashMap<>();
        this.vertices = new HashMap<>();
    }
    public class Vertex {
        private V data;

        public Vertex(V data) {
            this.data = data;
        }

        public V getData() {
            return data;
        }

        public Set<Vertex> getOutgoingVertices() {
            return vertices.get(this);
        }
        public Set<Edge> getOutgoingEdges() {
            return graph.get(this);
        }

        public void setData(V data) {
            if(vertexEqualityByData)
                throw new UnsupportedOperationException();
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if(vertexEqualityByData) return this == o;
            if (this == o) return true;
            if (o == null || !getClass().isInstance(o)) return false;
            Vertex vertex = (Vertex) o;
            return Objects.equals(data, vertex.data);
        }

        @Override
        public int hashCode() {
            return vertexEqualityByData ? Objects.hash(data) : super.hashCode();
        }
    }
    public class Edge {
        private Vertex start;
        private Vertex end;
        private E data;
        public Edge(Vertex start, Vertex end, E edgeData) {
            this.start = start;
            this.end = end;
            this.data = edgeData;
        }

        public void setData(E data) {
            this.data = data;
        }

        public E getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return Objects.equals(start, edge.start) && Objects.equals(end, edge.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        public Vertex getStart() {
            return start;
        }

        public Vertex getEnd() {
            return end;
        }
    }

    public Vertex addVertex(V data) {
        Vertex newVert = new Vertex(data);
        if(!vertexEqualityByData || !graph.containsKey(newVert)) {
            graph.put(newVert, new HashSet<>());
        }
        return newVert;
    }

    /**
     * adds an edge from start to end and returns the new one.
     * If the edge already exists, then the edge's data is updated instead, and the existing edge is returned
     */
    public Edge addEdge(Vertex start, Vertex end, E data) {
        if(!vertices.containsKey(start))
            throw new IllegalArgumentException("Start vertex " + start + " is not in graph!");
        if(!vertices.containsKey(end))
            throw new IllegalArgumentException("End vertex " + end + " is not in graph!");

        Tuple2<Vertex, Vertex> key = Tuple2.make(start, end);
        if(edges.containsKey(key)) {
            Edge existingEdge = edges.get(key);
            existingEdge.setData(data);
            return existingEdge;
        } else {
            Edge newEdge = new Edge(start, end, data);
            edges.put(key, newEdge);
            graph.get(start).add(newEdge);
            return newEdge;
        }
    }
}
