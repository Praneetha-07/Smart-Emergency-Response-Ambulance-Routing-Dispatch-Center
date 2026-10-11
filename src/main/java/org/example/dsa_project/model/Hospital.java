package org.example.dsa_project.model;

import java.util.Set;

public class Hospital {
    private final String name;
    private final String location;
    private int availableBeds;
    private final Set<String> facilities;
    public Hospital(String name, String location, int availableBeds, Set<String> facilities) {
        this.name = name;
        this.location = location;
        this.availableBeds = availableBeds;
        this.facilities = Set.copyOf(facilities);
    }
    public String getName() {
        return name;
    }
    public String getLocation() {
        return location;
    }
    public int getAvailableBeds() {
        return availableBeds;
    }
    public void setAvailableBeds(int beds) {
        availableBeds = Math.max(0, beds);
    }
    public boolean hasFacility(String facility) {
        return facilities.contains(facility);
    }
    public Set<String> getFacilities() {
        return facilities;
    }
}
