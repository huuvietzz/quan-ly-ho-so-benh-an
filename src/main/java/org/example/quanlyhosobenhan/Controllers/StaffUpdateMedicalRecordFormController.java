package org.example.quanlyhosobenhan.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StaffUpdateMedicalRecordFormController {
    @FXML
    private DatePicker admissionDateField;

    @FXML
    private DatePicker consultationDateField;

    @FXML
    private DatePicker dischargeDateField;

    @FXML
    private ComboBox<String> hourAdmissionDateComboBox;

    @FXML
    private ComboBox<String> hourConsultationDateComboBox;

    @FXML
    private ComboBox<String> hourDischargeDateComboBox;

    @FXML
    private ComboBox<String> minuteAdmissionDateComboBox;

    @FXML
    private ComboBox<String> minuteConsultationDateComboBox;

    @FXML
    private ComboBox<String> minuteDischargeDateComboBox;

    @FXML
    private TextArea noteField;

    @FXML
    private TextField patientSearchField;

    @FXML
    private TextField doctorSearchField;


    private StaffMedicalRecordManagementController staffMedicalRecordManagementController;


    public void setMedicalRecordManagementController(StaffMedicalRecordManagementController controller) {
        this.staffMedicalRecordManagementController = controller;
    }

    private Patient selectedPatient;
    private MedicalRecord selectedRecord;
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    public void setMedicalRecord(MedicalRecord record) {
        this.selectedRecord = record;
        this.selectedPatient = record.getPatient();
        fillFormWithData();
    }

    private void fillFormWithData() {
        if (selectedRecord == null) return;

        patientSearchField.setText(selectedPatient.getFullName());

        if (selectedRecord.getDoctor() != null) {
            doctorSearchField.setText(selectedRecord.getDoctor().getFullName());
        }

        LocalDateTime consultationDateTime = selectedRecord.getConsultationDate();
        if (consultationDateTime != null) {
            consultationDateField.setValue(consultationDateTime.toLocalDate());
            hourConsultationDateComboBox.setValue(String.format("%02d h", consultationDateTime.getHour()));
            minuteConsultationDateComboBox.setValue(String.format("%02d m", consultationDateTime.getMinute()));
        }

        LocalDateTime admission = selectedRecord.getAdmissionDate();
        if (admission != null) {
            admissionDateField.setValue(admission.toLocalDate());
            hourAdmissionDateComboBox.setValue(String.format("%02d h", admission.getHour()));
            minuteAdmissionDateComboBox.setValue(String.format("%02d m", admission.getMinute()));
        }

        LocalDateTime discharge = selectedRecord.getDischargeDate();
        if (discharge != null) {
            dischargeDateField.setValue(discharge.toLocalDate());
            hourDischargeDateComboBox.setValue(String.format("%02d h", discharge.getHour()));
            minuteDischargeDateComboBox.setValue(String.format("%02d m", discharge.getMinute()));
        }

        noteField.setText(selectedRecord.getNotes());
    }

    @FXML
    public void initialize() {
        patientSearchField.setEditable(false);
        doctorSearchField.setEditable(false);

        setupDatePickerFormat(admissionDateField);
        setupDatePickerFormat(consultationDateField);
        setupDatePickerFormat(dischargeDateField);

        for (int i = 0; i < 24; i++) {
            hourAdmissionDateComboBox.getItems().add(String.format("%02d h", i));
            hourConsultationDateComboBox.getItems().add(String.format("%02d h", i));
            hourDischargeDateComboBox.getItems().add(String.format("%02d h", i));
        }

        for (int i = 0; i < 60; i++) {
            minuteAdmissionDateComboBox.getItems().add(String.format("%02d m", i));
            minuteConsultationDateComboBox.getItems().add(String.format("%02d m", i));
            minuteDischargeDateComboBox.getItems().add(String.format("%02d m", i));
        }
    }

    private void setupDatePickerFormat(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string == null || string.isEmpty()) ? null : LocalDate.parse(string, formatter);
            }
        });
    }

    @FXML
    void update(ActionEvent event) {
        try {
            if (selectedPatient == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa chọn bệnh nhân!");
                return;
            }

            LocalDateTime admissionDateTime = getDateTimeFromPicker(admissionDateField, hourAdmissionDateComboBox, minuteAdmissionDateComboBox);
            LocalDateTime consultationDateTime = getDateTimeFromPicker(consultationDateField, hourConsultationDateComboBox, minuteConsultationDateComboBox);
            LocalDateTime dischargeDateTime = getDateTimeFromPicker(dischargeDateField, hourDischargeDateComboBox, minuteDischargeDateComboBox);

            selectedRecord.setAdmissionDate(admissionDateTime);
            selectedRecord.setConsultationDate(consultationDateTime);
            selectedRecord.setDischargeDate(dischargeDateTime);
            selectedRecord.setNotes(noteField.getText().trim());

            medicalRecordDAO.updateMedicalRecord(selectedRecord);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật hồ sơ bệnh án thành công!");

            if (staffMedicalRecordManagementController != null) {
                staffMedicalRecordManagementController.refreshTable();
            }

            ((Stage) admissionDateField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi cập nhật!");
        }
    }

    private LocalDateTime getDateTimeFromPicker(DatePicker datePicker, ComboBox<String> hourBox, ComboBox<String> minuteBox) {
        if (datePicker.getValue() == null || hourBox.getValue() == null || minuteBox.getValue() == null) return null;
        int hour = Integer.parseInt(hourBox.getValue().split(" ")[0]);
        int minute = Integer.parseInt(minuteBox.getValue().split(" ")[0]);
        return LocalDateTime.of(datePicker.getValue(), LocalTime.of(hour, minute));
    }

    @FXML
    void cancel(ActionEvent event) {
        ((Stage) admissionDateField.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
