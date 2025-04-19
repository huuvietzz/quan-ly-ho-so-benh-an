package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PrescriptionDAO;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Model.Patient;
import org.example.quanlyhosobenhan.Model.Prescription;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalRecordController {

    @FXML
    private TableColumn<MedicalRecord, LocalDate> consultationDateColumn;

    @FXML
    private TableColumn<MedicalRecord, String> diagnoseColumn;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<MedicalRecord, Integer> idRecordColumn;

    @FXML
    private TableColumn<MedicalRecord, String> noteColumn;

    @FXML
    private TableColumn<MedicalRecord, String> patientColumn;

    @FXML
    private TableColumn<MedicalRecord, Void> prescriptionColumn;

    @FXML
    private TableView<MedicalRecord> recordTable;

    @FXML
    private TextField searchTextField;

    @FXML
    private AnchorPane medicalRecordPane;

    @FXML
    private TableColumn<MedicalRecord, String> symptomColumn;

    @FXML
    private TableColumn<MedicalRecord, String> treatmentColumn;

    private MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    @FXML
    void initialize() {
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
        setupEllipsisColumn(symptomColumn, "Chi tiết triệu chứng");

        diagnoseColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDiagnosis()));
        setupEllipsisColumn(diagnoseColumn, "Chi tiết chẩn đoán");

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
        setupEllipsisColumn(treatmentColumn, "Chi tiết pp điều trị");

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
                    link.setText("Xem đơn");
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
        setupEllipsisColumn(noteColumn, "Chi tiết ghi chú");

        // Load bảng ngay khi mở form
        refreshTable();

        // Xử lý tìm kiếm
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRecordsCombined(newValue);
        });

        // Xu ly chon khoang ngay sinh
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            filterRecordsCombined(searchTextField.getText());
        });
        endDatePicker.valueProperty().addListener((obs, oldDate, newDate) ->{
            filterRecordsCombined(searchTextField.getText());
        });
    }

    // Hàm tìm kiếm kết hợp giữa tìm kiếm và chọn khoảng ngày sinh
    private void filterRecordsCombined(String keyword) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<MedicalRecord> allRecords = medicalRecordDAO.getMedicalRecordsByDoctor(LoginController.loggedInDoctor.getId());

        List<MedicalRecord> filtered = allRecords.stream()
                .filter(record -> {
                    LocalDate consultationDate  = record.getConsultationDate();
                    if(consultationDate  == null) return false;

                    // Lọc theo khoảng ngày được chọn
                    boolean afterOrEqualStart = (startDate == null || !consultationDate .isBefore(startDate)); // consultationDate >= startDate
                    boolean beforeOrEqualEnd = (endDate == null || !consultationDate .isAfter(endDate)); // consultationDate <= endDate
                    if(!(afterOrEqualStart && beforeOrEqualEnd))  return false;

                    // Lọc theo từ khóa nếu có
                    if(keyword != null && !keyword.trim().isEmpty()) {
                        Patient patient = record.getPatient();
                        String combined = (
                                (patient != null ? patient.getName() : "") + " " +
                                        record.getSymptoms() + " " +
                                        record.getDiagnosis() + " " +
                                        record.getTreatmentMethod() + " " +
                                        record.getNotes() + " " +
                                        consultationDate.toString() + " " +
                                        record.getId()
                        ).toLowerCase();

                        return combined.contains(keyword.toLowerCase());
                    }
                    return true; // Neu ko có từ khó, chỉ lọc theo ngày
                }).collect(Collectors.toList());

        recordTable.getItems().setAll(filtered);
    }

    // Hàm giúp hiển thị nội dung khi nội dung vuot quá chieu dài của cột
    private void setupEllipsisColumn(TableColumn<MedicalRecord, String> column, String dialogTitle) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    setOnMouseClicked(null);
                    setStyle(""); // Reset lại style nếu ô rỗng
                } else {
                    // Đo chiều rộng văn bản thực tế
                    Text text = new Text(item);
                    text.setFont(getFont());
                    double textWidth = text.getLayoutBounds().getWidth();

                    //Chiều rộng cột - padding (khoảng 10px mặc định)
                    double cellWidth = column.getWidth() - 10;

                    // Nếu quá rộng, cắt dần và thêm "..."
                    if(textWidth > cellWidth) {
                        String shortened = item;
                        text.setText(shortened);
                        while(text.getLayoutBounds().getWidth() > cellWidth - 10 && shortened.length() > 0) {
                            shortened = shortened.substring(0, shortened.length() - 1);
                            text.setText(shortened + "...");
                        }
                        setText(shortened + "...");
                        setTooltip(new Tooltip("Bấm để xem chi tiết"));
                        setStyle("-fx-cursor: hand;");
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 1 && !isEmpty()) {
                                showDetailDialog(dialogTitle, item);
                            }
                        });
                    } else {
                        // Nếu ngắn, hiển thị bình thường, không cần tooltip hay click
                        setText(item);
                        setTooltip(null);
                        setOnMouseClicked(null);
                        setStyle("");
                    }
                }
            }
        });
    }

    private void showDetailDialog(String title, String content) {
        Stage dialog = new Stage();
        dialog.setTitle(title);

        TextArea textArea = new TextArea(content);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefSize(400, 200);

        Button closeButton = new Button("Đóng");
        closeButton.setOnAction(e -> dialog.close());

        VBox vbox = new VBox(10, textArea, closeButton);
        vbox.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.initOwner(recordTable.getScene().getWindow());
        dialog.show();
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

    private void openPrescriptionForm(int recordId) {
        try {
            Prescription existingPrescription = PrescriptionDAO.getByMedicalRecordId(recordId);
            if (existingPrescription == null) {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Chưa có đơn thuốc cho hồ sơ này.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/PrescriptionForm.fxml"));
            Parent root = loader.load();
            PrescriptionFormController controller = loader.getController();

            MedicalRecord record = medicalRecordDAO.getMedicalRecordById(recordId);
            Patient patient = record.getPatient();

            controller.setPatient(patient);
            controller.setMedicalRecord(record);
            controller.setExistingPrescription(existingPrescription);

            // Chế độ chỉ xem
            controller.setReadOnly(true);

            Stage stage = new Stage();
            stage.setTitle("Kê đơn thuốc");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch(IOException e){
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

    private void refreshTable() {
        int doctorId = LoginController.loggedInDoctor.getId();
        recordTable.getItems().clear();
        recordTable.getItems().addAll(medicalRecordDAO.getMedicalRecordsByDoctor(doctorId));
    }

    @FXML
    void printMedicalRecord(ActionEvent event) {

    }
}
