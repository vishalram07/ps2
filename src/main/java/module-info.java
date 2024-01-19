module com.dc {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.dc to javafx.fxml;
    exports com.dc;
}
