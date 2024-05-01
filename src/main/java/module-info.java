module com.example.javafxreadingdemo {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;
    requires java.sql;

    //requires testng;


    opens com.example.javafxreadingdemo to javafx.fxml;
    exports com.example.javafxreadingdemo;
}