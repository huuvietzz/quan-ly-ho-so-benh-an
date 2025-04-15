package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.quanlyhosobenhan.Model.PrescriptionDetail;

public class UpdatePrescriptionFormController {
    @FXML
    private TextField dosageField;

    @FXML
    private TextField medicineNameField;

    @FXML
    private TextArea noteField;

    @FXML
    private TextArea usageInstructionsField;

    private PrescriptionDetail result;

    public PrescriptionDetail getResult() {
        return result;
    }

    @FXML
    void updateBtn(ActionEvent event) {
        String medicineName = medicineNameField.getText();
        String dosage = dosageField.getText();
        String usageInStructions = usageInstructionsField.getText();
        String note = noteField.getText();

        if(medicineName.trim().isEmpty() || dosage.trim().isEmpty() || usageInStructions.trim().isEmpty() ) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        result = new PrescriptionDetail();
        result.setMedicineName(medicineName);
        result.setDosage(dosage);
        result.setUsageInstructions(usageInStructions);
        result.setNotes(note);

        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Cập nhật đơn thuốc thành công!");
        closeWindow();
    }

    @FXML
    void cancelBtn(ActionEvent event) {
       result = null;
       closeWindow();
    }

    public void setData(PrescriptionDetail detail) {
        this.result = null; // reset
        medicineNameField.setText(detail.getMedicineName());
        dosageField.setText(detail.getDosage());
        usageInstructionsField.setText(detail.getUsageInstructions());
        noteField.setText(detail.getNotes());
    }

    private void closeWindow(){
        Stage stage = (Stage) medicineNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
