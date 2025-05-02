package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PrescriptionDAO;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Model.Patient;
import org.example.quanlyhosobenhan.Model.Prescription;

import java.awt.print.Printable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalRecordController {

    @FXML
    private TableColumn<MedicalRecord, LocalDateTime> consultationDateColumn;

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
    private TableColumn<MedicalRecord, String> symptomColumn;

    @FXML
    private TableColumn<MedicalRecord, String> treatmentColumn;

    private MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    public static final DateTimeFormatter VIETNAMESE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    void initialize() {
        recordTable.setPlaceholder(new Label("❌ Không có hồ sơ bệnh án."));

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        consultationDateColumn.setCellFactory(column -> new TableCell<MedicalRecord, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
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

        // Định dạng ngày hiển thị theo dd-MM-yyyy
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(string, dateFormatter);
            }
        };

        startDatePicker.setConverter(converter);
        endDatePicker.setConverter(converter);

        // Xu ly chon khoang ngay sinh
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            filterRecordsCombined(searchTextField.getText());
        });
        endDatePicker.valueProperty().addListener((obs, oldDate, newDate) ->{
            filterRecordsCombined(searchTextField.getText());
        });

        // Bổ sung xử lý khi người dùng xóa ngày bằng bàn phím rồi rời focus
        startDatePicker.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (!newFocus) { // Khi mất focus
                filterRecordsCombined(searchTextField.getText());
            }
        });
        endDatePicker.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (!newFocus) {
                filterRecordsCombined(searchTextField.getText());
            }
        });
    }

    // Hàm tìm kiếm kết hợp giữa tìm kiếm và chọn khoảng ngày sinh
    private void filterRecordsCombined(String keyword) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<MedicalRecord> allRecords = medicalRecordDAO.getMedicalRecordsByDoctor(LoginController.loggedInDoctor.getId());

        List<MedicalRecord> filtered = allRecords.stream()
                .filter(record -> {
                    LocalDateTime consultationDate  = record.getConsultationDate();
                    if(consultationDate  == null) return false;

                    // Chuyển LocalDateTime thành LocalDate để so sánh chỉ theo ngày
                    LocalDate consultationLocalDate = consultationDate.toLocalDate();

                    // Lọc theo khoảng ngày được chọn
                    boolean afterOrEqualStart = (startDate == null || !consultationLocalDate .isBefore(startDate)); // consultationDate >= startDate
                    boolean beforeOrEqualEnd = (endDate == null || !consultationLocalDate .isAfter(endDate)); // consultationDate <= endDate
                    if(!(afterOrEqualStart && beforeOrEqualEnd))  return false;

                    // Lọc theo từ khóa nếu có
                    if(keyword != null && !keyword.trim().isEmpty()) {
                        try {
                            int keywordId = Integer.parseInt(keyword);
                            return record.getId() == keywordId;
                        } catch(NumberFormatException e) {
                            Patient patient = record.getPatient();
                            String combined = (
                                    (patient != null ? patient.getName() : "") + " " +
                                            record.getId() + " " +
                                            record.getSymptoms() + " " +
                                            record.getDiagnosis() + " " +
                                            record.getTreatmentMethod() + " " +
                                            record.getNotes() + " " +
                                            consultationDate + " " +
                                            record.getId()
                            ).toLowerCase();

                            return combined.contains(keyword.trim().toLowerCase());
                        }
                    }
                    return true;
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
                        "SĐT: " + patient.getPhone() + "\n" +
                        "Số căn cước công dân: " + patient.getNationalId() + "\n" +
                        "Số thẻ BHYT: " + patient.getHealthInsuranceId()
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
            stage.setTitle("Đơn thuốc");
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
        ObservableList<MedicalRecord> selectedRecords = recordTable.getSelectionModel().getSelectedItems();

        if (selectedRecords == null || selectedRecords.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn hồ sơ để in.");
            return;
        }

        VBox printContent = new VBox(20); // chứa tất cả hồ sơ
        printContent.setPadding(new Insets(20));
        printContent.setPrefWidth(600);

        // Thêm hồ sơ vào VBox
        for (MedicalRecord record : selectedRecords) {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(8);
            grid.setPadding(new Insets(10));
            grid.setStyle("-fx-background-color: #f9f9f9;");

            int row = 0;
            grid.addRow(row++, new Label("Mã hồ sơ:"), new Label(String.valueOf(record.getId())));
            grid.addRow(row++, new Label("Bệnh nhân:"), new Label(record.getPatient().getName()));
            grid.addRow(row++, new Label("Bác sĩ:"), new Label(record.getDoctor().getName()));
            grid.addRow(row++, new Label("Ngày khám:"), new Label(record.getConsultationDate().format(VIETNAMESE_DATE_TIME_FORMATTER)));

            // Kiểm tra ngày nhập viện và ngày xuất viện có thể null
            String admissionDate = (record.getAdmissionDate() != null)
                    ? record.getAdmissionDate().format(VIETNAMESE_DATE_TIME_FORMATTER) : "Không có";
            String dischargeDate = (record.getDischargeDate() != null)
                    ? record.getDischargeDate().format(VIETNAMESE_DATE_TIME_FORMATTER) : "Không có";

            grid.addRow(row++, new Label("Ngày nhập viện:"), new Label(admissionDate));
            grid.addRow(row++, new Label("Ngày xuất viện:"), new Label(dischargeDate));
            grid.addRow(row++, new Label("Triệu chứng:"), new Label(record.getSymptoms()));
            grid.addRow(row++, new Label("Chẩn đoán:"), new Label(record.getDiagnosis()));
            grid.addRow(row++, new Label("Kết quả khám bệnh:"), new Label(record.getExaminationResult()));
            grid.addRow(row++, new Label("Kết quả điều trị cuối:"), new Label(record.getFinalTreatmentResult()));
            grid.addRow(row++, new Label("Phương pháp điều trị:"), new Label(record.getTreatmentMethod()));

            // Kiểm tra trạng thái đơn thuốc
            String prescriptionStatus = record.getPrescription() != null ? "Đã kê" : "Chưa kê";
            grid.addRow(row++, new Label("Đơn thuốc:"), new Label(prescriptionStatus));

            grid.addRow(row++, new Label("Ghi chú:"), new Label(record.getNotes()));

            VBox container = new VBox(5, grid, new Separator());
            printContent.getChildren().add(container);
        }

        // In nội dung
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(recordTable.getScene().getWindow())) {
            boolean success = job.printPage(printContent);
            if (success) {
                job.endJob();
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "In hồ sơ thành công!");
            } else {
                System.out.println("In thất bại.");
            }
        }
    }

    @FXML
    void exportToExcel(ActionEvent event) {
        try {
            // Chọn vị trí lưu file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu file Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                // Tạo workbook và sheet
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("MedicalRecords");

                // Style căn giữa và in đậm cho tiêu đề chính
                CellStyle titleStyle = workbook.createCellStyle();
                XSSFFont titleFont = workbook.createFont();
                titleFont.setFontHeightInPoints((short) 16);
                titleFont.setBold(true);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);
                titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Style in đậm cho tiêu đề cột
                CellStyle headerStyle = workbook.createCellStyle();
                XSSFFont headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setAlignment(HorizontalAlignment.LEFT);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Style căn trái cho dữ liệu
                CellStyle leftAlignStyle = workbook.createCellStyle();
                leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
                leftAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Thêm tiêu đề "Danh sách hồ sơ bệnh án"
                XSSFRow titleRow = sheet.createRow(0);
                org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("Danh sách hồ sơ bệnh án");
                titleCell.setCellStyle(titleStyle);

                // Merge ô từ cột 0 đến 11 để căn giữa tiêu đề
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));

                // Tạo tiêu đề cột
                XSSFRow headerRow = sheet.createRow(1);
                String[] headers =
                        {
                                "Mã hồ sơ", "Tên bệnh nhân", "Bác sĩ", "Ngày khám", "Ngày nhập viện",
                                "Ngày xuất viện", "Triệu chứng", "Chẩn đoán", "Kết quả khám bệnh",
                                "Kết quả điều trị cuối", "Phương pháp điều trị", "Đơn thuốc", "Ghi chú"
                        };

                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Thêm dữ liệu
                int rowNum = 2; // Dòng bắt đầu sau tiêu đề và header
                for (MedicalRecord record : recordTable.getItems()) {
                    XSSFRow row = sheet.createRow(rowNum++);

                    org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
                    cell0.setCellValue(record.getId());
                    cell0.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
                    cell1.setCellValue(record.getPatient().getName());
                    cell1.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
                    cell2.setCellValue(record.getDoctor().getName());
                    cell2.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
                    cell3.setCellValue(record.getConsultationDate().format(VIETNAMESE_DATE_TIME_FORMATTER));
                    cell3.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
                    cell4.setCellValue(record.getAdmissionDate() != null
                            ? record.getAdmissionDate().format(VIETNAMESE_DATE_TIME_FORMATTER)
                            : "Không có");
                    cell4.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
                    cell5.setCellValue(record.getDischargeDate() != null
                            ? record.getDischargeDate().format(VIETNAMESE_DATE_TIME_FORMATTER)
                            : "Không có");
                    cell5.setCellStyle(leftAlignStyle);


                    org.apache.poi.ss.usermodel.Cell cell6 = row.createCell(6);
                    cell6.setCellValue(record.getSymptoms());
                    cell6.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell7 = row.createCell(7);
                    cell7.setCellValue(record.getDiagnosis());
                    cell7.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell8 = row.createCell(8);
                    cell8.setCellValue(record.getExaminationResult());
                    cell8.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell9 = row.createCell(9);
                    cell9.setCellValue(record.getFinalTreatmentResult());
                    cell9.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell10 = row.createCell(10);
                    cell10.setCellValue(record.getTreatmentMethod());
                    cell10.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell11 = row.createCell(11);
                    cell11.setCellValue(record.getPrescription() != null ? "Đã kê" : "Chưa kê");
                    cell11.setCellStyle(leftAlignStyle);

                    org.apache.poi.ss.usermodel.Cell cell12 = row.createCell(12);
                    cell12.setCellValue(record.getNotes());
                    cell12.setCellStyle(leftAlignStyle);
                }

                // Auto-size cho đẹp
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                    int currentWidth = sheet.getColumnWidth(i);
                    sheet.setColumnWidth(i, (int) (currentWidth * 1.5));
                }

                // Ghi file
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();

                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xuất file Excel thành công!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có file nào được chọn để lưu!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất file Excel!");
        }
    }

    @FXML
    void detail(ActionEvent event) {
        MedicalRecord selectedRecord = recordTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi!", "Vui lòng chọn hồ sơ bệnh án!");
            return;
        }

        Stage detailStage = new Stage();
        detailStage.setTitle("Chi tiết hồ sơ bệnh án");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label idLabel = new Label("ID: " + selectedRecord.getId());
        Label patientLabel = new Label("Bệnh nhân: " + selectedRecord.getPatient().getName());
        Label doctorLabel = new Label("Bác sĩ: " + selectedRecord.getDoctor().getName());
        Label consultationDateLabel = new Label("Ngày khám: " + selectedRecord.getConsultationDate().format(VIETNAMESE_DATE_TIME_FORMATTER));
        Label symptomsLabel = new Label("Triệu chứng: " + selectedRecord.getSymptoms());
        Label diagnosisLabel = new Label("Chẩn đoán: " + selectedRecord.getDiagnosis());
        Label resultLabel = new Label("Kết quả khám bệnh: " + selectedRecord.getExaminationResult());
        Label treatmentLabel = new Label("Phương pháp điều trị: " + selectedRecord.getTreatmentMethod());
        Label finalResultLabel = new Label("Kết quả điều trị cuối: " + selectedRecord.getFinalTreatmentResult());
        Label notesLabel = new Label("Ghi chú: " + selectedRecord.getNotes());
        Label admissionLabel = new Label("Ngày nhập viện: " +
                (selectedRecord.getAdmissionDate() != null ? selectedRecord.getAdmissionDate().format(VIETNAMESE_DATE_TIME_FORMATTER) : "Chưa có"));
        Label dischargeLabel = new Label("Ngày xuất viện: " +
                (selectedRecord.getDischargeDate() != null ? selectedRecord.getDischargeDate().format(VIETNAMESE_DATE_TIME_FORMATTER) : "Chưa có"));

        Button closeBtn = new Button("Đóng");
        closeBtn.setOnAction(e -> detailStage.close());

        vbox.getChildren().addAll(
                idLabel, patientLabel, doctorLabel, consultationDateLabel,
                admissionLabel, dischargeLabel,symptomsLabel, diagnosisLabel,
                resultLabel, treatmentLabel, finalResultLabel, notesLabel,
                closeBtn
        );

        Scene scene = new Scene(vbox, 500, 450);
        detailStage.setScene(scene);
        detailStage.show();
    }
}
