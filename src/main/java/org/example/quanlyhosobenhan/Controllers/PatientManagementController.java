package org.example.quanlyhosobenhan.Controllers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xwpf.usermodel.*;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Model.Patient;


import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PatientManagementController {

    @FXML
    private TableColumn<Patient, String> addressColumn;

    @FXML
    private TableColumn<Patient, String> dobColumn;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableColumn<Patient, String> emailColumn;

    @FXML
    private ComboBox<String> exportBtn;

    @FXML
    private TableColumn<Patient, Patient.Gender> genderColumn;

    @FXML
    private TableColumn<Patient, Integer> idColumn;

    @FXML
    private TableColumn<Patient, String> nameColumn;

    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, String> phoneNumberColumn;

    private static final DateTimeFormatter VIETNAMESE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.forLanguageTag("vi-VN"));

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{3,6}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private PatientDAO patientDAO = new PatientDAO();

    @FXML
    public void initialize() {
        patientTable.setPlaceholder(new Label("❌ Không có bệnh nhân."));

        idColumn.setCellValueFactory(cellData
                -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        nameColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getName()));
        setupEllipsisColumn(nameColumn, "Chi tiết tên");

        genderColumn.setCellValueFactory(cellData
                -> new SimpleObjectProperty<>(cellData.getValue().getGender()));

        dobColumn.setCellValueFactory(cellData -> {
            LocalDate birthdate = cellData.getValue().getBirthdate();
            String formattedDate = birthdate != null ? birthdate.format(VIETNAMESE_DATE_FORMATTER) : "";
            return new SimpleStringProperty(formattedDate);
        });

        addressColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getAddress()));
        setupEllipsisColumn(addressColumn, "Chi tiết địa chỉ");

        emailColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getEmail()));
        setupEllipsisColumn(emailColumn, "Chi tiết email");

        phoneNumberColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getPhone()));

        exportBtn.getItems().addAll("Excel (.xlsx)", "Word (.docx)", "PDF (.pdf)");


        exportBtn.setOnMouseClicked(event -> {
            exportBtn.setValue(null); // Xóa lựa chọn cũ
        });
        exportBtn.setOnAction(event -> {
            String selectedFormat = exportBtn.getValue();
            handleExport(selectedFormat);
        });

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

    @FXML
    void details(ActionEvent event) {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi!", "Vui lòng chọn bệnh nhân!");
            return;
        }

        Stage detailsStage = new Stage();
        detailsStage.setTitle("Thông tin chi tiết bệnh nhân");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label idLabel = new Label("ID: " + selectedPatient.getId());
        Label nameLabel = new Label("Tên: " + selectedPatient.getName());
        Label genderLabel = new Label("Giới tính: " + selectedPatient.getGender());
        Label dobLabel = new Label("Ngày sinh: " + selectedPatient.getBirthdate().format(PatientManagementController.VIETNAMESE_DATE_FORMATTER));
        Label addressLabel = new Label("Địa chỉ: " + selectedPatient.getAddress());
        Label emailLabel = new Label("Email: " + selectedPatient.getEmail());
        Label phoneLabel = new Label("Số điện thoại: " + selectedPatient.getPhone());
        Label nationalIdLabel = new Label("Số căn cước công dân: " + selectedPatient.getNationalId());
        Label healthInsuranceIdLabel = new Label("Số thẻ BHYT: " + selectedPatient.getHealthInsuranceId());

        vbox.getChildren().addAll(idLabel, nameLabel, genderLabel, dobLabel, addressLabel, emailLabel, phoneLabel, nationalIdLabel, healthInsuranceIdLabel);

        Button closeBtn = new Button("Đóng");
        closeBtn.setOnAction(e -> detailsStage.close());

        vbox.getChildren().addAll(closeBtn);

        Scene scene = new Scene(vbox, 400, 300);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    @FXML
    void refresh(ActionEvent event) {
        refreshTable();
        playTableRefreshAnimation();
        showAlert(Alert.AlertType.INFORMATION, "Thông báo!", "Đã làm mới toàn bộ danh sách!");
    }

    private void playTableRefreshAnimation() {
        // Hiệu ứng mờ
        FadeTransition fade = new FadeTransition(Duration.millis(250), patientTable);
        fade.setFromValue(1.0);
        fade.setToValue(0.7);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);

        // Hiệu ứng rung ngang
        TranslateTransition shake = new TranslateTransition(Duration.millis(100), patientTable);
        shake.setFromX(0);
        shake.setByX(5);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);

        // Chạy đồng thời
        ParallelTransition parallel = new ParallelTransition(fade, shake);
        parallel.play();
    }

    @FXML
    void exportFile(ActionEvent event) {
        String selectedFormat = exportBtn.getSelectionModel().getSelectedItem();

        switch (selectedFormat) {
            case "Excel (.xlsx)":
                exportToExcel();
                break;
            case "Word (.docx)":
                exportToWord();
                break;
            case "PDF (.pdf)":
                exportToPDF();
                break;
            default:
                break;
        }
    }

    private void exportToExcel() {
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
                XSSFSheet sheet = workbook.createSheet("Patients");

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

                // Thêm tiêu đề "Danh sách bệnh nhân"
                XSSFRow titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("Danh sách bệnh nhân");
                titleCell.setCellStyle(titleStyle);

                // Merge ô từ cột 0 đến 6 để căn giữa tiêu đề
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

                // Tạo tiêu đề cột
                XSSFRow headerRow = sheet.createRow(1);
                String[] headers = {"ID", "Tên", "Giới tính", "Ngày sinh", "Địa chỉ", "Email", "SĐT"};

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Thêm dữ liệu
                int rowNum = 2; // Dòng bắt đầu sau tiêu đề và header
                for (Patient patient : patientTable.getItems()) {
                    XSSFRow row = sheet.createRow(rowNum++);

                    Cell cell0 = row.createCell(0);
                    cell0.setCellValue(patient.getId());
                    cell0.setCellStyle(leftAlignStyle);

                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(patient.getName());
                    cell1.setCellStyle(leftAlignStyle);

                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(patient.getGender().toString());
                    cell2.setCellStyle(leftAlignStyle);

                    Cell cell3 = row.createCell(3);
                    cell3.setCellValue(patient.getBirthdate().format(VIETNAMESE_DATE_FORMATTER));
                    cell3.setCellStyle(leftAlignStyle);

                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(patient.getAddress());
                    cell4.setCellStyle(leftAlignStyle);

                    Cell cell5 = row.createCell(5);
                    cell5.setCellValue(patient.getEmail());
                    cell5.setCellStyle(leftAlignStyle);

                    Cell cell6 = row.createCell(6);
                    cell6.setCellValue(patient.getPhone());
                    cell6.setCellStyle(leftAlignStyle);
                }

                // Auto-size cho đẹp
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                    int currentWidth = sheet.getColumnWidth(i);
                    if (i == 1 || i == 4 || i == 5) {
                        sheet.setColumnWidth(i, (int) (currentWidth * 1.5));
                    } else {
                        sheet.setColumnWidth(i, (int) (currentWidth * 1.2));
                    }
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


    private void exportToWord() {
        try {
            // Chọn vị trí lưu file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu file Word");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Word Files", "*.docx")
            );
            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                XWPFDocument document = new XWPFDocument();

                // Thêm tiêu đề "Danh sách bệnh nhân" căn giữa
                XWPFParagraph title = document.createParagraph();
                title.setAlignment(ParagraphAlignment.CENTER); // căn giữa
                XWPFRun titleRun = title.createRun();
                titleRun.setText("Danh sách bệnh nhân");
                titleRun.setBold(true); // in đậm tiêu đề
                titleRun.setFontSize(16); // cỡ chữ 16
                titleRun.addBreak(); // xuống dòng sau tiêu đề

                // Tạo bảng trong Word
                XWPFTable table = document.createTable();
                XWPFTableRow headerRow = table.getRow(0);
                String[] headers = {"ID", "Tên", "Giới tính", "Ngày sinh", "Địa chỉ", "Email", "SĐT"};

                // Thêm các cột tiêu đề và in đậm
                for (int i = 0; i < headers.length; i++) {
                    XWPFParagraph paragraph;
                    XWPFRun run;

                    if (i == 0) {
                        paragraph = headerRow.getCell(0).getParagraphs().get(0);
                        run = paragraph.createRun();
                    } else {
                        paragraph = headerRow.addNewTableCell().addParagraph();
                        run = paragraph.createRun();
                    }

                    run.setText(headers[i]);
                    run.setBold(true); // In đậm tiêu đề
                    paragraph.setAlignment(ParagraphAlignment.CENTER); // Căn giữa tiêu đề trong ô
                }

                // Thêm dữ liệu bệnh nhân
                for (Patient patient : patientTable.getItems()) {
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(String.valueOf(patient.getId()));
                    row.getCell(1).setText(patient.getName());
                    row.getCell(2).setText(patient.getGender().toString());
                    row.getCell(3).setText(patient.getBirthdate().format(VIETNAMESE_DATE_FORMATTER));
                    row.getCell(4).setText(patient.getAddress());
                    row.getCell(5).setText(patient.getEmail());
                    row.getCell(6).setText(patient.getPhone());
                }

                // Ghi file
                FileOutputStream fileOut = new FileOutputStream(file);
                document.write(fileOut);
                fileOut.close();
                document.close();

                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xuất file Word thành công!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có file nào được chọn để lưu!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất file Word!");
        }
    }

    private void exportToPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu file PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                // Load font Arial hỗ trợ tiếng Việt
                String fontPath = "src/main/resources/Fonts/arial.ttf";
                PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

                // Thêm tiêu đề căn giữa
                Paragraph title = new Paragraph("DANH SÁCH BỆNH NHÂN")
                        .setFont(font)
                        .setFontSize(18)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20); // cách bảng 20px

                document.add(title); // <-- thêm tiêu đề trước khi add bảng

                // Tạo bảng 7 cột với tỷ lệ width tuỳ chỉnh
                float[] columnWidths = {1f, 6f, 1f, 1f, 6f, 6f, 1f}; // Cột 1,4,5 rộng hơn
                Table table = new Table(columnWidths);


                String[] headers = {"ID", "Tên", "Giới tính", "Ngày sinh", "Địa chỉ", "Email", "SĐT"};
                for (String header : headers) {
                    table.addCell(new com.itextpdf.layout.element.Cell()
                            .add(new Paragraph(header).setFont(font).setBold())); // Dùng font + in đậm tiêu đề
                }

                for (Patient patient : patientTable.getItems()) {
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(patient.getId())).setFont(font)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(patient.getName()).setFont(font)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(patient.getGender().toString()).setFont(font)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(patient.getBirthdate().format(VIETNAMESE_DATE_FORMATTER)).setFont(font)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(patient.getAddress()).setFont(font)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(patient.getEmail()).setFont(font)));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(patient.getPhone()).setFont(font)));
                }

                document.add(table);
                document.close();

                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xuất file PDF thành công!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có file nào được chọn để lưu!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất file PDF!");
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
        patientTable.getItems().clear();
        List<Patient> patients = patientDAO.getPatientsByDoctorId(LoginController.loggedInDoctor.getId());
        patientTable.getItems().addAll(patients);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "0\\d{9}";
        return phoneNumber != null && phoneNumber.matches(regex);
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void handleExport(String format) {
        if (format == null) return;

        if (format.equals("Excel (.xlsx)")) {
            exportToExcel();
        }
        else if (format.equals("Word (.docx)")) {
            exportToWord();
        }
        else if (format.equals("PDF (.pdf)")) {
            exportToPDF();
        }
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
        dialog.initOwner(patientTable.getScene().getWindow());
        dialog.show();
    }

    // Hàm giúp hiển thị nội dung khi nội dung vuot quá chieu dài của cột
    private void setupEllipsisColumn(TableColumn<Patient, String> column, String dialogTitle) {
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

    // Hàm tìm kiếm kết hợp giữa tìm kiếm và chọn khoảng ngày sinh
    private void filterRecordsCombined(String keyword) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        PatientDAO patientDAO = new PatientDAO();
        List<Patient> allRecords = patientDAO.getPatientsByDoctorId(LoginController.loggedInDoctor.getId());

        List<Patient> filtered = allRecords.stream()
                .filter(patient -> {
                    LocalDate dob  = patient.getBirthdate();
                    if(dob  == null) return false;

                    // Lọc theo khoảng ngày được chọn
                    boolean afterOrEqualStart = (startDate == null || !dob .isBefore(startDate)); // consultationDate >= startDate
                    boolean beforeOrEqualEnd = (endDate == null || !dob .isAfter(endDate)); // consultationDate <= endDate
                    if(!(afterOrEqualStart && beforeOrEqualEnd))  return false;

                    // Lọc theo từ khóa nếu có
                    if(keyword != null && !keyword.trim().isEmpty()) {
                        try {
                            int patientId = Integer.parseInt(keyword);
                            return patient.getId() == patientId;
                        } catch(NumberFormatException e) {
                            String combined = (
                                    (patient != null ? patient.getName() : "") + " " +
                                            patient.getGender() + " " +
                                            patient.getAddress() + " " +
                                            patient.getEmail() + " " +
                                            patient.getPhone() + " " +
                                            dob.toString() + " " +
                                            patient.getId()
                            ).toLowerCase();

                            return combined.contains(keyword.trim().toLowerCase());
                        }
                    }
                    return true; // Neu ko có từ khó, chỉ lọc theo ngày
                }).collect(Collectors.toList());

        patientTable.getItems().setAll(filtered);
    }
}
