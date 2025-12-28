module com.edutrack {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires mysql.connector.j;

    requires jakarta.mail;

    opens com.edutrack to javafx.fxml;
    opens com.edutrack.controller to javafx.fxml;

    exports com.edutrack;
    exports com.edutrack.controller;
}
