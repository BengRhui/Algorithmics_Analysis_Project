import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Graph {

    // In graphs, we have vertices and edges.
    // Therefore, we shall create two classes for them.

    // Class for vertex - Record is a new type of class that generates getter, setter and constructor automatically
    public record Vertex(String label) {

        // An array to store the vertex created
        private static final ArrayList<Vertex> vertexArray = new ArrayList<>();

        // Constructor to create the vertex, and this will be stored in an array for tracking later
        public Vertex(String label) {
            this.label = label.trim();
            vertexArray.add(this);
        }

        // Function to return the vertex based on the label
        public static Vertex find(String input) {

            // First, standardize the input
            input = input.strip().toLowerCase();

            // Loop through the vertex array to find the vertex with the same label
            for (Vertex vertex : vertexArray) {
                if (vertex.label.toLowerCase().equals(input)) return vertex;
            }

            // If not found, return null
            return null;
        }

        // Function to print the vertex
        public String toString() {
            return label;
        }
    }

    // Class for edge
    public static class Edge {
        Vertex startNode;
        Vertex endNode;
        double weight;
        
        // Constructor to create the edge
        public Edge(Vertex startNode, Vertex endNode, double weight) {
            this.startNode = startNode;
            this.endNode = endNode;
            this.weight = weight;
        }
        
        // A static function to convert text data into an edge, sample data: (A, B, 1)
        public static Edge fromText(String input) {

            // First, remove the brackets '(' and ')'
            input = input.substring(1, input.length() - 1);
            
            // Then, split them
            String[] splitElements = input.split(",");
            
            // Find the starting vertex, ending vertex, and weight
            Vertex startVertex = switch (Vertex.find(splitElements[0])) {
                case Vertex found -> found;
                case null -> new Vertex(splitElements[0].trim());
            };

            Vertex endVertex = switch (Vertex.find(splitElements[1])) {
                case Vertex found -> found;
                case null -> new Vertex(splitElements[1].trim());
            };
            
            double weight = Double.parseDouble(splitElements[2].trim());
            
            // Lastly, we can create the edge
            return new Edge(startVertex, endVertex, weight);
        }
    }

    // The graph class will be represented using an adjacency list
    private final Map<Vertex, ArrayList<Edge>> adjacencyList = new HashMap<>();

    // Data will be in the format of [(A, B, 1); (B, C, 2); ...]
    public Graph(String inputText) {

        // First, remove the brackets '[' and ']'
        inputText = inputText.trim().substring(1, inputText.length() - 1);
        
        // Split the inputs into arrays
        String[] splitArray = inputText.split("; ");
        
        // Create an array to store the converted edges
        ArrayList<Edge> edgeArray = new ArrayList<>();
        
        // All elements in the array are edges, so convert them first
        for (String textEdge: splitArray) {
            
            // Convert first
            Edge edge = Edge.fromText(textEdge);
            
            // And add to the array
            edgeArray.add(edge);
        }
        
        // Now the edges and vertices are ready, can add into the adjacency list
        for (Edge edge: edgeArray) {
            adjacencyList.putIfAbsent(edge.startNode, new ArrayList<>());
            adjacencyList.putIfAbsent(edge.endNode, new ArrayList<>());
            adjacencyList.get(edge.startNode).add(edge);
        }
    }

    // A function to print the graph details
    public void printGraph() {

        // Loop the map for each vertex and edge array
        adjacencyList.forEach((vertex, edgeArray) -> {

            // Print the vertex
            System.out.print(vertex.label() + " -> [");

            // Followed by the edges in the edge array
            for (Edge edge : edgeArray) {
                System.out.print("(" + edge.endNode.label() + ", w=" + edge.weight + "), ");
            }

            // And close the edge array
            System.out.print("]\n");
        });
    }


    // A function to convert graphs from adjacency lists to matrix
    public double[][] convertToMatrix() {

        // First, get the number of vertices in the graph
        int vertexCount = adjacencyList.size();

        // Then, create a square matrix with the size: count x count
        double[][] matrix = new double[vertexCount][vertexCount];

        // Initialize the matrix where all weights are infinity
        for (double[] row: matrix) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }

        // And for diagonals, set them to zero
        for (int i = 0; i < vertexCount; i++) {
            matrix[i][i] = 0;
        }

        // We collect and sort the vertices first
        ArrayList<Vertex> sortedVertexList = this.getSortedVertices();

        // And we assign an index to each vertex
        Map<Vertex, Integer> vertexWithIndex = new HashMap<>();
        for (int i = 0; i < vertexCount; i++) {
            vertexWithIndex.put(sortedVertexList.get(i), i);
        }

        // Loop through the adjacency list
        for (Map.Entry<Vertex, ArrayList<Edge>> entry : adjacencyList.entrySet()) {

            // Loop through each edge
            for (Edge edge: entry.getValue()) {

                // Find the index of the starting and ending node
                int startingNodeIndex = vertexWithIndex.get(edge.startNode);
                int endingNodeIndex = vertexWithIndex.get(edge.endNode);

                // Assign weight to the matrix
                matrix[startingNodeIndex][endingNodeIndex] = edge.weight;
            }
        }

        // Return the matrix once done
        return matrix;
    }


    // A method to sort the list of vertices
    public ArrayList<Vertex> getSortedVertices() {

        // Get the current list of vertices
        ArrayList<Vertex> vertexList = new ArrayList<>(adjacencyList.keySet());

        // Perform sorting on it
        vertexList.sort((a, b) -> a.label.compareToIgnoreCase(b.label));

        // And return the vertex list
        return vertexList;
    }

    // Provide read-only access to all directed edges (returns copies)
    public ArrayList<Edge> getEdges() {

        ArrayList<Edge> edges = new ArrayList<>();

        adjacencyList.forEach((vertex, edgeArray) -> {
            for (Edge edge : edgeArray) {
                edges.add(new Edge(edge.startNode, edge.endNode, edge.weight));
            }
        });

        return edges;
    }
}
