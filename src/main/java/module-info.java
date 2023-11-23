module com.example.thermalprinterdesktop {
    requires escpos.coffee;
    requires java.desktop;
    requires java.sql;
    requires lombok;
    requires java.prefs;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;


    opens com.example.thermalprinterdesktop to javafx.fxml;
    exports com.example.thermalprinterdesktop;
    exports com.example.thermalprinterdesktop.model;
    opens com.example.thermalprinterdesktop.model to javafx.fxml;
    exports com.example.thermalprinterdesktop.controller;
    opens com.example.thermalprinterdesktop.controller to javafx.fxml;
    exports com.example.thermalprinterdesktop.service;
    opens com.example.thermalprinterdesktop.service to javafx.fxml;
    exports com.example.thermalprinterdesktop.database;
    opens com.example.thermalprinterdesktop.database to javafx.fxml;
    exports com.example.thermalprinterdesktop.manager;
    opens com.example.thermalprinterdesktop.manager to javafx.fxml;
}
