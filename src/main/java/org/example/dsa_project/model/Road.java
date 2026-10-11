package org.example.dsa_project.model;

public class Road {
    private final String from;
    private final String to;
    private final double distanceKm;
    private double travelMinutes;
    private boolean blocked;

    public Road(String from, String to, double distanceKm, double travelMinutes) {
        this.from = from;
        this.to = to;
        this.distanceKm = distanceKm;
        this.travelMinutes = travelMinutes;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public double getDistanceKm() {
        return distanceKm;
    }
    public double getTravelMinutes() {
        return travelMinutes;
    }
    public void setTravelMinutes(double travelMinutes) {
        this.travelMinutes = Math.max(0.1, travelMinutes);
    }
    public boolean isBlocked() {
        return blocked;
    }
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
