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
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Model.Patient;

import java.time.LocalDate;

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

        treatmentColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getTreatmentMethod()));

        prescriptionColumn.setCellFactory(col -> new TableCell<>() {
                private final Hyperlink link = new Hyperlink("Kê đơn");

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
                if(empty) {
                    setGraphic(null);
                } else {
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
    void update(ActionEvent event) {

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

    private void openPrescriptionForm(int recordId) {
        System.out.println("Mở form kê đơn cho hồ sơ có ID: " + recordId);
    }

    private void openPatientDetail(Patient patient) {
        if (patient == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin bệnh nhân");
        alert.setHeaderText(patient.getName());
        alert.setContentText(
                "ID: " + patient.getId() + "\n" +
                        "Tên: " + patient.getName() + "\n" +
                        "Giới tính: " + patient.getGender() + "\n" +
                        "Ngày sinh: " + patient.getBirthdate() + "\n" +
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
