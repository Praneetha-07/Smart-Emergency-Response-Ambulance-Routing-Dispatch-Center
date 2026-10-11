package org.example.dsa_project.dsa;

import java.util.List;

public record PathResult(List<String> path, double minutes, double distanceKm, int visitedNodes) {
    public PathResult {
        path = List.copyOf(path);
    }
    public boolean reachable() {
        return !path.isEmpty() && Double.isFinite(minutes);
    }
    public String pathText() {
        return String.join(" → ", path);
    }
}

