module fhtw.projectwienschmobil {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.graphics;
    requires java.sql;


    opens fhtw.projectwienschmobil to com.google.gson, javafx.fxml;
    exports fhtw.projectwienschmobil;
}