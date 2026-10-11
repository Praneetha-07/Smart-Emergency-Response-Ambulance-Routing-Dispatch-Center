package org.example.dsa_project.dsa;

import org.example.dsa_project.model.Road;
import java.util.*;

public class RouteFinder {
    private RouteFinder() {}
    private record State(String node, double cost) {}

    public static PathResult dijkstra(CityGraph graph, String start, String goal) {
        return shortestPath(graph, start, goal, false);
    }
    public static PathResult aStar(CityGraph graph, String start, String goal) {
        // The demo graph has no geographic coordinates; zero heuristic makes A* equivalent to Dijkstra.
        // Replace heuristic() with a valid lower-bound travel-time estimate when coordinates are added.
        return shortestPath(graph, start, goal, true);
    }
    private static PathResult shortestPath(CityGraph graph, String start, String goal, boolean aStar) {
        if (!graph.getNodes().contains(start) || !graph.getNodes().contains(goal))
            return new PathResult(List.of(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
        Map<String, Double> dist = new HashMap<>(), km = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> settled = new HashSet<>();
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingDouble(State::cost));
        dist.put(start, 0.0); km.put(start, 0.0);
        pq.add(new State(start, heuristic(start, goal, aStar)));
        int visited = 0;
        while (!pq.isEmpty()) {
            State state = pq.poll(); String u = state.node();
            if (!settled.add(u)) continue; visited++;
            if (u.equals(goal)) break;
            for (Road road : graph.getRoadsFrom(u)) {
                if (road.isBlocked()) continue;
                String v = road.getTo(); double candidate = dist.get(u) + road.getTravelMinutes();
                if (candidate < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    dist.put(v, candidate); km.put(v, km.getOrDefault(u, 0.0) + road.getDistanceKm()); previous.put(v, u);
                    pq.add(new State(v, candidate + heuristic(v, goal, aStar)));
                }
            }
        }
        if (!dist.containsKey(goal))
            return new PathResult(List.of(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, visited);
        LinkedList<String> path = new LinkedList<>();
        String current = goal;
        while (current != null) {
            path.addFirst(current);
            if (current.equals(start))
                break;
            current = previous.get(current);
        }
        if (!path.getFirst().equals(start))
            return new PathResult(List.of(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, visited);
        return new PathResult(path, dist.get(goal), km.get(goal), visited);
    }
    private static double heuristic(String node, String goal, boolean aStar) {
        return 0.0;
    }
}
