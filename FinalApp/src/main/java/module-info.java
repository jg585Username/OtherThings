module com.example.finalapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.finalapp to javafx.fxml;
    exports com.example.finalapp;
}