package org.example.dsa_project.model;

public class Ambulance {
        public enum Status { AVAILABLE, DISPATCHED, BUSY }
        private final String id;
        private String location;
        private final boolean advancedEquipment;
        private Status status = Status.AVAILABLE;
        private String assignedEmergency = "—";
        private String destinationHospital = "—";

        public Ambulance(String id, String location, boolean advancedEquipment) {
            this.id = id;
            this.location = location;
            this.advancedEquipment = advancedEquipment;
        }
        public String getId() {
                return id;
        }
        public String getLocation() {
                return location;
        }
        public void setLocation(String location) {
                this.location = location;
        }
        public boolean hasAdvancedEquipment() {
                return advancedEquipment;
        }
        public Status getStatus() {
                return status;
        }
        public void setStatus(Status status) {
                this.status = status;
        }
        public String getAssignedEmergency() {
                return assignedEmergency;
        }
        public void setAssignedEmergency(String id) {
                assignedEmergency = id;
        }
        public String getDestinationHospital() {
                return destinationHospital;
        }
        public void setDestinationHospital(String hospital) {
                destinationHospital = hospital;
        }

}
