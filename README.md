**Smart Emergency Response & Ambulance Routing System (DSA Project)**
Java 23 + JavaFX 23.0.2 + Maven desktop simulation.

**Features**
Weighted, bidirectional city graph using adjacency lists
Dijkstra shortest path with blocked-road handling
A* entry point (currently uses a zero heuristic because the demo graph has no geographic coordinates; it is equivalent to Dijkstra until a valid heuristic is added)
Custom binary max-heap for emergency priority
HashMap-backed ambulance/emergency/hospital registries
Eligibility-aware greedy ambulance dispatch
Hospital capacity/facility checks
Traffic/road-closure simulation and route recalculation
JavaFX dashboard, fleet/hospital/emergency tables, event log and canvas map

**Run in IntelliJ**
Open the DSA_Project folder as a Maven project.
Set Project SDK and Maven runner JDK to JDK 23.
Reload Maven dependencies.
Run HelloApplication.java, or run ./mvnw clean javafx:run if your project contains the Maven wrapper. Otherwise run mvn clean javafx:run from the project folder.

**Important limitations**
This is an educational simulation, not a real emergency dispatch tool. Road travel times, equipment requirements and hospital capacities are synthetic. Dispatch policies must not be used for real medical decisions. The current UI has an explicit restart-to-reset workflow; data is in memory and is not persisted.

**DSA complexity notes**
HashMap lookup: average O(1)
Custom binary heap insertion/removal: O(log n); peek O(1)
Dijkstra with adjacency list and binary min-heap: O((V + E) log V)
Greedy ambulance assignment: roughly O(A * (V + E) log V) per request because each eligible ambulance's route is evaluated independently
