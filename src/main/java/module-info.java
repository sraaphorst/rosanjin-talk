module com.vorpal.rosanjintalk {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    exports com.vorpal.rosanjintalk.model;
    exports com.vorpal.rosanjintalk.controller;
    opens com.vorpal.rosanjintalk.controller to javafx.fxml;
    exports com.vorpal.rosanjintalk;
    opens com.vorpal.rosanjintalk to javafx.fxml;
    exports com.vorpal.rosanjintalk.view.management;
    opens com.vorpal.rosanjintalk.view.management to javafx.fxml;
    exports com.vorpal.rosanjintalk.controller.management;
    opens com.vorpal.rosanjintalk.controller.management to javafx.fxml;
    exports com.vorpal.rosanjintalk.shared;
    opens com.vorpal.rosanjintalk.shared to javafx.fxml;
    opens com.vorpal.rosanjintalk.model to com.fasterxml.jackson.databind;
}