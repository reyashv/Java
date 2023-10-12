module com.example.jproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.jproject to javafx.fxml;
    exports com.example.jproject;
}
