import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
public class GraphDemo {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        GraphProcessor gp = new GraphProcessor();
        gp.initialize(new FileInputStream("data/usa.graph"));

        // Input 2 Cities
        System.out.println("Enter your starting city and state:");
        String startCity = input.nextLine();
        double[] startLL = cityToLatLon(new FileInputStream("data/uscities.csv"), startCity);
        Point startCityPoint = new Point(startLL[0], startLL[1]);

        // System.out.println("Enter your starting city's latitude:");
        // Double startLat = Double.valueOf(input.nextLine());
        // System.out.println("Enter your starting city's longitude:");
        // Double startLon = Double.valueOf(input.nextLine());

        System.out.println("Enter your ending city and state:");
        String endCity = input.nextLine();
        double[] endLL = cityToLatLon(new FileInputStream("data/uscities.csv"), endCity);
        Point endCityPoint = new Point(endLL[0], endLL[1]);
        
        // System.out.println("Enter your destination city's latitude:");
        // Double endLat = Double.valueOf(input.nextLine());
        // System.out.println("Enter your destination city's longitude:");
        // Double endLon = Double.valueOf(input.nextLine());
        // Point endCityPoint = new Point(endLat, endLon);

        // Shortest Distance & Time
        long startTime = System.nanoTime();

        Point startClose = gp.nearestPoint(startCityPoint);
        Point endClose = gp.nearestPoint(endCityPoint);
        List<Point> route = gp.route(startClose, endClose);
        double distance = gp.routeDistance(route);
        
        long endTime = System.nanoTime();
        System.out.println("Nearest point to " + startCity +" is ("+ startCityPoint.getLat() +", "+startCityPoint.getLon()+").");
        System.out.println("Nearest point to " + endCity +" is ("+ endCityPoint.getLat() +", "+endCityPoint.getLon()+").");

        System.out.println("The shortest route between "+ startCity +" and "+ endCity+ " is " + distance + " miles");
        System.out.println("The time taken to calculate the shortest route is " + (endTime - startTime)/1000000 + "ms");

        // Visualize
        Visualize v = new Visualize("data/usa.vis", "images/usa.png");
        v.drawPoint(startCityPoint);
        v.drawPoint(endCityPoint);
        v.drawRoute(route);

        input.close();
    }

    public static double[] cityToLatLon(FileInputStream file, String city) throws Exception {
        double[] latlon = new double[2];
        Scanner input = new Scanner(file);

        while (input.hasNextLine()) {
            String[] citystate = input.nextLine().split(",");
            if (citystate[0].equals(city.split(" ")[0]) && citystate[1].equals(city.split(" ")[1])) {
                latlon[0] = Double.parseDouble(citystate[2]);
                latlon[1] = Double.parseDouble(citystate[3]);
                input.close();
                return latlon;
            }
        }
        input.close();
        throw new Exception("Entered city does not exist.");
    }
}
