package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class UpdateMedicalRecordFormController {
    @FXML
    private DatePicker admissionDateField;

    @FXML
    private DatePicker consultationDateField;

    @FXML
    private TextArea diagnoseField;

    @FXML
    private DatePicker dischargeDateField;

    @FXML
    private TextArea examinationResultField;

    @FXML
    private TextArea finalTreatmentResultField;

    @FXML
    private TextArea noteField;

    @FXML
    private TextField patientSearchField;

    @FXML
    private TextArea symptomField;

    @FXML
    private TextArea treatmentMethodField;

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

    private Patient selectedPatient;

    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    private MedicalRecordManagementController medicalRecordManagementController;

    private MedicalRecord selectedRecord;

    public void setMedicalRecordManagementController(MedicalRecordManagementController controller) {
        this.medicalRecordManagementController = controller;
    }


    public void setMedicalRecord(MedicalRecord record) {
        this.selectedRecord = record;
        this.selectedPatient = record.getPatient();
        fillFormWithData();
    }

    private void fillFormWithData() {
        if (selectedRecord == null) return;

        patientSearchField.setText(selectedPatient.getFullName());

        // Fill ngày giờ khám
        LocalDateTime consultationDateTime = selectedRecord.getConsultationDate();
        if (consultationDateTime != null) {
            consultationDateField.setValue(consultationDateTime.toLocalDate());
            hourConsultationDateComboBox.setValue(String.format("%02d h", consultationDateTime.getHour()));
            minuteConsultationDateComboBox.setValue(String.format("%02d m", consultationDateTime.getMinute()));
        }

        // Tương tự cho admission và discharge
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

        // Fill các field khác
        symptomField.setText(selectedRecord.getSymptoms());
        diagnoseField.setText(selectedRecord.getDiagnosis());
        examinationResultField.setText(selectedRecord.getExaminationResult());
        treatmentMethodField.setText(selectedRecord.getTreatmentMethod());
        finalTreatmentResultField.setText(selectedRecord.getFinalTreatmentResult());
        noteField.setText(selectedRecord.getNotes());
    }

    @FXML
    public void initialize() {
        patientSearchField.setEditable(false);

        // Setup định dạng cho các DatePicker
        setupDatePickerFormat(admissionDateField);
        setupDatePickerFormat(consultationDateField);
        setupDatePickerFormat(dischargeDateField);

        for(int i = 0;i < 24; i++) {
            hourConsultationDateComboBox.getItems().add(String.format("%02d h", i));
            hourDischargeDateComboBox.getItems().add(String.format("%02d h", i));
            hourAdmissionDateComboBox.getItems().add(String.format("%02d h", i));
        }

        for(int i = 0; i < 60; i++) {
            minuteConsultationDateComboBox.getItems().add(String.format("%02d m", i));
            minuteDischargeDateComboBox.getItems().add(String.format("%02d m", i));
            minuteAdmissionDateComboBox.getItems().add(String.format("%02d m", i));
        }
    }

    private void setupDatePickerFormat(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    @FXML
    void update(ActionEvent event) {
        try {
            // Lấy giá trị từ DatePicker và ComboBox
            LocalDate consultationDate = consultationDateField.getValue();
            String consultationHourStr = hourConsultationDateComboBox.getValue();
            String consultationMinuteStr = minuteConsultationDateComboBox.getValue();

            // Kiểm tra các trường bắt buộc trước khi xử lý
            if (selectedPatient == null || consultationDate == null
                    || consultationHourStr == null || consultationMinuteStr == null
                    || symptomField.getText().trim().isEmpty()
                    || diagnoseField.getText().trim().isEmpty()
                    || examinationResultField.getText().trim().isEmpty()
                    || treatmentMethodField.getText().trim().isEmpty()
                    || finalTreatmentResultField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin bắt buộc!");
                return;
            }

            // Nếu không lỗi thì mới parse
            int consultationHour = Integer.parseInt(consultationHourStr.split(" ")[0]);
            int consultationMinute = Integer.parseInt(consultationMinuteStr.split(" ")[0]);
            LocalDateTime consultationDateTime = LocalDateTime.of(consultationDate, LocalTime.of(consultationHour, consultationMinute));

            // admissionDateTime và dischargeDateTime có thể null
            LocalDateTime admissionDateTime = null;
            LocalDate admissionDate = admissionDateField.getValue();
            if (admissionDate != null
                    && hourAdmissionDateComboBox.getValue() != null
                    && minuteAdmissionDateComboBox.getValue() != null) {
                int admissionHour = Integer.parseInt(hourAdmissionDateComboBox.getValue().split(" ")[0]);
                int admissionMinute = Integer.parseInt(minuteAdmissionDateComboBox.getValue().split(" ")[0]);
                admissionDateTime = LocalDateTime.of(admissionDate, LocalTime.of(admissionHour, admissionMinute));
            }

            LocalDateTime dischargeDateTime = null;
            LocalDate dischargeDate = dischargeDateField.getValue();
            if (dischargeDate != null
                    && hourDischargeDateComboBox.getValue() != null
                    && minuteDischargeDateComboBox.getValue() != null) {
                int dischargeHour = Integer.parseInt(hourDischargeDateComboBox.getValue().split(" ")[0]);
                int dischargeMinute = Integer.parseInt(minuteDischargeDateComboBox.getValue().split(" ")[0]);
                dischargeDateTime = LocalDateTime.of(dischargeDate, LocalTime.of(dischargeHour, dischargeMinute));
            }

            // Lấy các field khác
            String symptom = symptomField.getText().trim();
            String diagnosis = diagnoseField.getText().trim();
            String examinationResult = examinationResultField.getText().trim();
            String treatmentMethod = treatmentMethodField.getText().trim();
            String finalTreatmentResult = finalTreatmentResultField.getText().trim();
            String note = noteField.getText().trim();

            // Tạo MedicalRecord
            selectedRecord.setPatient(selectedPatient);
            selectedRecord.setDoctor(LoginController.loggedInDoctor);
            selectedRecord.setConsultationDate(consultationDateTime);
            selectedRecord.setAdmissionDate(admissionDateTime);
            selectedRecord.setDischargeDate(dischargeDateTime);
            selectedRecord.setSymptoms(symptom);
            selectedRecord.setDiagnosis(diagnosis);
            selectedRecord.setExaminationResult(examinationResult);
            selectedRecord.setTreatmentMethod(treatmentMethod);
            selectedRecord.setFinalTreatmentResult(finalTreatmentResult);
            selectedRecord.setNotes(note);

            medicalRecordDAO.updateMedicalRecord(selectedRecord);
            showAlert(Alert.AlertType.INFORMATION, "Thông báo!", "Cập nhật hồ sơ bệnh án thành công!");

            if (medicalRecordManagementController != null) {
                medicalRecordManagementController.refreshTable();
            }

            Stage stage = (Stage) admissionDateField.getScene().getWindow();
            stage.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) admissionDateField.getScene().getWindow();
        stage.close();
    }

    @FXML
    void searchPatient(ActionEvent event) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Chọn bệnh nhân");

            TableView<Patient> table = new TableView<>();

            TableColumn<Patient, Integer> idColumn = new TableColumn<>("ID");
            idColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));

            TableColumn<Patient, String> nameColumn = new TableColumn<>("Tên bệnh nhân");
            nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));

            table.getColumns().addAll(idColumn, nameColumn);

            // Tự động giãn cột, xóa cột ảo
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // ⚡ Load dữ liệu từ database
            int doctorId = LoginController.loggedInDoctor.getId();
            table.getItems().addAll(new PatientDAO().getPatientsByDoctorId(doctorId));

            table.setRowFactory(tv -> {
                TableRow<Patient> row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && !row.isEmpty()) {
                        Patient selected = row.getItem();
                        selectedPatient = selected;
                        patientSearchField.setText(selected.getFullName()); // set tên bệnh nhân vào text field
                        stage.close();
                    }
                });
                return row;
            });

            TextField searchField = new TextField();
            searchField.setPromptText("Tìm bệnh nhân...");
            searchField.textProperty().addListener((obs, oldText, newText) -> {
                table.setItems(FXCollections.observableArrayList(
                        new PatientDAO().getPatientsByDoctorId(doctorId).stream()
                                .filter(p ->
                                        String.valueOf(p.getId()).contains(newText) ||
                                                p.getFullName().toLowerCase().contains(newText.toLowerCase()))
                                .toList()
                ));
            });

            VBox vbox = new VBox(10, searchField, table);
            vbox.setPadding(new Insets(10));

            ScrollPane scrollPane = new ScrollPane(vbox);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            Scene scene = new Scene(scrollPane, 400, 300);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
