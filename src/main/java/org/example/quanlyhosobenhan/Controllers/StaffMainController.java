package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Optional;

public class StaffMainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label userNameLabel;

    @FXML
    public void initialize() {
        loadPage("/Fxml/StaffDashboard.fxml");

        if (LoginController.loggedInStaff != null) {
            userNameLabel.setText("Xin chào, " + LoginController.loggedInStaff.getUserName());
            userNameLabel.setAlignment(Pos.CENTER);
        }
    }

    @FXML
    public void dashboard(ActionEvent event) {
        loadPage("/Fxml/StaffDashboard.fxml");
    }

    @FXML
    public void patientManagement(ActionEvent event) {
        loadPage("/Fxml/StaffPatientManagement.fxml");
    }

    @FXML
    public void medicalRecord(ActionEvent event) {
        loadPage("/Fxml/StaffMedicalRecord.fxml");
    }

    @FXML
    public void medicalRecordManagement(ActionEvent event) {
        loadPage("/Fxml/StaffMedicalRecordManagement.fxml");
    }

    @FXML
    public void appointment(ActionEvent event) {
        loadPage("/Fxml/StaffAppointment.fxml");
    }

    @FXML
    public void setting(ActionEvent event) {
        loadPage("/Fxml/StaffAccount.fxml");
    }

    @FXML
    public void logOut(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        alert.setContentText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
        }
    }

    private void loadPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentArea.getChildren().setAll(root);
            if (root instanceof Region) {
                Region region = (Region) root;
                region.prefWidthProperty().bind(contentArea.widthProperty());
                region.prefHeightProperty().bind(contentArea.heightProperty());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
