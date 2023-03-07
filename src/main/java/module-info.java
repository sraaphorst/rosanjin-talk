module com.vorpal.rosanjintalk {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.vorpal.rosanjintalk.ui to javafx.fxml;
    exports com.vorpal.rosanjintalk.ui;
    exports com.vorpal.rosanjintalk.model;
}