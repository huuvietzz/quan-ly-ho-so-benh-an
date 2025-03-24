module org.example.quanlyhosobenhan {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.desktop;
    requires java.prefs;
    requires jbcrypt;


    opens org.example.quanlyhosobenhan to javafx.fxml;
    opens org.example.quanlyhosobenhan.Controllers to javafx.fxml;
    exports org.example.quanlyhosobenhan;
    exports org.example.quanlyhosobenhan.Controllers;
    exports org.example.quanlyhosobenhan.Models;
}