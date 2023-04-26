module com.libary {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.apache.commons.collections4;
    requires java.sql.rowset;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens com.library to javafx.fxml;
    opens com.library.controllers to javafx.fxml, javafx.graphics;
    opens com.library.models to javafx.fxml, javafx.base;

    exports com.library.controllers to javafx.fxml;
//    exports com.sun.javafx.event to org.controlsfx.controls;
    exports com.library;
}
