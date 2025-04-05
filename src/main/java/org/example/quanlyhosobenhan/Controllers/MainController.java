package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
//    private double xOffset = 0;
//    private double yOffset = 0;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button close_btn;

    @FXML
    private Button minus_btn;

    @FXML
    public void minus(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void close(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPage("/Fxml/Dashboard.fxml");

        // Thêm sự kiện để di chuyển cửa sổ
//        contentArea.setOnMousePressed(event -> {
//            xOffset = event.getSceneX();
//            yOffset = event.getSceneY();
//        });
//
//        contentArea.setOnMouseDragged(event -> {
//            Stage stage = (Stage) contentArea.getScene().getWindow();
//            stage.setX(event.getScreenX() - xOffset);
//            stage.setY(event.getScreenY() - yOffset);
//        });

    }

    public void dashboard(ActionEvent event) throws IOException {
        loadPage("/Fxml/Dashboard.fxml");
    }

    public void patientManagement(ActionEvent event) throws IOException {
        loadPage("/Fxml/PatientManagement.fxml");
    }


    public void medicalRecord(ActionEvent event) throws IOException {
        loadPage("/Fxml/MedicalRecord.fxml");
    }

    public void medicalRecordManagement(ActionEvent event) throws IOException {
        loadPage("/Fxml/MedicalRecordManagement.fxml");
    }

    public void appointment(ActionEvent event) throws IOException {
        loadPage("/Fxml/Appointment.fxml");
    }

    public void setting(ActionEvent event) throws IOException {
        loadPage("/Fxml/Setting.fxml");
    }

    public void logOut(ActionEvent event) throws IOException {
        loadPage("/Fxml/Setting.fxml");
    }

    private void loadPage(String fxmlFile) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
