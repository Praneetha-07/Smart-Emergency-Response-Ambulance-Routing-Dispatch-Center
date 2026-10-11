module org.example.dsa_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.dsa_project to javafx.fxml;
    exports org.example.dsa_project;
}