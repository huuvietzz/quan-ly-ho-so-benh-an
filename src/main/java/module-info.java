module org.example.quanlyhosobenhan {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.desktop;
    requires java.prefs;
    requires jbcrypt;
    requires java.persistence;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires static lombok;
    requires java.validation;
    requires org.apache.poi.ooxml;
    requires layout;
    requires kernel;
    requires io;

    opens org.example.quanlyhosobenhan to javafx.fxml, org.hibernate.orm.core;
    exports org.example.quanlyhosobenhan;

    opens org.example.quanlyhosobenhan.Controllers to javafx.fxml, org.hibernate.orm.core;
    exports org.example.quanlyhosobenhan.Controllers;

    opens org.example.quanlyhosobenhan.Dao to javafx.fxml, org.hibernate.orm.core;
    exports org.example.quanlyhosobenhan.Dao;

    opens org.example.quanlyhosobenhan.Model to javafx.fxml, org.hibernate.orm.core;
    exports org.example.quanlyhosobenhan.Model;

    opens org.example.quanlyhosobenhan.Util to javafx.fxml, org.hibernate.orm.core;
    exports org.example.quanlyhosobenhan.Util;
}