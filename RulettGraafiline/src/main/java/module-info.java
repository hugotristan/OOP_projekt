module com.example.rulettgraafiline {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rulettgraafiline to javafx.fxml;
    exports com.example.rulettgraafiline;
}