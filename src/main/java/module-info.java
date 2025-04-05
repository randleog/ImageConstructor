module com.example.imageconstructor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.commons.io;
    requires jdk.jfr;

    opens com.example.imageconstructor to javafx.fxml;
    exports com.example.imageconstructor;
}