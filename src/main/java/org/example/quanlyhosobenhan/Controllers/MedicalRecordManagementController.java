package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Dao.PrescriptionDAO;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Model.Patient;
import org.example.quanlyhosobenhan.Model.Prescription;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalRecordManagementController  {

    @FXML
    private Button addBtn;

    @FXML
    private TableColumn<MedicalRecord, LocalDate> consultationDateColumn;

    @FXML
    private DatePicker consultationDateField;

    @FXML
    private Button deleteBtn;

    @FXML
    private TableColumn<MedicalRecord, String> diagnoseColumn;

    @FXML
    private TextArea diagnoseField;

    @FXML
    private TableColumn<MedicalRecord, Void> prescriptionColumn;

    @FXML
    private ComboBox<String> exportBtn;

    @FXML
    private TableColumn<MedicalRecord, Integer> idRecordColumn;

    @FXML
    private TableColumn<MedicalRecord, String> noteColumn;

    @FXML
    private TextArea noteField;

    @FXML
    private TableColumn<MedicalRecord, String> patientColumn;

    @FXML
    private TextField patientSearchField;

    @FXML
    private TextField recordSearchField;

    @FXML
    private TableView<MedicalRecord> recordTable;

    @FXML
    private Button refreshBtn;

    @FXML
    private TableColumn<MedicalRecord, String> symptomColumn;

    @FXML
    private TextArea symptomField;

    @FXML
    private TableColumn<MedicalRecord, String> treatmentColumn;

    @FXML
    private TextArea treatmentField;

    @FXML
    private Button updateBtn;

    private Patient selectedPatient;


    private MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();


    @FXML
    public void initialize(){
        idRecordColumn.setCellValueFactory(cellData
                -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        patientColumn.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue().getPatient();
            return new SimpleStringProperty(patient != null ? patient.getName() : "");
        });

        patientColumn.setCellFactory(cellData   -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();

            {
                link.setUnderline(true);
                link.setFocusTraversable(false);
                link.setOnAction((ActionEvent event) -> {
                    MedicalRecord record = getTableView().getItems().get(getIndex());
                    Patient patient = record.getPatient();
                    openPatientDetail(patient);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    link.setText(item);
                    setGraphic(link);

                   // Check nếu dòng được chọn thì đổi màu chữ
                    TableRow<?> currentRow = getTableRow();
                    if(currentRow != null && currentRow.isSelected()) {
                        link.setStyle("-fx-text-fill: #fff");
                    } else {
                        link.setStyle("-fx-text-fill: #2a73ff;");
                    }

                    // Bắt sự kiện thay đổi chọn dòng
                    currentRow.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if(isNowSelected) {
                            link.setStyle("-fx-text-fill: #fff");
                        } else {
                            link.setStyle("-fx-text-fill: #2a73ff;");
                        }
                    });
                }
            }
        });

        symptomColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getSymptoms()));

        diagnoseColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDiagnosis()));

        consultationDateColumn.setCellValueFactory(cellData
                -> new SimpleObjectProperty<>(cellData.getValue().getConsultationDate()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        consultationDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        treatmentColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getTreatmentMethod()));

        prescriptionColumn.setCellFactory(col -> new TableCell<>() {
                private final Hyperlink link = new Hyperlink();

            {
                link.setUnderline(true);
                link.setFocusTraversable(false);
                link.setOnAction((ActionEvent event) -> {
                    MedicalRecord record = getTableView().getItems().get(getIndex());
                    openPrescriptionForm(record.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    MedicalRecord record = getTableView().getItems().get(getIndex());

                    // Kiểm tra xem có đơn thuốc không
                    Prescription existingPrescription = PrescriptionDAO.getByMedicalRecordId(record.getId());
                    if (existingPrescription != null) {
                        link.setText("Xem đơn");
                    } else {
                        link.setText("Kê đơn");
                    }
                    setGraphic(link);

                    // Check nếu dòng được chọn thì đổi màu chữ
                    TableRow<?> currentRow = getTableRow();
                    if(currentRow != null && currentRow.isSelected()) {
                        link.setStyle("-fx-text-fill: #fff");
                    } else {
                        link.setStyle("-fx-text-fill: #2a73ff;");
                    }

                    // Bắt sự kiện thay đổi chọn dòng
                    currentRow.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if(isNowSelected) {
                            link.setStyle("-fx-text-fill: #fff");
                        } else {
                            link.setStyle("-fx-text-fill: #2a73ff;");
                        }
                    });
                }
            }
        });

        noteColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getNotes()));

        exportBtn.getItems().addAll("Excel (.xlsx)", "Word (.docx)", "PDF (.pdf)");


        exportBtn.setOnMouseClicked(event -> {
            exportBtn.setValue(null);
        });

        patientSearchField.setEditable(false);
//        exportBtn.setOnAction(event -> {
//            String selectedFormat = exportBtn.getValue();
//            handleExport(selectedFormat);
//        });

//        Load bảng ngay khi mở form
        refreshTable();

        // Xử lý tìm kiếm
        recordSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRecords(newValue.trim().toLowerCase());
        });

    }

    @FXML
    void add(ActionEvent event) {
        try {
            if(selectedPatient == null || symptomField.getText().isEmpty() || diagnoseField.getText().isEmpty()
                    || treatmentField.getText().isEmpty() || consultationDateField.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi","Vui lòng điền đầy đủ thông tin!");
                return;
            }

            String symptom = symptomField.getText();
            String diagnosis = diagnoseField.getText();
            String treatment = treatmentField.getText();
            String note = noteField.getText();
            LocalDate consultationDate = consultationDateField.getValue();

            MedicalRecord record = new MedicalRecord();
            Doctor selectedDoctor = LoginController.loggedInDoctor;
            record.setPatient(selectedPatient);
            record.setDoctor(selectedDoctor);
            record.setSymptoms(symptom);
            record.setDiagnosis(diagnosis);
            record.setTreatmentMethod(treatment);
            record.setNotes(note);
            record.setConsultationDate(consultationDate);

            medicalRecordDAO.saveMedicalRecord(record);
            showAlert(Alert.AlertType.INFORMATION, "Thông báo!", "Thêm hồ sơ bệnh án thành công!");
            refreshTable();

            selectedPatient = null;
            patientSearchField.clear();
            symptomField.clear();
            diagnoseField.clear();
            treatmentField.clear();
            noteField.clear();
            consultationDateField.setValue(null);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void update(ActionEvent event) {
        try {
            MedicalRecord selectedMedicalRecord = recordTable.getSelectionModel().getSelectedItem();
            if (selectedMedicalRecord == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi","Vui lòng chọn bệnh nhân cần sửa.");
                return;
            }

            String symptom = symptomField.getText();
            String diagnosis = diagnoseField.getText();
            String treatment = treatmentField.getText();
            String note = noteField.getText();
            LocalDate consultationDate = consultationDateField.getValue();

            if(selectedPatient == null || symptom.isEmpty() || diagnosis.isEmpty()
                    || treatment.isEmpty() || consultationDate == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi","Vui lòng điền đầy đủ thông tin!");
                return;
            }

            selectedMedicalRecord.setSymptoms(symptom);
            selectedMedicalRecord.setDiagnosis(diagnosis);
            selectedMedicalRecord.setTreatmentMethod(treatment);
            selectedMedicalRecord.setNotes(note);
            selectedMedicalRecord.setConsultationDate(consultationDate);

            medicalRecordDAO.updateMedicalRecord(selectedMedicalRecord);
            showAlert(Alert.AlertType.INFORMATION, "Thông báo!", "Cập nhật hồ sơ bệnh án thành công!");
            refreshTable();

            selectedPatient = null;
            patientSearchField.clear();
            symptomField.clear();
            diagnoseField.clear();
            treatmentField.clear();
            noteField.clear();
            consultationDateField.setValue(null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @FXML
    void delete(ActionEvent event) {
        MedicalRecord selectedRecord = recordTable.getSelectionModel().getSelectedItem();
        if(selectedRecord == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi!", "Vui lòng chọn hồ sơ bệnh án để xóa!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn xóa hồ sơ bệnh án này?");
        confirmAlert.setContentText(null);

        confirmAlert.showAndWait().ifPresent(response -> {
           if(response == ButtonType.OK) {
               medicalRecordDAO.deleteMedicalRecord(selectedRecord.getId());
               showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xóa hồ sơ bệnh án thành công!");
               refreshTable();
           }
        });
    }

    @FXML
    void exportFile(ActionEvent event) {

    }

    @FXML
    void refresh(ActionEvent event) {
        refreshTable();
        showAlert(Alert.AlertType.INFORMATION, "Thông báo!", "Đã làm mới toàn bộ danh sách!");
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
            nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

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
                        patientSearchField.setText(selected.getName()); // set tên bệnh nhân vào text field
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
                                          p.getName().toLowerCase().contains(newText.toLowerCase()))
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

    @FXML
    void searchRecord(ActionEvent event) {

    }

    private void filterRecords(String keyword) {
        if(keyword.isEmpty()) {
            refreshTable();
            return;
        }

        List<MedicalRecord> doctorRecords = medicalRecordDAO.getMedicalRecordsByDoctor(LoginController.loggedInDoctor.getId());

        List<MedicalRecord> filtered = doctorRecords.stream()
                .filter(record -> {
                   Patient patient = record.getPatient();
                   String combined = (
                           (patient != null ? patient.getName() : "") + " " +
                                   record.getSymptoms() + " " +
                                   record.getDiagnosis() + " " +
                                   record.getTreatmentMethod() + " " +
                                   record.getNotes() + " " +
                                   (record.getConsultationDate() != null ? record.getConsultationDate() : "") + " " +
                                   record.getId()
                           ).toLowerCase();

                   return combined.contains(keyword);
                }).collect(Collectors.toList());
        recordTable.getItems().setAll(filtered);
    }

    private void openPrescriptionForm(int recordId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/PrescriptionForm.fxml"));
            Parent root = loader.load();
            PrescriptionFormController controller = loader.getController();

            MedicalRecord record = medicalRecordDAO.getMedicalRecordById(recordId);
            Patient patient = record.getPatient();

            controller.setPatient(patient);
            controller.setMedicalRecord(record);

            //Kiểm tra nếu đã có đơn thuốc => truyền vào
            Prescription existingPrescription = PrescriptionDAO.getByMedicalRecordId(recordId);
            if (existingPrescription != null) {
                controller.setExistingPrescription(existingPrescription);
            }
//            else {
//                controller.setMedicalRecord(record); // dùng nếu tạo đơn mới cần medicalRecord
//            }

            Stage stage = new Stage();
            stage.setTitle("Kê đơn thuốc");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void openPatientDetail(Patient patient) {
        if (patient == null) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedBirthDate = patient.getBirthdate().format(formatter);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin bệnh nhân");
        alert.setHeaderText(patient.getName());
        alert.setContentText(
                "ID: " + patient.getId() + "\n" +
                        "Tên: " + patient.getName() + "\n" +
                        "Giới tính: " + patient.getGender() + "\n" +
                        "Ngày sinh: " + formattedBirthDate + "\n" +
                        "Địa chỉ: " + patient.getAddress() + "\n" +
                        "Email: " + patient.getEmail() + "\n" +
                        "SĐT: " + patient.getPhone()
        );
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void refreshTable() {
        int doctorId = LoginController.loggedInDoctor.getId();
        recordTable.getItems().clear();
        recordTable.getItems().addAll(medicalRecordDAO.getMedicalRecordsByDoctor(doctorId));
    }
}
