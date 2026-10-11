package org.example.dsa_project.service;

import org.example.dsa_project.dsa.CityGraph;
import org.example.dsa_project.dsa.EmergencyMaxHeap;
import org.example.dsa_project.dsa.PathResult;
import org.example.dsa_project.dsa.RouteFinder;
import org.example.dsa_project.model.Ambulance;
import org.example.dsa_project.model.Emergency;
import org.example.dsa_project.model.Hospital;
import org.example.dsa_project.model.Road;

import java.util.*;

public class DispatchService {
    public record DispatchResult(boolean success, String message, Emergency emergency, Ambulance ambulance, Hospital hospital, PathResult route) {}
    private final CityGraph graph;
    private final Map<String, Ambulance> ambulances = new LinkedHashMap<>();
    private final Map<String, Emergency> emergencies = new LinkedHashMap<>();
    private final Map<String, Hospital> hospitals = new LinkedHashMap<>();
    private final EmergencyMaxHeap waiting = new EmergencyMaxHeap();
    private final List<String> history = new ArrayList<>();

    public DispatchService(CityGraph graph, Collection<Ambulance> ambulances, Collection<Hospital> hospitals) {
        this.graph=graph;
        ambulances.forEach(a -> this.ambulances.put(a.getId(),a));
        hospitals.forEach(h -> this.hospitals.put(h.getName(),h));
    }
    public CityGraph getGraph() { return graph; }
    public Collection<Ambulance> getAmbulances() {
        return Collections.unmodifiableCollection(ambulances.values());
    }
    public Collection<Emergency> getEmergencies() {
        return Collections.unmodifiableCollection(emergencies.values());
    }
    public Collection<Hospital> getHospitals() {
        return Collections.unmodifiableCollection(hospitals.values());
    }
    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }
    public EmergencyMaxHeap getWaitingHeap() {
        return waiting;
    }
    public void addEmergency(Emergency e) {
        emergencies.put(e.getId(),e);
        waiting.add(e);
        history.add("Reported " + e.getId() + " — " + e.getSeverity() + " at " + e.getLocation());
    }

    public DispatchResult dispatchNext() {
        Emergency e = waiting.poll();
        while (e != null && !e.getStatus().equals("WAITING")) e = waiting.poll();
        if (e == null)
            return new DispatchResult(false,"No waiting emergencies.",null,null,null,null);
        Ambulance bestAmbulance=null;
        PathResult bestToIncident=null;
        double bestCost=Double.POSITIVE_INFINITY;
        for (Ambulance a : ambulances.values()) {
            if (a.getStatus()!=Ambulance.Status.AVAILABLE)
                continue;
            if (e.needsAdvancedEquipment() && !a.hasAdvancedEquipment())
                continue;
            PathResult p= RouteFinder.dijkstra(graph,a.getLocation(),e.getLocation());
            if (!p.reachable())
                continue;
            double cost=p.minutes();
            if(cost<bestCost){
                bestCost=cost;bestAmbulance=a;bestToIncident=p;
            }
        }
        if(bestAmbulance==null){
            waiting.add(e);
            return new DispatchResult(false,"No eligible available ambulance can reach this emergency.",e,null,null,null);
        }
        Hospital bestHospital=null;
        PathResult bestHospitalRoute=null;
        double hospitalCost=Double.POSITIVE_INFINITY;
        for(Hospital h:hospitals.values()){
            if(h.getAvailableBeds()<=0)
                continue;
            if(e.getSeverity()==Emergency.Severity.CRITICAL && !h.hasFacility("TRAUMA"))
                continue;
            PathResult p=RouteFinder.dijkstra(graph,e.getLocation(),h.getLocation());
            if(!p.reachable())
                continue;
            if(p.minutes()<hospitalCost){
                hospitalCost=p.minutes();
                bestHospital=h;
                bestHospitalRoute=p;
            }
        }
        if(bestHospital==null){
            waiting.add(e);
            return new DispatchResult(false,"No reachable hospital with required capacity/facility.",e,null,null,null);
        }
        bestAmbulance.setStatus(Ambulance.Status.DISPATCHED);
        bestAmbulance.setAssignedEmergency(e.getId());
        bestAmbulance.setDestinationHospital(bestHospital.getName());
        e.setStatus("DISPATCHED");
        e.setAssignedAmbulance(bestAmbulance.getId());
        e.setAssignedHospital(bestHospital.getName());
        e.setEtaMinutes(bestToIncident.minutes());
        e.setHospitalEtaMinutes(bestHospitalRoute.minutes());
        e.setRoute(bestToIncident.pathText());
        history.add("Dispatched " + bestAmbulance.getId() + " to " + e.getId() + " | ETA " + String.format(Locale.US,"%.1f",bestToIncident.minutes()) + " min | Hospital: " + bestHospital.getName());
        return new DispatchResult(true,"Dispatch successful. Estimated arrival: "+String.format(Locale.US,"%.1f",bestToIncident.minutes())+" minutes.",e,bestAmbulance,bestHospital,bestToIncident);
    }
    public void completeEmergency(String emergencyId) {
        Emergency e=emergencies.get(emergencyId);
        if(e==null || !e.getStatus().equals("DISPATCHED"))
            return;
        Ambulance a=ambulances.get(e.getAssignedAmbulance());
        if(a!=null){
            a.setStatus(Ambulance.Status.AVAILABLE);
            a.setLocation(e.getLocation());
            a.setAssignedEmergency("—");
            a.setDestinationHospital("—");
        }
        Hospital h=hospitals.get(e.getAssignedHospital());
        if(h!=null)
            h.setAvailableBeds(h.getAvailableBeds()-1);
        e.setStatus("COMPLETED");
        history.add("Completed " + emergencyId + "; ambulance returned to available pool.");
    }
    public void toggleRoad(String a,String b) {
        Road r=graph.findRoad(a,b);
        if(r==null)
            throw new IllegalArgumentException("Road not found");
        graph.setRoadBlocked(a,b,!r.isBlocked());
        history.add("Road " + a + " ↔ " + b + (r.isBlocked()?" reopened":" blocked"));
    }
    public void increaseTraffic(String a,String b) {
        Road r=graph.findRoad(a,b);
        if(r==null)
            throw new IllegalArgumentException("Road not found");
        double current=r.getTravelMinutes();
        double base=Math.max(1,current/1.5);
        graph.setRoadTraffic(a,b,Math.max(1.1,1.5));
        history.add("Traffic increased on " + a + " ↔ " + b);
    }
    public void recalculateRoutes() {
        for (Emergency e : emergencies.values()) {

            // Recalculate routes only for active emergencies
            if (!e.getStatus().equals("DISPATCHED")) {
                continue;
            }

            Ambulance a = ambulances.get(e.getAssignedAmbulance());

            if (a == null) {
                continue;
            }

            // 1. Recalculate ambulance -> incident route
            PathResult responseRoute = RouteFinder.dijkstra(
                    graph,
                    a.getLocation(),
                    e.getLocation()
            );

            if (responseRoute.reachable()) {
                e.setRoute(responseRoute.pathText());
                e.setEtaMinutes(responseRoute.minutes());

                history.add(
                        "Updated response ETA for "
                                + e.getId()
                                + ": "
                                + String.format(
                                Locale.US, "%.1f",
                                responseRoute.minutes()
                        )
                                + " min"
                );
            } else {
                history.add(
                        "WARNING: No route to incident for "
                                + e.getId()
                );
            }

            // 2. Recalculate incident -> assigned hospital route
            Hospital hospital = hospitals.get(e.getAssignedHospital());

            if (hospital == null) {
                continue;
            }

            PathResult hospitalRoute = RouteFinder.dijkstra(
                    graph,
                    e.getLocation(),
                    hospital.getLocation()
            );

            if (hospitalRoute.reachable()) {
                e.setHospitalEtaMinutes(hospitalRoute.minutes());

                history.add("Updated hospital ETA for " + e.getId() + ": " + String.format(Locale.US, "%.1f", hospitalRoute.minutes()) + " min to " + hospital.getName());
            } else {
                history.add(
                        "WARNING: No route to hospital "
                                + hospital.getName()
                                + " for "
                                + e.getId()
                );
            }
        }
    }
}
