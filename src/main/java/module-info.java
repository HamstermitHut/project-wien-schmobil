module fhtw.projectwienschmobil {
    requires javafx.controls;
    requires javafx.fxml;


    opens fhtw.projectwienschmobil to javafx.fxml;
    exports fhtw.projectwienschmobil;
}