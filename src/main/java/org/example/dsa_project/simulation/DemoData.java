package org.example.dsa_project.simulation;

import org.example.dsa_project.dsa.CityGraph;
import org.example.dsa_project.model.Ambulance;
import org.example.dsa_project.model.Emergency;
import org.example.dsa_project.model.Hospital;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DemoData {
    private DemoData() {}
    public static List<Ambulance> ambulances() { return new ArrayList<>(List.of(
            new Ambulance("AMB-101", "Central", false),
            new Ambulance("AMB-102", "WestGate", true),
            new Ambulance("AMB-103", "BusStand", true),
            new Ambulance("AMB-104", "College", false),
            new Ambulance("AMB-105", "NorthJunction", true)));
    }
    public static List<Hospital> hospitals() {
        return new ArrayList<>(List.of(
            new Hospital("City Hospital", "CityHospital", 8, Set.of("GENERAL", "TRAUMA")),
            new Hospital("Trauma Center", "TraumaCenter", 4, Set.of("GENERAL", "TRAUMA", "ADVANCED"))));
    }
    public static List<Emergency> emergencies() {
        return new ArrayList<>(List.of(
            new Emergency("EastPark", Emergency.Severity.HIGH, false),
            new Emergency("RiverRoad", Emergency.Severity.CRITICAL, true)));
    }
    public static CityGraph city() {
        return CityGraph.demoCity();
    }
}
