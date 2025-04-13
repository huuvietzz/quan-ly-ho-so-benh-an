package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


public class MainController implements Initializable {
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
        // Tạo hộp thoại xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        alert.setContentText("Nhấn OK để đăng xuất, Cancel để hủy.");

        // Hiển thị hộp thoại và đợi người dùng tra lời
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {

            // Neu nguoi dung chon OK, load tro lai man hinh dang nhap
            SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
        }
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
