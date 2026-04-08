import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {

    // A record to store each graph
    private record GraphInList(Graph graph, Graph.Vertex start, Graph.Vertex end) {}
    private record GraphInMatrix(double[][] matrix, ArrayList<Graph.Vertex> vertexList, Graph.Vertex start, Graph.Vertex end) {}

    private static final ArrayList<GraphInList> adjacency_list_array = new ArrayList<>();
    private static final ArrayList<GraphInMatrix> adjacency_matrix_array = new ArrayList<>();

    private static final ArrayList<Long> bellmanFordTime = new ArrayList<>();
    private static final ArrayList<Long> floydWarshallTime = new ArrayList<>();

    // Main function here
    public static void main(String[] args) {

        // A sample data for testing
        String example_input = """
        [(A,B,2); (A,C,3); (B,C,1); (C,D,4); (D,A,2); (B,D,3)] | A | D
        [(A,B,1); (A,C,4); (A,D,2); (B,C,2); (B,E,3); (C,D,1); (D,E,2); (E,A,5)] | A | E
        [(A,B,2); (A,C,5); (B,C,1); (B,D,3); (C,D,2); (C,E,4); (D,E,1); (E,F,3); (F,A,2); (D,F,5)] | A | F
        [(A,B,3); (A,C,2); (A,D,4); (B,C,1); (B,E,5); (C,D,3); (C,F,6); (D,E,2); (E,F,1); (F,A,4); (D,F,2)] | A | F
        """;

        // Run correctness checks before timing benchmarks
        runBellmanEmpirical();

        // Convert the string data into graph and vertex objects
        preprocessing_steps(example_input);

        // Retrieve the number of graphs from input
        int numOfGraphs = adjacency_list_array.size();
        int iterationCount = 1000;

        // Check algorithm performance
        for (int iter = 0; iter < iterationCount; iter++) {
            for (int i = 0; i < numOfGraphs; i++) {

                // Bellman-Ford
                System.out.println("--- Bellman-Ford: Graph " + (i + 1) + ": ---");
                System.out.println(Bellman_Ford.getShortestPath(
                        adjacency_list_array.get(i).graph,
                        adjacency_list_array.get(i).start,
                        adjacency_list_array.get(i).end
                ));
                System.out.println();

                // Floyd-Warshall
                System.out.println("--- Floyd-Warshall: Graph " + (i + 1) + ": ---");
                System.out.println(Floyd_Warshall.getShortestPath(
                        adjacency_matrix_array.get(i).matrix,
                        adjacency_matrix_array.get(i).vertexList,
                        adjacency_matrix_array.get(i).start,
                        adjacency_matrix_array.get(i).end
                ));
                System.out.println();
            }
        }

        // Calculate and print average time
        double bellmanFordAvg = bellmanFordTime.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);

        double floydWarshallAvg = floydWarshallTime.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);

        System.out.println("Bellman-Ford Time (nanoseconds): " + bellmanFordAvg);
        System.out.println("Floyd-Warshall Time (nanoseconds): " + floydWarshallAvg);

        // Convert output to CSV
        try {
            PrintWriter writer = new PrintWriter("data.csv");
            writer.println("Run,Edge Num,Vertex Num,Bellman-Ford,Floyd-Warshall");
            for (int i = 0; i < numOfGraphs * iterationCount; i++) {
                writer.println((i + 1) + "," +
                        adjacency_list_array.get(i % 4).graph.getEdges().size() + "," +
                        adjacency_list_array.get(i % 4).graph.getSortedVertices().size() + "," +
                        bellmanFordTime.get(i) + "," +
                        floydWarshallTime.get(i));
            }
            writer.close();
        } catch (FileNotFoundException _) {}
    }

    // Empirical test cases for Bellman-Ford correctness
    public static void runBellmanEmpirical() {
        System.out.println("Running Empirical Tests...\n");

        int pass = 0;
        int total = 4;

        // Test 1: Normal case, shortest path A -> C -> B = 3
        Graph g1 = new Graph("[(A,B,4); (A,C,2); (C,B,1)]");
        Graph.Vertex e1Start = Graph.Vertex.find("A");
        Graph.Vertex e1End = Graph.Vertex.find("B");
        String result1 = (e1Start == null || e1End == null)
                ? "Error. Test setup failed."
                : Bellman_Ford.getShortestPath(g1, e1Start, e1End);
        boolean test1 = result1.contains("is: 3.0") || result1.contains("is: 3");
        System.out.println("Test 1 Result:\n" + result1);
        System.out.println("Expected: Distance = 3");
        System.out.println("PASS = " + test1 + "\n");
        if (test1) pass++;

        // Test 2: No path from X to Y
        Graph g2 = new Graph("[(X,Z,1); (W,Y,1)]");
        Graph.Vertex e2Start = Graph.Vertex.find("X");
        Graph.Vertex e2End = Graph.Vertex.find("Y");
        String result2 = (e2Start == null || e2End == null)
                ? "Error. Test setup failed."
                : Bellman_Ford.getShortestPath(g2, e2Start, e2End);
        boolean test2 = result2.contains("Not found") || result2.contains("No path");
        System.out.println("Test 2 Result:\n" + result2);
        System.out.println("Expected: Not found");
        System.out.println("PASS = " + test2 + "\n");
        if (test2) pass++;

        // Test 3: Negative edge is supported, R -> Q -> R = 1
        Graph g3 = new Graph("[(P,Q,-2); (Q,R,3)]");
        Graph.Vertex e3Start = Graph.Vertex.find("P");
        Graph.Vertex e3End = Graph.Vertex.find("R");
        String result3 = (e3Start == null || e3End == null)
                ? "Error. Test setup failed."
                : Bellman_Ford.getShortestPath(g3, e3Start, e3End);
        boolean test3 = result3.contains("is: 1.0") || result3.contains("is: 1");
        System.out.println("Test 3 Result:\n" + result3);
        System.out.println("Expected: Distance = 1");
        System.out.println("PASS = " + test3 + "\n");
        if (test3) pass++;

        // Test 4: Negative cycle should be detected
        Graph g4 = new Graph("[(U,V,1); (V,U,-2)]");
        Graph.Vertex e4Start = Graph.Vertex.find("U");
        Graph.Vertex e4End = Graph.Vertex.find("V");
        String result4 = (e4Start == null || e4End == null)
                ? "Error. Test setup failed."
                : Bellman_Ford.getShortestPath(g4, e4Start, e4End);
        boolean test4 = result4.toLowerCase().contains("negative cycle");
        System.out.println("Test 4 Result:\n" + result4);
        System.out.println("Expected: Negative cycle detected");
        System.out.println("PASS = " + test4 + "\n");
        if (test4) pass++;

        System.out.println("Empirical Tests Passed: " + pass + "/" + total + "\n");
    }

    // This is the function where data is converted from text to graph and vertex objects
    public static void preprocessing_steps(String input_data) {

        // First, split the text into different graphs
        String[] graphs_string = input_data.split("\n");

        // Then, loop each graph
        for (String graph_string: graphs_string) {

            // Split the input into three: graph, starting vertex, ending vertex
            String[] split_array = graph_string.split("\\s*\\|\\s*");
            String graph_in_string = split_array[0];
            String start_in_string = split_array[1];
            String end_in_string = split_array[2];

            // Generate the graph
            Graph graph = new Graph(graph_in_string);

            // Generate the starting and ending vertex from the graph
            Graph.Vertex start_vertex = Graph.Vertex.find(start_in_string);
            Graph.Vertex end_vertex = Graph.Vertex.find(end_in_string);

            // Store the output into the GraphInList record
            GraphInList list_result = new GraphInList(graph, start_vertex, end_vertex);
            adjacency_list_array.add(list_result);

            // Lastly, convert the graph into matrix format and store in an array
            GraphInMatrix matrix_result = new GraphInMatrix(graph.convertToMatrix(), graph.getSortedVertices(), start_vertex, end_vertex);
            adjacency_matrix_array.add(matrix_result);
        }
    }

    // A function to record the time taken by each graph using Bellman-Ford algorithm
    public static void bellmanRecord(long start, long end) {
        bellmanFordTime.add(end - start);
    }

    // A function to record the time taken by each graph using Floyd-Warshall algorithm
    public static void floydRecord(long start, long end) {
        floydWarshallTime.add(end - start);
    }
}
