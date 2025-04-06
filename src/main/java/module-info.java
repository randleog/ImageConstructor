module com.example.imageconstructor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.commons.io;
    requires jdk.jfr;
    requires jdk.jdi;

    opens com.example.imageconstructor to javafx.fxml;
    exports com.example.imageconstructor;
}