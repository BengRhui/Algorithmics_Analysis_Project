import java.util.ArrayList;

public class Floyd_Warshall {

    // The function implementing Floyd-Warshall algorithm
    public static String getShortestPath(double[][] distMatrix,
                                           ArrayList<Graph.Vertex> vertexList, 
                                           Graph.Vertex start, 
                                           Graph.Vertex end) {

        // --------------------- Small explanation on how the algorithm works ---------------------
        //
        // How the algorithm works is that - we work on the intermediate (middle) node:
        //
        // 1. We must have two matrices, one called distance matrix and the other called next matrix
        //    - Distance matrix records the shortest distance between two points (called "matrix" in this function)
        //    - Next matrix records the next vertex to visit
        //
        // 2. We create the next matrix first, and we label the next matrix with existing paths. For example,
        //
        //         A  B  C  D                                         A  B  C  D
        //     A | 0  3  ∞  7 |                                   A | -  B  -  D |
        //     B | 8  0  2  ∞ |    -- creating next matrix -->    B | A  -  C  - |
        //     C | 5  ∞  0  1 |                                   C | A  -  -  D |
        //     D | 2  ∞  ∞  0 |                                   D | A  -  -  - |
        //
        // 3. Now, we let 'A' to be the middle node.
        //    Excluding rows and columns with A and diagonal, we should check these few paths:
        //    - B -> A -> C =         8 + ∞  = ?    (Path doesn't exist, don't do anything)
        //    - B -> A -> D = 8 + 7 = 15     < ∞    (Shorter,            so need to update)  *
        //    - C -> A -> B = 5 + 3 = 8      < ∞    (Shorter,            so need to update)  *
        //    - C -> A -> D = 5 + 7 = 12     > 1    (Longer,             don't do anything)
        //    - D -> A -> B = 2 + 3 = 5      < ∞    (Shorter,            so need to update)  *
        //    - D -> A -> C =         2 + ∞  = ?    (Path doesn't exist, don't do anything)
        //
        //    So, the distance matrix shall be updated to the shortest path, and the next matrix shall be updated with the intermediary node
        //
        //         A  B  C  D                                          A  B  C  D
        //     A | 0  3  ∞  7  |                                   A | -  B  -  D |
        //     B | 8  0  2  15 |    -- updating next matrix -->    B | A  -  C  A |
        //     C | 5  8  0  1  |                                   C | A  A  -  D |
        //     D | 2  5  ∞  0  |                                   D | A  A  -  - |
        //
        // 4. Following the same process as step 3, we let 'B', 'C' and 'D' be the intermediate node, and the corresponding matrices should be:
        //
        //    - "B" as middle
        //
        //             A  B  C  D                                          A  B  C  D
        //         A | 0  3  5  7  |                                   A | -  B  B  D |
        //         B | 8  0  2  15 |    -- updating next matrix -->    B | A  -  C  A |
        //         C | 5  8  0  1  |                                   C | A  A  -  D |
        //         D | 2  5  7  0  |                                   D | A  A  B  - |
        //
        //    - "C" as middle
        //
        //             A  B  C  D                                          A  B  C  D
        //         A | 0  3  5  6  |                                   A | -  B  B  B |
        //         B | 7  0  2  3  |    -- updating next matrix -->    B | C  -  C  C |
        //         C | 5  8  0  1  |                                   C | A  A  -  D |
        //         D | 2  5  7  0  |                                   D | A  A  B  - |
        //
        //    - "D" as middle
        //
        //             A  B  C  D                                          A  B  C  D
        //         A | 0  3  5  6  |                                   A | -  B  B  B |
        //         B | 5  0  2  3  |    -- updating next matrix -->    B | C  -  C  C |
        //         C | 3  6  0  1  |                                   C | D  D  -  D |
        //         D | 2  5  7  0  |                                   D | A  A  B  - |
        //
        // 5. The final matrices show the shortest paths naturally.
        //    How this works is that the shortest path is found recursively.
        //    Let's say, to find C -> B,
        //        from the next matrix, we can know that C -> D -> B is shorter,
        //        and if we look at D -> B, we can know that D -> A -> B is shorter,
        //        so the shortest path will be C -> D -> A -> B.
        //
        // ----------------------------------------------------------------------------------------

        // First, determine the matrix size - O(1), directly retrieve from an internal field
        int matrixSize = vertexList.size();
        
        // Have to initialize the next matrix to track vertices
        Integer[][] nextMatrix = new Integer[matrixSize][matrixSize];

        // Note that we use index to represent the nodes (to allow computation)
        for (int row = 0; row < matrixSize; row++) {
            for (int col = 0; col < matrixSize; col++) {

                // Only update when a path exists (i.e., when not 0 and not infinity)
                if (row != col && distMatrix[row][col] < Double.POSITIVE_INFINITY) {
                    nextMatrix[row][col] = col;
                }
            }
        }

        // Record time
        long startTime = System.nanoTime();

        // Now loop through each intermediate node
        for (int middle = 0; middle < matrixSize; middle++) {

            // And we check each element in the distance matrix
            for (int row = 0; row < matrixSize; row++) {
                for (int col = 0; col < matrixSize; col++) {

                    // If (start to mid) + (mid to end) < (start to end), then update
                    if (distMatrix[row][middle] + distMatrix[middle][col] < distMatrix[row][col]) {

                        // Replace the shortest length in distance matrix
                        distMatrix[row][col] = distMatrix[row][middle] + distMatrix[middle][col];

                        // Update the next matrix with the node that is between (start to middle)
                        nextMatrix[row][col] = nextMatrix[row][middle];
                    }
                }
            }

            // Invariant testing - loop through each row
            for (int row = 0; row < matrixSize; row++) {

                // Check 1: Negative cycle check - negative value in distance matrix diagonal means negative cycle exists
                assert distMatrix[row][row] >= 0 : "Invariant Violated: Negative cycle detected at node " + row;

                // Loop through the elements in each row
                for (int col = 0; col < matrixSize; col++) {

                    // Check 2: Intermediate distance check
                    // Use if-statement to make sure that the path through intermediary node exists
                    if (distMatrix[row][middle] != Double.POSITIVE_INFINITY
                            && distMatrix[middle][col] != Double.POSITIVE_INFINITY) {

                        // Perform checking to see if a shorter distance can be found
                        assert distMatrix[row][col] <= distMatrix[row][middle] + distMatrix[middle][col] :
                                "Invariant Violated: Path through " + middle + " is shorter but not updated!";
                    }
                }
            }

            // Output that checking passes
            System.out.println("Invariant checking [Iteration " + (middle + 1) + "]: PASS");
        }
        System.out.println();

        // Algorithm ends here. Remaining is just to retrieve the shortest path (guess should not be included in complexity calculation)
        // Record time
        Main.floydRecord(startTime, System.nanoTime());

        // Find the indices corresponding to the start and end vertices
        int startNodeIndex = vertexList.indexOf(start);
        int endNodeIndex = vertexList.indexOf(end);

        // If both vertices are the same, return 0 (coz going from itself to itself costs nothing)
        if (startNodeIndex == endNodeIndex)
            return "Distance from " + start + " to " + end + " is: 0";
        
        // First, we check if any path exists. The weight is infinity if a path doesn't exist
        double shortestDistance = distMatrix[startNodeIndex][endNodeIndex];
        if (shortestDistance == Double.POSITIVE_INFINITY)
            return "Distance from " + start + " to " + end + " is: Not found";

        // Creating an array list to store the path
        ArrayList<String> path = new ArrayList<>();

        // Begin to store the first vertex
        path.add(start.label());

        // Now we try to traverse through the next matrix and search for the middle vertex
        int currentNodeIndex = startNodeIndex;

        // If the middle vertex is the last vertex, then the shortest path is found, and the loop ends
        while (currentNodeIndex != endNodeIndex) {

            // Retrieve the middle vertex first
            Integer middleVertex = nextMatrix[currentNodeIndex][endNodeIndex];

            // If the middle vertex is not found - should not be happening with distance matrix checked, but included in case
            if (middleVertex == null)
                return "Error. No path is found.";

            // Add the middle vertex into the path
            path.add(vertexList.get(middleVertex).label());

            // Then update the current vertex to the middle vertex
            currentNodeIndex = middleVertex;
        }
        
        // Finally, the path is returned
        return "Distance from " + start + " to " + end + " is: " + shortestDistance +
                "\nThe shortest path is: " + String.join(" -> ", path);
    }
}
