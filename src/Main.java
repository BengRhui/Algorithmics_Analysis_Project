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
