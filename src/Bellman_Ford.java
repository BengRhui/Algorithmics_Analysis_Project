import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Bellman_Ford {

	// Bellman-Ford is to find the shortest path with early stop when no edges relax
	public static String getShortestPath(Graph graph, Graph.Vertex start, Graph.Vertex destination) {
		return getShortestPath(graph, start, destination, false);
	}

	// Overload with a invariant flag for invariant testing
	public static String getShortestPath(Graph graph, Graph.Vertex start, Graph.Vertex destination, boolean invariant) {

		if (graph == null || start == null || destination == null) {
			return "Error. Invalid input.";
		}

		// First, we sort the vertices and get the number of vertices
		ArrayList<Graph.Vertex> vertices = graph.getSortedVertices();
		int verticesCount = vertices.size();

		// Check if start vertex and destination are the same - return 0 if same
		if (start.equals(destination)) {
			return "Distance from " + start + " to " + destination + " is: 0";
		}

		// Map vertices to indices for array-based storage
		Map<Graph.Vertex, Integer> indexOf = new HashMap<>();
		for (int i = 0; i < verticesCount; i++) {
			indexOf.put(vertices.get(i), i);
		}

		// Get indices of start and destination vertices
		Integer startIdx = indexOf.get(start);
		Integer endIdx = indexOf.get(destination);

		// Return error if no index is found
		if (startIdx == null || endIdx == null) {
			return "Error. No path is found.";
		}

		// Initialize distance array - this records the distance from the starting node
		double[] dist = new double[verticesCount];
		Arrays.fill(dist, Double.POSITIVE_INFINITY);

		// Initialize the predecessor array - this records the intermediary node that yields a shorter path
		int[] prev = new int[verticesCount];
		Arrays.fill(prev, -1);

		// The distance from the starting node to itself is 0
		dist[startIdx] = 0.0;

		// Duplicate index-based edges into an array for processing
		ArrayList<EdgeIndex> edges = new ArrayList<>();
		for (Graph.Edge edge : graph.getEdges()) {

			// Get the index of the start node and end node
			Integer startNode = indexOf.get(edge.startNode);
			Integer endNode = indexOf.get(edge.endNode);

			// Create an edge index object
			if (startNode != null && endNode != null) {
				edges.add(new EdgeIndex(startNode, endNode, edge.weight));
			}
		}

		// Algorithm starts here:
		// Relax edges V (number of edges) - 1 times
		/*
		 Check dist[u] is known, then if the new path from u to v is shorter than the previously known path to v,
		 Update dist[v] and prev[v].
		 If no, do nothing.

		 Keep repeat until all vertices are processed.
		 If in one iteration, no update is made, it means all shortest paths are found, can exit early.
		 */

		// Record time
		long startTime = System.nanoTime();

		// Loop through each vertex
		for (int i = 0; i < verticesCount - 1; i++) {

			double[] prevDist = invariant ? Arrays.copyOf(dist, dist.length) : null;

			// Check all edges
			for (EdgeIndex edge : edges) {

				// Invariant: edge indices must always be valid
				if (edge.startIndex < 0 || edge.startIndex >= verticesCount ||
						edge.endIndex < 0 || edge.endIndex >= verticesCount) {
					return "Error. Algorithm invariant violated.";
				}

				// If infinity, this indicates that the starting node is not reachable, hence skipped
				if (dist[edge.startIndex] == Double.POSITIVE_INFINITY) continue;

				// If not infinity (reachable), we calculate the new distance first
				double newDist = dist[edge.startIndex] + edge.weight;

				// Then we check if the path is shorter than the previously known path to destination
				if (newDist < dist[edge.endIndex]) {

					// If yes, update new distance and predecessor
					dist[edge.endIndex] = newDist;
					prev[edge.endIndex] = edge.startIndex;
				}
			}

			// Invariant: relaxation consistency check
			if (invariant) {
				for (EdgeIndex e : edges) {
					if (dist[e.startIndex] != Double.POSITIVE_INFINITY) {
						if (dist[e.endIndex] > dist[e.startIndex] + e.weight) {
							System.out.println("Invariant violated at edge: " + e);
						}
					}
				}
			}

			// Invariant: distances should never increase after relaxation
			if (invariant) {
				for (int j = 0; j < dist.length; j++) {
					if (dist[j] > prevDist[j]) {
						System.out.println("Invariant violated: distance increased!");
					}
				}
			}

			// Invariant: if a node is unreachable in the previous iteration, its distance should remain ∞
			if (invariant) {
				for (int j = 0; j < dist.length; j++) {
					if (prev[j] == -1 && j != startIdx && dist[j] != Double.POSITIVE_INFINITY) {
						System.out.println("Invariant warning: unexpected reachable node");
					}
				}
			}

		}

		// Record time
		Main.bellmanRecord(startTime, System.nanoTime());

		// --- Negative cycle detection ---

		// After all iterations, if we can still relax any edge, it means there is a negative cycle reachable from the start vertex, e.g., [A, B, -1]
		// If there is a negative cycle, the shortest path is invalid as it can keep going through the negative edge to reduce the distance indefinitely.

		// Note: Negative paths are allowed, as long as it does not lead to infinite loops due to negative cycles, hence checking at the end
		for (EdgeIndex e : edges) {

			// No issues with unreachable edges
			if (dist[e.startIndex] == Double.POSITIVE_INFINITY) continue;

			// If the distance can still be reduced, negative cycle exists, hence the path is invalid
			if (dist[e.startIndex] + e.weight < dist[e.endIndex]) {
				return "Negative cycle found from vertex " + start + ". This path is invalid.";
			}
		}

		// Algorithm ends here.

		// Get the shortest distance from start to end
		double shortest = dist[endIdx];

		// If the destination is unreachable, display distance not found
		if (shortest == Double.POSITIVE_INFINITY) {
			return "Distance from " + start + " to " + destination + " is: Not found";
		}

		// Construct the shortest path by backtracking from destination
		ArrayList<String> path = new ArrayList<>();

		// Beginning from end index, we track the predecessor array
		int current = endIdx;

		// If a path does not exist, the predecessor array has value -1
		while (current != -1) {

			if (current < 0 || current >= verticesCount) {
				return "Error. Algorithm invariant violated.";
			}

			// Add the vertex to the front of the array
			path.addFirst(vertices.get(current).label());

			// End the search process if the starting vertex is found
			if (current == startIdx) break;

			// If not, continue the process with the found node
			current = prev[current];
		}

		// Check for two conditions: the path is empty, or the path does not start with the starting label
		if (path.isEmpty() || !path.getFirst().equals(start.label())) {
			return "Error. No path is found.";
		}

		// If everything is fine, return the shortest distance and path
		return "Distance from " + start + " to " + destination + " is: " + shortest +
				"\nThe shortest path is: " + String.join(" -> ", path);
	}

	// Minimal edge holder for index-based relaxation
	private record EdgeIndex(int startIndex, int endIndex, double weight) {}
}
