module com.example.module04assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.module04assignment to javafx.fxml;
    exports com.example.module04assignment;
}