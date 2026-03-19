import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    // A record to store each graph
    private record GraphInList(Graph graph, Graph.Vertex start, Graph.Vertex end) {}
    private record GraphInMatrix(double[][] matrix, ArrayList<Graph.Vertex> vertexList, Graph.Vertex start, Graph.Vertex end) {}
    private static final ArrayList<GraphInList> adjacency_list_array = new ArrayList<>();
    private static final ArrayList<GraphInMatrix> adjacency_matrix_array = new ArrayList<>();

    // Main function here
    public static void main(String[] args) {

        // A sample data for testing
        String example_input = """
                [(A, B, 2); (B, D, 1); (A, D, 4); (D, C, 3)] | A | C
                [(A, B, 10); (B, C, 6); (C, D, 4); (D, E, 8); (E, A, 10)] | C | E
                """;

        // Convert the string data into graph and vertex objects
        preprocessing_steps(example_input);

        // Output the results here - list
        System.out.println("Adjacency list:");
        for (GraphInList graph_in_list : adjacency_list_array) {
            graph_in_list.graph.printGraph();
            System.out.println("Start node: " + graph_in_list.start + ", End node: " + graph_in_list.end);
            System.out.println();
        }

        // Print matrix here
        System.out.println("Adjacency matrix:");
        for (GraphInMatrix graph_in_matrix : adjacency_matrix_array) {

            // Print the header row
            System.out.println(Arrays.toString(graph_in_matrix.vertexList.toArray()));

            // Print the elements
            for (double[] row: graph_in_matrix.matrix) {
                for (double element: row) {
                    System.out.printf("%-10s, ", element);
                }
                System.out.println();
            }

            // And print the starting and ending node
            System.out.println("Start node: " + graph_in_matrix.start + ", End node: " + graph_in_matrix.end);
            System.out.println();
        }
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
}