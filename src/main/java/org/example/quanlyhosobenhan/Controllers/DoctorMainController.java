package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DoctorMainController {
    @FXML
    private StackPane contentArea;

    @FXML
    private Label userNameLabel;


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


    @FXML
    public void initialize() {
        loadPage("/Fxml/DoctorDashboard.fxml");

        // Hiển thị tên bác sĩ
        if(LoginController.loggedInDoctor != null) {
            userNameLabel.setText("Xin chào, " + LoginController.loggedInDoctor.getUserName());
            userNameLabel.setAlignment(Pos.CENTER);
        }
    }

    @FXML
    public void dashboard(ActionEvent event) throws IOException {
        loadPage("/Fxml/DoctorDashboard.fxml");
    }

    @FXML
    public void patientManagement(ActionEvent event) throws IOException {
        loadPage("/Fxml/DoctorPatientManagement.fxml");
    }

    @FXML
    public void medicalRecord(ActionEvent event) throws IOException {
        loadPage("/Fxml/DoctorMedicalRecord.fxml");
    }

    @FXML
    public void medicalRecordManagement(ActionEvent event) throws IOException {
        loadPage("/Fxml/DoctorMedicalRecordManagement.fxml");
    }

    @FXML
    public void appointment(ActionEvent event) throws IOException {
        loadPage("/Fxml/DoctorAppointment.fxml");
    }

    @FXML
    public void profile(ActionEvent event) throws IOException {
        loadPage("/Fxml/DoctorProfile.fxml");
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        // Tạo hộp thoại xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        alert.setContentText(null);

        // Hiển thị hộp thoại và đợi người dùng tra lời
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            // Neu nguoi dung chon OK, load tro lai man hinh dang nhap
            SwitchScreenController.switchScreen(event, "/Fxml/Login.fxml", "Đăng nhập");
        }
    }

    private void loadPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));

            // Xóa và thêm node mới
            contentArea.getChildren().setAll(root);

            // Nếu root là Region (ví dụ ScrollPane, AnchorPane, VBox, …) thì mới bind kích thước
            if (root instanceof Region) {
                Region region = (Region) root;
                region.prefWidthProperty().bind(contentArea.widthProperty());
                region.prefHeightProperty().bind(contentArea.heightProperty());
            }

            // Nếu đúng là ScrollPane thì cho nó fit nội dung
//            if (root instanceof ScrollPane) {
//                ScrollPane sp = (ScrollPane) root;
//                sp.setFitToWidth(false);
//                sp.setFitToHeight(false);
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}