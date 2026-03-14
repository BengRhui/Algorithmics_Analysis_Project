import java.util.ArrayList;

public class Main {

    // A record to store each graph
    private record Result(Graph graph, Graph.Vertex start, Graph.Vertex end) {}
    private static final ArrayList<Result> result_array = new ArrayList<>();

    // Main function here
    public static void main(String[] args) {

        // A sample data for testing
        String example_input = """
                [(A, B, 2); (B, D, 1); (A, D, 4); (D, C, 3)] | A | C
                [(A, B, 10); (B, C, 6); (C, D, 4); (D, E, 8); (E, A, 10)] | C | E
                """;

        // Convert the string data into graph and vertex objects
        preprocessing_steps(example_input);

        // Output the results here
        for (Result result: result_array) {
            result.graph.printGraph();
            System.out.println("Start node: " + result.start + ", End node: " + result.end);
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

            // Store the output into the Result record
            Result result = new Result(graph, start_vertex, end_vertex);
            result_array.add(result);
        }
    }
}