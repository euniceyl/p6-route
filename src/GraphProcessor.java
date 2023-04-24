import java.security.InvalidAlgorithmParameterException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 * @author Elise Nackley
 * @author Eunice Lee
 *
 */
public class GraphProcessor {

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */
    //instance variables
    private static HashMap<Point, Set<Point>> aList = new HashMap<>(); 
    public static Map<Point, Integer> pointLabels = new HashMap<>();
    
    public void initialize(FileInputStream file) throws Exception {
        // TODO: Implement initialize
        try{
            Scanner reader = new Scanner(file);
            String[] lonBounds = reader.nextLine().split(" ");
            int num_vertices = Integer.parseInt(lonBounds[0]);
            int num_edges = Integer.parseInt(lonBounds[1]);
            Point[] verticesArray = new Point[num_vertices];

            for(int i = 0; i<num_vertices; i++ ){
                String[] info = reader.nextLine().split(" ");
                String name = info[0];
                Double lat = Double.parseDouble(info[1]);
                Double longitude =  Double.parseDouble(info[2]);
                verticesArray[i] = new Point(lat, longitude);

            }
            for(int j =0; j< num_edges; j++){
                String[] edgeInfo = reader.nextLine().split(" ");
                int index1 = Integer.parseInt(edgeInfo[0]);
                int index2 = Integer.parseInt(edgeInfo[1]);
                if(edgeInfo.length>2){
                    String nameEdge = edgeInfo[2];
                }
                if(!aList.containsKey(verticesArray[index1])){
                    aList.put(verticesArray[index1], new HashSet<Point>());

                }
                if(!aList.containsKey(verticesArray[index2])){
                    aList.put(verticesArray[index2], new HashSet<Point>());
                }
                aList.get(verticesArray[index1]).add(verticesArray[index2]);
                aList.get(verticesArray[index2]).add(verticesArray[index1]);
            }
            file.close();
            reader.close();
        } catch (IOException e){
            throw new Exception("Could not read .graph file");

        }

        Set<Point> visited = new HashSet<>();
        Queue<Point> toExplore = new LinkedList<>();
        int count = 0;

        for (Point p : aList.keySet()) {
            if (!pointLabels.keySet().contains(p)) {
                toExplore.add(p);
                while(!toExplore.isEmpty()) {
                    Point current = toExplore.remove();
                    pointLabels.put(p, count);
                    for(Point neighborPoint:aList.get(current)) {
                        if (!visited.contains(neighborPoint)) {
                            visited.add(neighborPoint);
                            toExplore.add(neighborPoint);
                        }
                    }
                }
                count = count + 1;
            }
        }
    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        Point closest = null;
        Double smallest = null;
        for(Point other : aList.keySet()){
            if(smallest == null || p.distance(other)< smallest){
                smallest = p.distance(other);
                closest = other;
            }
        }
        return closest;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        double sum = 0.0;
        for(int i = 1; i<route.size();i++){
            sum += route.get(i-1).distance(route.get(i));
        }
        return sum;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        return (pointLabels.get(p1) == pointLabels.get(p2));
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        
        if(!connected(start, end) || start.equals(end)){
            throw new InvalidAlgorithmParameterException("No path between start and end");

        }

        Map<Point, Double> distance = new HashMap<>();
        distance.put(start, 0.0);
        Comparator<Point> comp = (a, b) -> distance.get(a).compareTo(distance.get(b));
        PriorityQueue<Point> toExplore = new PriorityQueue<>(comp);
        Map<Point, Point> previous = new HashMap<>();
        toExplore.add(start);
        while (toExplore.size() > 0) {
            Point current = toExplore.remove();
            for (Point neighbor : aList.get(current)) {
                Double newDist = distance.get(current) + current.distance(neighbor);
                if (!distance.containsKey(neighbor) || newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    toExplore.add(neighbor);
                    previous.put(neighbor, current);
                }
            }
        }
       
        List<Point> finalList  = new ArrayList<>();
        Point end2 = end;
        finalList.add(end);
        while(!end2.equals(start)){
            finalList.add(previous.get(end2));
            end2 = previous.get(end2);
        }
        Collections.reverse(finalList);
        return finalList;


  

    
    }

    
}
