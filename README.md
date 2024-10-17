# Project 6: Route

## Project Introduction

This project implements a routing service that represents the United States highway network as a graph and calculates routes and distances on this network. We first implement `GraphProcessor` which stores a graph representation and provides public methods to answer connectivity, distance, and pathfinding queries. Then we implement a `main` method in `GraphProcessor` that produces a minimal viable product (MVP) demonstrating the functionality of `GraphProcessor` and visualizing the results.

### `Point` Class

`Point.java` represents an immutable point on the Earth's surface. Each such point consists of a [latitude](https://en.wikipedia.org/wiki/Latitude), or north-south angle relative to the equator, and a [longitude](https://en.wikipedia.org/wiki/Longitude), or east-west angle relative to the prime meridian. We use the convention whereby **latitudes and longitudes are both measured in degrees between -180.0 and 180.0**, where positive latitudes are for north of the equator and negative latitudes are for south of the equator. Similarly, positive longitudes are for east of the prime meridian, and negative longitudes are for west of the equator. Vertices/nodes in the graph we will use to represent the United States highway system will be `Point` objects. 

<details><summary>Expand for details on Point methods</summary>

- `getLat` and `getLon` are getter methods for returning the values of the private latitude and longitude instance variables. 
- The `distance` method calculates the "straight-line" distance in US miles from one point to another. Latitudes and longitudes are *angles* and not x-y coordinates, so this calculation requires trigonometric projection onto a sphere.
- The `equals` method checks if two points have the same `latitude` and `longitude`.
- The `hashCode` method has been implemented to be consistent with `equals`, so that `Point` objects can be used in `HashSet`s or as keys in `HashMap`s.
- The `toString` directly prints Point objects.
- The `compareTo` method compares `Point` objects by latitude, then breaks ties by longitude. `Point implements Comparable<Point>`.

</details>

### `Visualize` Class

To create visualizations of the route(s) computed by your algorithms, we implement `Visualize.java` (which, in turn, uses `StdDraw.java`). 

<details><summary>Expand for details on Visualize and methods</summary>

The constructor to create a `Visualize` object has two parameters:
```
min_longitude max_longitude
min_latitude max latitude
width height
```
where the ranges correspond to the left, right, bottom, and top boundaries respectively of the image onto which the graph will be visualized, and the width and height are the number of pixels in the image to be visualized. `imageFile` is a `.png` image with dimensions matching those supplied in the `visFile`. Three such images files are supplied inside of the `images` folder, each of which has a corresponding `.vis` file. These images were taken from [Open Street Map](https://www.openstreetmap.org).

The `public` methods of `Visualize` are:
- `drawPoint` draws a single point on the image supplied.
- `drawEdge` draws an edge between two points on the `image` supplied.
- `drawGraph` takes a `List<Point>` and calls `drawPoint` on each, as well as a `List<Point[]>`, and attempts to call `drawEdge` on the index 0 and index 1 elements of each array in the latter list.
- `drawRoute` takes a `List<Point>` and draws each point in the list, connecting each subsequent two points by an edge.

</details>

### Graph Data

A graph consists of a number of vertices/nodes and the connections between them (known as edges). Our data represents highway networks, where vertices/nodes are points (see the [`Point` class](#the-point-class)) on the Earth's surface and the edges represent road segments. Our graph is **undirected**, meaning we assume every edge can be traversed in either direction. Our graph is also **weighted**, meaning the edges are not all of the same length. **The weight of an edge is the straight-line distance between its endpoints**, see [the `Point` class](#the-point-class) for the `distance` method.

The data we work with was originally pulled from the [METAL project by Dr. James D. Teresco](https://courses.teresco.org/metal/graph-formats.shtml). It has been slightly modified and stored as `.graph` files inside of the `data` folder. Three `.graph` files are supplied, the first two are small and intended for development, testing, and debugging, and the third is much larger and intended for use in the final demo. All three have corresponding `.vis` and `.png` files for use with `Visualize`.

1. `simple.graph` contains a small abstract graph (meaning not a real road network) with ten nodes and ten edges. A visualization is shown below at the left.

2. `durham.graph` contains a small but real-world graph, a subset of `usa.graph` that lies within the downtown Durham area. A visualization is shown below at the right. Note that now the graph is imposed on a real image of the road network of Durham instead of an abstract background.

<div align="center">
  <img width="300" src="images/simpleGraph.png">
  <img width="300" src="images/durhamGraph.png">
</div>

3. `usa.graph` contains over 85 thousand vertices and edges representing the (continental) United States Highway Network. This is the network on which we ultimately produce our demo.

<details><summary>Expand for details on the `.graph` file format</summary>

Each `.graph` file represents a graph in the following format:

```
num_vertices num_edges
node0_name node0_latitude node0_longitude
node1_name node1_latitude node1_longitude
...
index_u_edge0 index_v_edge0 optional_edge0_name
index_v_edge1 index_v_edge1 optional_edge1_name
...
```
In other words:
- The first line consists of the number of vertices and edges respectively, space separated.
- The next `num_vertices` lines describe one vertex/node per line, giving its name/label, then its latitude, then its longitude, all space separated.
- The next `num_edges` lines describe one edge per line, giving the index of its first endpoint and then the index of its second endpoint, space separated. These indices refer to the order in which the vertices/nodes appear in this file (0-indexed). For example, `0 1` would mean there is an edge between the first and second vertices listed above in the file. 
- There may or may not be an edge label/name after the indices for each edge; `simple.graph` and `durham.graph` do not include these labels, but `usa.graph` does, so you will need to be able to handle both cases.

</details>

## Part 1: Implementing `GraphProcessor`

We first implement `GraphProcessor`, which stores a graph representation and provides public methods to answer connectivity, distance, and pathfinding queries implementing 5 public methods.

### Instance variables

We add instance variables to `GraphProcessor` to represent a graph. Vertices/nodes in the graph are `Point` objects. Our graph representation allows to:
- Check if two vertices are adjacent (meaning there is an edge between them)
- For a given vertex, lookup/loop over all of its adjacent vertices.  

### Implement `initialize`

This method takes as input a `FileInputStream`. The method reads data from the file and create a representation of the graph, **stored in the instance variables** so that the graph representation is avaialble to subsequent method calls. If the file cannot be opened or does not have the correct format, the method throws an `Exception`, for example:
```java
throw new Exception("Could not read .graph file");
```

`initialize` should always be called first before any of the subsequent methods.

### Implement `nearestPoint`

We route between points that are not themselves vertices of the graph, in which case we need to be able to find the closest points on the graph. This method takes a `Point p` as input and returns the vertex in the graph that is closest to `p`, in terms of the straight-line distance calculated by the `distance` method of [the Point class](#the-point-class), NOT shortest path distance. Note that the input `p` may not be in the graph. If there are ties, we break them arbitrarily.

### Implement `routeDistance`

This method takes a `List<Point> route` representing a path in the graph as input and calculates the total distance along that path, starting at the first point and adding the distances from the first to the second point, the second to the third point, and so on. We use the `distance` method of [the `Point` class](#the-point-class). The runtime complexity of the method should be linear in `route.size()`, that is, the number of points on the path. 

### Implement `connected`

This method takes two points `p1` and `p2` and returns `true` if the points are connected, meaning there exists a path in the graph (a sequence of edges) from `p1` to `p2`. Otherwise, the method should return `false`, including if `p1` or `p2` are not themselves points in the graph. Instead of repeating this linear time search as part of `connected` itself, we search in the graph during `initialize` to store information about the connected components (subsets of the vertices that are connected to one another) so that we can answer `connected` queries in $`O(1)`$ time.

<details><summary>Optimizing the efficiency of connected</summary>

In this case, it is possible to run a single $`O(N+M)`$ search algorithm during `initialize` that stores information about the *connected components* of the graph, so that subsequent repeated calls to `connected` are much more efficient. A *connected component* is a subset of vertices that are all connected, meaning reachable from one another by paths. For undirected graphs, the question of whether two vertices are connected is equivalent to asking whether they are in the same connected component.

Two ideas for how to compute the connected components include:

1. One can use a graph search algorithm like depth-first search to explore the entire graph, one component at a time, and store a component label for each vertex that can be quickly looked up when later running `connected`.
2. One can use a Union-Find data structure for disjoint sets, initially with vertices all in their own set, and unioning sets together when there is at least one edge connecting them. Then `connected` just needs to perform two `find` operations to check if two vertices are in the same set.

</details>

### Implement `route`

This method takes two points, `start` and `end`, as input and should return a `List<Point>` representing the **shortest path** from `start` to `end` as a sequence of points. The total distance along a path is the sum of the edge weights, equal to the sum of the straight-line distance between consecutive points (see [implement `routeDistance`](#implement-routedistance)). Note that you must return the path itself, not just the distance along the path. The first point in our returned list should be `start`, and the last point should be `end`. 

If there is no path between `start` and `end`, either because the two points are not in the graph, or because they are the same point, or because they are not connected in the graph, then we throw an exception, for example: 
```java
throw new InvalidAlgorithmParameterException("No path between start and end");
```

This method searches in the graph itself, and takes into account the fact that the graph is weighted while searching for shortest paths. We adapted Dijkstra's algorithm to accomplish this: a breadth-first search (BFS) that uses a binary heap instead of a queue to keep track of which vertex to explore next. You used the `java.util` data structure `PriorityQueue` that implements a binary heap. This data structure does not support operations to change the priority of an element, so instead our implementation `add`s an element again any time a new shorter path is discovered, with the corresponding smaller distance. 

The runtime complexity of your implementation should be at most $`O(N+M) \log(N))`$ where $`N`$ is the number of vertices in the graph, $`M`$ is the number of edges in the graph, and we are assuming that each vertex is connected to at most a constant number of other vertices due to the way we use the `PriorityQueue`.

## Part 2: Creating `GraphDemo`

`GraphDemo` `main` method demonstration:

1. A user is able to indicate two cities in the United States, choosing two that are reasonably far apart (say, 1,000 miles or more) for the demo. They input the name of the cities.

<details><summary>Details on user input</summary>

An extensive list of latitude-longitude coordinates for US Cities has been included in `data/uscities.csv` (that the file is a `.csv` means each row contains an entry where the values are separated/delimited by commas `,`). This data was obtained from [simplemaps.com](https://simplemaps.com/data/us-cities) for educational use only. The example demo recording allowed the user to input their source and destination cities by typing them into the terminal.

</details>

2. For each of the user indicated points, the demo locates the closest vertex of the road network from `usa.graph`, the large data file containing the highway network of the USA.

3. The demo calculates a route (shortest path) between the two nearest vertices to the cities indicated by the user.

4. The demo indicates the total distance (in miles) of the route calculated.

5. The demo measures and report how long it took (include units) to calculate the closest points, shortest path, and distance along the path (steps 2-4).

6. The demo generates a visualization of the route calculated projected onto the map of the USA (see `images/usa.png` and `data/usa.vis`), using the [`Visualize` class](#the-visualize-class).

Coursework from Duke CS 201: Data Structures and Algorithms.
