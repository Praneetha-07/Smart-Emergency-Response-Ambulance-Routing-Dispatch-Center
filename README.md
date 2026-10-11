
# Smart Emergency Response & Ambulance Routing System

A Java-based desktop simulation developed using **Java 23, JavaFX 23.0.2, and Maven** to demonstrate the application of Data Structures and Algorithms (DSA) in emergency response management and ambulance routing.

The system models a city as a weighted, bidirectional graph and simulates emergency requests, ambulance dispatch, hospital selection, road closures, and route recalculation through an interactive JavaFX dashboard.

## Features

- **City Graph Representation:** Models the city as a weighted, bidirectional graph using adjacency lists.
- **Dijkstra's Shortest Path:** Calculates shortest routes while accounting for blocked roads.
- **A* Search Entry Point:** Includes an A* entry point that currently uses a zero heuristic. Since geographic coordinates are not available, it behaves equivalently to Dijkstra's algorithm.
- **Custom Binary Max-Heap:** Prioritizes emergencies according to their assigned priorities.
- **HashMap-Based Registries:** Maintains ambulance, emergency, and hospital records for efficient lookup.
- **Intelligent Ambulance Dispatch:** Uses an eligibility-aware greedy strategy to assign suitable ambulances to emergencies.
- **Hospital Selection:** Checks hospital capacity and required facilities before selecting a destination.
- **Traffic and Road-Closure Simulation:** Supports road blocking, traffic updates, and route recalculation.
- **JavaFX Dashboard:** Provides an interactive interface with ambulance, hospital, and emergency tables.
- **Canvas-Based City Map:** Displays the simulated city and routing information.
- **Event Log:** Tracks important events and dispatch-related activities.

## Technologies Used

- **Programming Language:** Java 23
- **UI Framework:** JavaFX 23.0.2
- **Build Tool:** Maven
- **Data Structures:** Adjacency Lists, HashMaps, Custom Binary Max-Heap
- **Algorithms:** Dijkstra's Shortest Path, A* Search Entry Point, Greedy Assignment
- **Development Environment:** IntelliJ IDEA

## Data Structures and Algorithms

| Data Structure / Algorithm | Application |
|---|---|
| Adjacency List | Represents the city road network |
| Dijkstra's Algorithm | Calculates shortest available routes |
| A* Search | Provides an extensible pathfinding entry point |
| Custom Binary Max-Heap | Prioritizes emergency requests |
| HashMap | Stores and retrieves ambulance, emergency, and hospital records |
| Greedy Algorithm | Selects an eligible ambulance for an emergency |

## DSA Time Complexity

Let:
- `V` = number of vertices in the city graph
- `E` = number of edges in the city graph
- `A` = number of ambulances

| Operation | Time Complexity |
|---|---|
| HashMap lookup | Average O(1) |
| Binary heap insertion | O(log n) |
| Binary heap removal | O(log n) |
| Binary heap peek | O(1) |
| Dijkstra's algorithm using an adjacency list and binary min-heap | O((V + E) log V) |
| Greedy ambulance assignment | Approximately O(A × (V + E) log V) per request in the worst case when each eligible ambulance's route is evaluated independently |

The actual running time may vary depending on the graph representation, heap implementation, number of eligible ambulances, and route calculation strategy.

## How to Run the Project in IntelliJ IDEA

### Prerequisites

- JDK 23
- IntelliJ IDEA
- Maven
- JavaFX dependencies configured in `pom.xml`

### Steps

1. Open IntelliJ IDEA.
2. Select **Open** and choose the `DSA_Project` folder.
3. Ensure that IntelliJ recognizes the project as a Maven project.
4. Set the Project SDK to **JDK 23**.
5. Set the Maven runner JDK to **JDK 23**.
6. Reload the Maven project to download and resolve dependencies.
7. Run `HelloApplication.java` using the configured JavaFX run configuration.

### Run Using the Terminal

If the project contains the Maven Wrapper, execute:

```bash
./mvnw clean javafx:run
```

Otherwise, execute:

```bash
mvn clean javafx:run
```

Run these commands from the project root directory containing `pom.xml`.

## Project Limitations

- This is an educational simulation and is not intended for real-world emergency dispatch or medical decision-making.
- Road travel times, equipment requirements, and hospital capacities are synthetic demonstration data.
- The A* search entry point currently uses a zero heuristic because the demonstration graph has no geographic coordinates. A suitable heuristic can be added when valid geographic or other admissible distance information becomes available.
- Application data is stored in memory and is not persisted between application sessions.
- The application follows an explicit restart-to-reset workflow.

## Future Enhancements

- Integrate real geographic coordinates and an appropriate A* heuristic.
- Persist ambulance, emergency, and hospital data in a database.
- Incorporate realistic traffic and road network data.
- Improve ambulance assignment using additional optimization strategies.
- Add reporting and analytics for simulated emergency response performance.

## Learning Outcomes

This project demonstrates how fundamental data structures and algorithms can be combined in a practical desktop simulation. It provides hands-on experience with graph traversal and shortest-path algorithms, priority queues, hash-based registries, greedy assignment, event-driven UI development, and Maven-based Java application development.

## Disclaimer

This application is intended solely for academic and demonstration purposes. It must not be used to make real emergency response, ambulance dispatch, hospital selection, or medical decisions.


