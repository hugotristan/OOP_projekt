module com.example.rulett_graafiline {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rulett_graafiline to javafx.fxml;
    exports com.example.rulett_graafiline;
}