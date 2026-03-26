import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Bellman_Ford {

	// Bellman-Ford shortest path with early stop when no edges relax
	public static String getShortestPath(Graph graph, Graph.Vertex start, Graph.Vertex destination) {

		ArrayList<Graph.Vertex> vertices = graph.getSortedVertices();
		int verticesCount = vertices.size();

		// Check if start vertex and destination are the same
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
		if (startIdx == null || endIdx == null) {
			return "Error. No path is found.";
		}

		// Initialize distance and predecessor arrays
		// Set all distances to infinity as haven't reached yet
		double[] dist = new double[verticesCount];
		int[] prev = new int[verticesCount];

		Arrays.fill(dist, Double.POSITIVE_INFINITY);
		Arrays.fill(prev, -1);
		dist[startIdx] = 0.0;

		// Duplicate index-based edges into an array for processing
		ArrayList<EdgeIndex> edges = new ArrayList<>();
		for (Graph.Edge edge : graph.getEdges()) {
			Integer startNode = indexOf.get(edge.startNode);
			Integer endNode = indexOf.get(edge.endNode);
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
		for (int i = 0; i < verticesCount - 1; i++) {
//			boolean updated = false;

			for (EdgeIndex edge : edges) {
				if (dist[edge.u] == Double.POSITIVE_INFINITY) continue;

				// Check if the current path is shorter than the previously known path to destination
				double newDist = dist[edge.u] + edge.weight;
				if (newDist < dist[edge.v]) {
					// Update new distance and predecessor
					dist[edge.v] = newDist;
					prev[edge.v] = edge.u;
//					updated = true;
				}
			}

//			if (!updated) break; 		// If no updates, shortest paths are found, can exit early
		}

		// Negative cycle detection reachable from start
		/*
		After all iterations, if we can still relax any edge, it means there is a negative cycle reachable from the start vertex.
		such as [A, B, -1]
		If there is a negative cycle, the shortest path is invalid as can keep going through the negative edge to reduce the distance indefinitely.
		 */
		for (EdgeIndex e : edges) {
			if (dist[e.u] == Double.POSITIVE_INFINITY) continue;
			if (dist[e.u] + e.weight < dist[e.v]) {
				return "Negative cycle found from vertex " + start + ". This path is invalid.";
			}
		}

		// Algorithm ends here.

		double shortest = dist[endIdx];

		// Check if the destination is unreachable
		if (shortest == Double.POSITIVE_INFINITY) {
			return "Distance from " + start + " to " + destination + " is: Not found";
		}

		// Construct the shortest path by backtracking from destination
		ArrayList<String> path = new ArrayList<>();
		int current = endIdx;
		
		while (current != -1) {
			path.add(0, vertices.get(current).label());
			if (current == startIdx) break;
			current = prev[current];
		}

		if (path.isEmpty() || !path.get(0).equals(start.label())) {
			return "Error. No path is found.";
		}

		return "Distance from " + start + " to " + destination + " is: " + shortest +
				"\nThe shortest path is: " + String.join(" -> ", path);
	}

	// Minimal edge holder for index-based relaxation
	private record EdgeIndex(int u, int v, double weight) {}
}
