package org.example.dsa_project.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class Emergency {
    private static final AtomicInteger NEXT = new AtomicInteger(1001);
    public enum Severity {
        CRITICAL, HIGH, MODERATE, LOW
    }
    private final String id;
    private final String location;
    private final Severity severity;
    private final boolean needsAdvancedEquipment;
    private final LocalDateTime reportedAt;
    private String status = "WAITING";
    private String assignedAmbulance = "—";
    private String assignedHospital = "—";
    private String route = "—";
    private double etaMinutes = -1;
    private double hospitalEtaMinutes = -1;

    public Emergency(String location, Severity severity, boolean needsAdvancedEquipment) {
        this.id = "E" + NEXT.getAndIncrement();
        this.location = location;
        this.severity = severity;
        this.needsAdvancedEquipment = needsAdvancedEquipment;
        this.reportedAt = LocalDateTime.now();
    }
    public String getId() {
        return id;
    }
    public String getLocation() {
        return location;
    }
    public Severity getSeverity() {
        return severity;
    }
    public boolean needsAdvancedEquipment() {
        return needsAdvancedEquipment;
    }
    public LocalDateTime getReportedAt() {
        return reportedAt;
    }
    public String getReportedTime() {
        return reportedAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getAssignedAmbulance() {
        return assignedAmbulance;
    }
    public void setAssignedAmbulance(String value) {
        assignedAmbulance = value;
    }
    public String getAssignedHospital() {
        return assignedHospital;
    }
    public void setAssignedHospital(String value) {
        assignedHospital = value;
    }
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }
    public double getEtaMinutes() {
        return etaMinutes;
    }
    public void setEtaMinutes(double value) {
        etaMinutes = value;
    }
    public int severityRank() {
        return switch (severity) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case MODERATE -> 2;
            case LOW -> 1;
        };
    }
    public int priorityScore() {
        return severityRank() * 100;
    }
    public double getHospitalEtaMinutes() {
        return hospitalEtaMinutes;
    }
    public void setHospitalEtaMinutes(double hospitalEtaMinutes) {
        this.hospitalEtaMinutes = hospitalEtaMinutes;
    }
}
