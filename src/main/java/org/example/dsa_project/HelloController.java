package org.example.dsa_project;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.example.dsa_project.dsa.*;
import org.example.dsa_project.model.*;
import org.example.dsa_project.service.DispatchService;
import org.example.dsa_project.simulation.DemoData;
import java.util.*;

public class HelloController {
    @FXML private Label ambulanceCountLabel, waitingCountLabel, dispatchedCountLabel, routeSummaryLabel, statusLabel, algorithmLabel;
    @FXML private ComboBox<String> locationBox, roadFromBox, roadToBox, emergencyFilterBox;
    @FXML private ComboBox<Emergency.Severity> severityBox;
    @FXML private CheckBox advancedEquipmentBox;
    @FXML private TableView<Emergency> emergencyTable;
    @FXML private TableColumn<Emergency,String> idCol, locationCol, severityCol, statusCol, ambulanceCol, hospitalCol, etaCol;
    @FXML private TableView<Ambulance> ambulanceTable;
    @FXML private TableColumn<Ambulance,String> ambulanceIdCol, ambulanceLocationCol, ambulanceStatusCol, equipmentCol, assignmentCol;
    @FXML private TableView<Hospital> hospitalTable;
    @FXML private TableColumn<Hospital,String> hospitalNameCol, hospitalLocationCol, bedsCol, facilitiesCol;
    @FXML private ListView<String> historyList;
    @FXML private Canvas cityCanvas;
    @FXML private TableColumn<Emergency, String> hospitalEtaCol;
    private final CityGraph graph = DemoData.city();
    private final DispatchService service = new DispatchService(graph, DemoData.ambulances(), DemoData.hospitals());
    private String lastStart="Central", lastGoal="CityHospital";
    private PathResult lastPath;

    @FXML public void initialize() {
        locationBox.setItems(FXCollections.observableArrayList(graph.getNodes()));
        locationBox.setValue("EastPark");
        roadFromBox.setItems(FXCollections.observableArrayList(graph.getNodes()));
        roadToBox.setItems(FXCollections.observableArrayList(graph.getNodes()));
        roadFromBox.setValue("Market");
        roadToBox.setValue("BusStand");
        severityBox.setItems(FXCollections.observableArrayList(Emergency.Severity.values()));
        severityBox.setValue(Emergency.Severity.HIGH);
        emergencyFilterBox.setItems(FXCollections.observableArrayList("All", "WAITING", "DISPATCHED", "COMPLETED"));
        emergencyFilterBox.setValue("All");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        locationCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocation()));
        severityCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSeverity().toString()));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        ambulanceCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAssignedAmbulance()));
        hospitalCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAssignedHospital()));
        etaCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEtaMinutes()<0?"—":String.format(Locale.US,"%.1f min",c.getValue().getEtaMinutes())));
        ambulanceIdCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        ambulanceLocationCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocation()));
        ambulanceStatusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().toString()));
        equipmentCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().hasAdvancedEquipment()?"Advanced":"Basic"));
        assignmentCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAssignedEmergency()));
        hospitalNameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        hospitalLocationCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocation()));
        bedsCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(Integer.toString(c.getValue().getAvailableBeds())));
        facilitiesCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.join(", ",c.getValue().getFacilities())));
        emergencyFilterBox.setOnAction(e -> refreshTables());
        cityCanvas.widthProperty().addListener(
                (o,a,b)->drawCity());
        cityCanvas.heightProperty().addListener(
                (o,a,b)->drawCity()
        );
        // Seed the application with example incidents to make the dashboard demonstrable immediately.
        hospitalEtaCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getHospitalEtaMinutes() < 0
                                ? "—"
                                : String.format(
                                Locale.US, "%.1f min",
                                c.getValue().getHospitalEtaMinutes()
                        )
                )
        );
        DemoData.emergencies().forEach(service::addEmergency);
        refreshAll(); drawCity();
    }
    @FXML private void onReportEmergency() {
        if(locationBox.getValue()==null || severityBox.getValue()==null){
            alert("Choose a location and severity first.");return;
        }
        Emergency e=new Emergency(locationBox.getValue(),severityBox.getValue(),advancedEquipmentBox.isSelected());
        service.addEmergency(e);
        statusLabel.setText("Reported " + e.getId() + " — " + e.getSeverity() + " priority");
        refreshAll();
    }
    @FXML private void onDispatchNext() {
        DispatchService.DispatchResult result=service.dispatchNext();
        statusLabel.setText(result.message());
        if(result.success()) {lastStart=result.ambulance().getLocation();
            lastGoal=result.emergency().getLocation();
            lastPath=result.route();
            routeSummaryLabel.setText(
                    result.ambulance().getId()
                            + ": " + lastPath.pathText()
                            + " | Response ETA: "
                            + String.format(Locale.US, "%.1f", lastPath.minutes())
                            + " min"
                            + " | Hospital: " + result.hospital().getName()
                            + " | Hospital ETA: "
                            + String.format(
                            Locale.US, "%.1f",
                            result.emergency().getHospitalEtaMinutes()
                    )
                            + " min"
            );
        }
        refreshTables();
        refreshAll();
        drawCity();
    }
    @FXML private void onCompleteSelected() {
        Emergency e=emergencyTable.getSelectionModel().getSelectedItem();
        if(e==null){alert("Select an emergency from the table first.");
            return;
        }
        service.completeEmergency(e.getId());
        statusLabel.setText("Completion processed for "+e.getId());
        refreshTables();
        refreshAll();
        drawCity();
    }
    @FXML private void onToggleRoad() {
        String a=roadFromBox.getValue(),b=roadToBox.getValue();
        if(a==null||b==null||a.equals(b)){
            alert("Choose two different intersections.");
            return;
        }
        try{
            service.toggleRoad(a,b);
            service.recalculateRoutes();
            statusLabel.setText("Road updated: "+a+" ↔ "+b);
            refreshTables();;
            refreshAll();
            drawCity();
        }
        catch(IllegalArgumentException ex){
            alert(ex.getMessage());
        }
    }
    @FXML private void onIncreaseTraffic() {
        String a=roadFromBox.getValue(),b=roadToBox.getValue();
        if(a==null||b==null||a.equals(b)){
            alert("Choose two different intersections.");
            return;
        }
        try{service.increaseTraffic(a,b);
            service.recalculateRoutes();
            statusLabel.setText("Traffic increased on "+a+" ↔ "+b);
            refreshTables();
            refreshAll();
            drawCity();
        }
        catch(IllegalArgumentException ex){
            alert(ex.getMessage());
        }
    }
    @FXML private void onFindRoute() {
        lastStart="Central";
        lastGoal=locationBox.getValue();
        lastPath=RouteFinder.dijkstra(graph,lastStart,lastGoal);
        algorithmLabel.setText("Dijkstra | nodes visited: "+lastPath.visitedNodes());
        routeSummaryLabel.setText(lastPath.reachable()?lastStart+" → "+lastPath.pathText()+" | "+String.format(Locale.US,"%.1f",lastPath.minutes())+" min | "+String.format(Locale.US,"%.1f",lastPath.distanceKm())+" km":"No route available.");
        drawCity();
    }
    @FXML private void onCompareAlgorithms() {
        String goal=locationBox.getValue();
        PathResult d=RouteFinder.dijkstra(graph,"Central",goal),a=RouteFinder.aStar(graph,"Central",goal);
        algorithmLabel.setText("Dijkstra: "+d.visitedNodes()+" nodes | A*: "+a.visitedNodes()+" nodes | both use zero heuristic until coordinates are added");
        statusLabel.setText("Comparison completed for Central → "+goal+". A* currently uses a safe zero heuristic.");
    }
    @FXML private void onResetSimulation() {
        // Reset the application state by reporting a clear instruction: full reset is intentionally explicit.
        alert("To reset all simulation data, restart the application. This avoids silently losing dispatch history.");
    }
    private void refreshAll(){
        refreshTables();
        ambulanceCountLabel.setText(Long.toString(service.getAmbulances().stream().filter(a->a.getStatus()==Ambulance.Status.AVAILABLE).count()));
        waitingCountLabel.setText(Long.toString(service.getEmergencies().stream().filter(e->e.getStatus().equals("WAITING")).count()));
        dispatchedCountLabel.setText(Long.toString(service.getEmergencies().stream().filter(e->e.getStatus().equals("DISPATCHED")).count()));
        historyList.setItems(FXCollections.observableArrayList(service.getHistory()));drawCity();
    }
    private void refreshTables(){
        String filter=emergencyFilterBox.getValue();
        emergencyTable.setItems(FXCollections.observableArrayList(service.getEmergencies().stream().filter(e->filter==null||filter.equals("All")||e.getStatus().equals(filter)).toList()));
        ambulanceTable.setItems(FXCollections.observableArrayList(service.getAmbulances()));hospitalTable.setItems(FXCollections.observableArrayList(service.getHospitals()));
    }
    private void drawCity(){
        if(cityCanvas==null)
            return;
        GraphicsContext g=cityCanvas.getGraphicsContext2D();
        double w=cityCanvas.getWidth(),h=cityCanvas.getHeight();
        if(w<10||h<10)
            return;
        g.setFill(Color.web("#101827"));
        g.fillRect(0,0,w,h);
        Map<String,double[]> pos=positions(w,h);
        g.setLineWidth(3);
        for(Road r:graph.getAllRoads()){
            if(r.getFrom().compareTo(r.getTo())>0)
                continue;
            double[] p=pos.get(r.getFrom()),q=pos.get(r.getTo());
            g.setStroke(r.isBlocked()?Color.web("#F87171"):r.getTravelMinutes()>=7?Color.web("#FBBF24"):Color.web("#52647A"));
            g.strokeLine(p[0],p[1],q[0],q[1]);
            g.setFill(Color.web("#A9B8CA"));
            g.setFont(javafx.scene.text.Font.font(10));
            g.fillText(String.format(Locale.US,"%.0fm",r.getTravelMinutes()),(p[0]+q[0])/2,(p[1]+q[1])/2-4);
        }
        if(lastPath!=null&&lastPath.reachable()){
            g.setStroke(Color.web("#34D399"));
            g.setLineWidth(5);
            for(int i=0;i<lastPath.path().size()-1;i++){
                double[] p=pos.get(lastPath.path().get(i)),q=pos.get(lastPath.path().get(i+1));
                g.strokeLine(p[0],p[1],q[0],q[1]);
            }
        }
        for(String node:graph.getNodes()){
            double[] p=pos.get(node);
            g.setFill(Color.web("#DBEAFE"));
            g.fillOval(p[0]-6,p[1]-6,12,12);
            g.setFill(Color.WHITE);
            g.setFont(javafx.scene.text.Font.font(11));
            g.fillText(node,p[0]+8,p[1]-8);
        }
        for(Ambulance a:service.getAmbulances()){
            double[] p=pos.get(a.getLocation());
            g.setFill(a.getStatus()==Ambulance.Status.AVAILABLE?Color.web("#60A5FA"):Color.web("#FB923C"));
            g.fillRoundRect(p[0]-8,p[1]+7,16,8,3,3);
        }
    }
    private Map<String,double[]> positions(double w,double h){
        Map<String,double[]> p=new HashMap<>();
        p.put("Central",new double[]{w*.12,h*.48});
        p.put("Market",new double[]{w*.32,h*.30});
        p.put("College",new double[]{w*.32,h*.70});
        p.put("BusStand",new double[]{w*.55,h*.22});
        p.put("EastPark",new double[]{w*.57,h*.52});
        p.put("WestGate",new double[]{w*.52,h*.83});
        p.put("RiverRoad",new double[]{w*.76,h*.69});
        p.put("CityHospital",new double[]{w*.91,h*.48});
        p.put("TraumaCenter",new double[]{w*.80,h*.18});
        p.put("NorthJunction",new double[]{w*.28,h*.10});
        return p;
    }
    private void alert(String message){
        new Alert(Alert.AlertType.INFORMATION,message,ButtonType.OK).showAndWait();
    }
}
