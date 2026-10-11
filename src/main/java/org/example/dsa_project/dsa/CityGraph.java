package org.example.dsa_project.dsa;
import org.example.dsa_project.model.Road;
import java.util.*;

public class CityGraph {

        private final Map<String, List<Road>> adjacency = new LinkedHashMap<>();
        public void addNode(String node) {
            adjacency.computeIfAbsent(node, k -> new ArrayList<>());
        }
        public void addRoad(String a, String b, double km, double minutes) {
            addNode(a); addNode(b); adjacency.get(a).add(new Road(a,b,km,minutes)); adjacency.get(b).add(new Road(b,a,km,minutes));
        }
        public Set<String> getNodes() {
            return Collections.unmodifiableSet(adjacency.keySet());
        }
        public List<Road> getRoadsFrom(String node) {
            return Collections.unmodifiableList(adjacency.getOrDefault(node, List.of()));
        }
        public List<Road> getAllRoads() {
            List<Road> all = new ArrayList<>(); adjacency.values().forEach(all::addAll);
            return all;
        }
        public Road findRoad(String from, String to) {
            return adjacency.getOrDefault(from, List.of()).stream().filter(r -> r.getTo().equals(to)).findFirst().orElse(null);
        }
        public void setRoadBlocked(String a, String b, boolean blocked) {
            Road ab = findRoad(a,b), ba = findRoad(b,a); if (ab == null || ba == null) throw new IllegalArgumentException("Road not found: " + a + " - " + b);
            ab.setBlocked(blocked); ba.setBlocked(blocked);
        }
        public void setRoadTraffic(String a, String b, double multiplier) {
            Road ab = findRoad(a,b), ba = findRoad(b,a); if (ab == null || ba == null) throw new IllegalArgumentException("Road not found: " + a + " - " + b);
            ab.setTravelMinutes(ab.getTravelMinutes() * multiplier); ba.setTravelMinutes(ba.getTravelMinutes() * multiplier);
        }
        public static org.example.dsa_project.dsa.CityGraph demoCity() {
            org.example.dsa_project.dsa.CityGraph g = new org.example.dsa_project.dsa.CityGraph();
            // Bidirectional road network; edge weights are simulated travel minutes.
            g.addRoad("Central", "Market", 2.0, 4);
            g.addRoad("Central", "College", 2.5, 5);
            g.addRoad("Market", "BusStand", 1.5, 3);
            g.addRoad("Market", "EastPark", 2.0, 4);
            g.addRoad("College", "EastPark", 1.2, 3);
            g.addRoad("College", "WestGate", 2.0, 4);
            g.addRoad("WestGate", "BusStand", 3.0, 6);
            g.addRoad("BusStand", "EastPark", 1.0, 2);
            g.addRoad("EastPark", "RiverRoad", 2.0, 4);
            g.addRoad("WestGate", "RiverRoad", 2.5, 5);
            g.addRoad("RiverRoad", "CityHospital", 2.0, 4);
            g.addRoad("EastPark", "CityHospital", 4.0, 8);
            g.addRoad("BusStand", "TraumaCenter", 4.0, 7);
            g.addRoad("RiverRoad", "TraumaCenter", 2.0, 4);
            g.addRoad("Central", "NorthJunction", 1.0, 2);
            g.addRoad("NorthJunction", "TraumaCenter", 5.0, 8);
            return g;
        }
}

