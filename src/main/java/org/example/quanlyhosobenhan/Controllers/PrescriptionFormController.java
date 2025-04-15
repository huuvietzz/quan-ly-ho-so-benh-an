package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.quanlyhosobenhan.Model.PrescriptionDetail;

import java.net.URL;
import java.util.ResourceBundle;

public class PrescriptionFormController {
    @FXML
    private TableView<PrescriptionDetail> prescriptionTable;

    @FXML
    private TableColumn<PrescriptionDetail, Void> actionColumn;

    @FXML
    private TextField doctorField;

    @FXML
    private TableColumn<PrescriptionDetail, String> dosageColumn;

    @FXML
    private TextArea generalNotesField;

    @FXML
    private TableColumn<PrescriptionDetail, String> medicineNameColumn;

    @FXML
    private TableColumn<PrescriptionDetail, String> noteColum;

    @FXML
    private TextField patientField;

    @FXML
    private TextField prescriptionDateField;

    @FXML
    private TableColumn<PrescriptionDetail, String> usageInstructionsColumn;

    // Ô nhập dữ liệu chi tiết thuốc
    @FXML
    private TextField medicineNameInput;

    @FXML
    private TextField dosageInput;

    @FXML
    private TextField usageInput;

    @FXML
    private TextField noteInput;

    private final ObservableList<PrescriptionDetail> prescriptionDetails = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Gán danh sách dữ liệu vào bảng
        prescriptionTable.setItems(prescriptionDetails);

        // Cột dữ liệu
        medicineNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicineName()));
        dosageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDosage()));
        usageInstructionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsageInstructions()));
        noteColum.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotes()));

        addActionButtonsToTable();

        // Gán sẵn thông tin bệnh nhân, bác sĩ, ngày kê
        doctorField.setText("BS. Nguyễn Văn A");
        patientField.setText("Nguyễn Văn B");
        prescriptionDateField.setText("2025-04-15");
    }

    @FXML
    void addBtn(ActionEvent event) {
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/AddPrescriptionForm.fxml"));
           Parent root = loader.load();

           Stage stage = new Stage();
           stage.setTitle("Thêm đơn thuốc");
           stage.setScene(new Scene(root));
           stage.showAndWait();

           AddPrescriptionFormController controller = loader.getController();
           PrescriptionDetail newItem = controller.getResult();

           if(newItem != null) {
               prescriptionDetails.add(newItem);
           }

       } catch(Exception e){
           e.printStackTrace();
       }
    }

    @FXML
    void cancelBtn(ActionEvent event) {

    }

    @FXML
    void saveBtn(ActionEvent event) {
//         showAlert();
    }

    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            final Button editButton = new Button("✎"); // biểu tượng chỉnh sửa
            final Button deleteButton = new Button("🗑"); // biểu tượng xóa
            final HBox hbox = new HBox(10, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-font-size: 18px;");
                deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 18px;");

                deleteButton.setOnAction(event -> {
                    PrescriptionDetail item = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận xóa");
                    alert.setHeaderText("Bạn có chắc muốn xóa đơn thuốc này?");

                    ButtonType buttonYes = new ButtonType("Có", ButtonBar.ButtonData.YES);
                    ButtonType buttonNo = new ButtonType("Không", ButtonBar.ButtonData.NO);

                    alert.getButtonTypes().setAll(buttonYes, buttonNo);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == buttonYes) {
                            prescriptionDetails.remove(item);
                        }
                    });
                });

                editButton.setOnAction(event -> {
                    PrescriptionDetail selectedItem = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/UpdatePrescriptionForm.fxml"));
                        Parent root = loader.load();

                        // Lấy controller và truyền dữ liệu
                        UpdatePrescriptionFormController controller = loader.getController();
                        controller.setData(selectedItem); // truyền dữ liệu để hiển thị

                        Stage stage = new Stage();
                        stage.setTitle("Cập nhật đơn thuốc");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                        // Sau khi cập nhật xong, lấy lại dữ liệu
                        PrescriptionDetail updated = controller.getResult();
                        if(updated != null) {
                            selectedItem.setMedicineName(updated.getMedicineName());
                            selectedItem.setDosage(updated.getDosage());
                            selectedItem.setUsageInstructions(updated.getUsageInstructions());
                            selectedItem.setNotes(updated.getNotes());

                            // Cập nhật lại bảng
                            prescriptionTable.refresh();
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
