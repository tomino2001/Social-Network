module com.example.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.socialnetwork to javafx.fxml;
    opens com.example.socialnetwork.domain to javafx.fxml;
    opens com.example.socialnetwork.controller to javafx.fxml;

    exports com.example.socialnetwork;
    exports com.example.socialnetwork.domain;
    exports com.example.socialnetwork.controller to javafx.fxml;
}