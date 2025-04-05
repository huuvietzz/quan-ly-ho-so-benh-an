package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SettingController {

    @FXML
    private AnchorPane parent;

    private boolean isLightMode = true;

    public void changeMode(ActionEvent event) {
        isLightMode = !isLightMode;
        if (isLightMode) {
            setLightMode();
        } else {
            setDarkMode();
        }
    }

    private void setLightMode() {
        parent.getStylesheets().remove("/styles/darkMode.css");
        parent.getStylesheets().add("/styles/lightMode.css");
    }

    private void setDarkMode() {
        parent.getStylesheets().remove("/styles/lightMode.css");
        parent.getStylesheets().add("/styles/darkMode.css");
    }
}
