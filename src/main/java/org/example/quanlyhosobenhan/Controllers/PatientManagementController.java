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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.Patient;


import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public class PatientManagementController {

    @FXML
    private Button addBtn;

    @FXML
    private TableColumn<Patient, String> addressColumn;

    @FXML
    private TextField addressField;

    @FXML
    private Button deleteBtn;

    @FXML
    private TableColumn<Patient, String> dobColumn;

    @FXML
    private DatePicker dobField;

    @FXML
    private TableColumn<Patient, String> emailColumn;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> exportBtn;

    @FXML
    private TableColumn<Patient, Patient.Gender> genderColumn;

    @FXML
    private ComboBox<Patient.Gender> genderField;

    @FXML
    private TableColumn<Patient, Integer> idColumn;

    @FXML
    private TableColumn<Patient, String> nameColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, String> phoneNumberColumn;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Button resetBtn;

    @FXML
    private Button updateBtn;

    private static final DateTimeFormatter VIETNAMESE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{3,6}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private PatientDAO patientDAO = new PatientDAO();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        genderColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getGender()));
        dobColumn.setCellValueFactory(cellData -> {
            LocalDate birthdate = cellData.getValue().getBirthdate();
            String formattedDate = birthdate != null ? birthdate.format(VIETNAMESE_DATE_FORMATTER) : "";
            return new SimpleStringProperty(formattedDate);
        });
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        phoneNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));

        genderField.getItems().setAll(Patient.Gender.values());
        exportBtn.getItems().addAll("Excel (.xlsx)", "Word (.docx)", "PDF (.pdf)");


        exportBtn.setOnMouseClicked(event -> {
            exportBtn.setValue(null); // Xóa lựa chọn cũ
        });
        exportBtn.setOnAction(event -> {
            String selectedFormat = exportBtn.getValue();
            handleExport(selectedFormat);
        });

        refreshTable();
    }

    @FXML
    void add(ActionEvent event) {
        if(nameField.getText().isEmpty() || addressField.getText().isEmpty() || emailField.getText().isEmpty()
        || phoneNumberField.getText().isEmpty() || dobField.getValue() == null || genderField.getValue() == null){
             showAlert(Alert.AlertType.ERROR, "Lỗi","Vui lòng điền đầy đủ thông tin!");
             return;
        }

        if(!isValidEmail(emailField.getText()) && !isValidPhoneNumber(phoneNumberField.getText())){
            showAlert(Alert.AlertType.ERROR, "Lỗi","Email và SĐT không hợp lệ! Vui lòng nhập lại!");
            return;
        }

        if(!isValidEmail(emailField.getText())){
            showAlert(Alert.AlertType.ERROR, "Lỗi","Email không hợp lệ! Vui lòng nhập lại!");
            return;
        }

        if(!isValidPhoneNumber(phoneNumberField.getText())){
            showAlert(Alert.AlertType.ERROR, "Lỗi","SĐT không hợp lệ! Vui lòng nhập lại!");
            return;
        }

        String name = nameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();
        LocalDate birthdate = dobField.getValue();
        Patient.Gender gender = genderField.getSelectionModel().getSelectedItem();

        Patient patient = new Patient(name, email, phoneNumber, address, birthdate, gender);
        patientDAO.savePatient(patient);
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Thêm thành công!");
        refreshTable();

        nameField.clear();
        addressField.clear();
        emailField.clear();
        phoneNumberField.clear();
        dobField.setValue(null);
        genderField.getSelectionModel().clearSelection();
    }

    @FXML
    void update(ActionEvent event) {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi","Vui lòng chọn bệnh nhân cần cập nhật.");
            return;
        }

        if(nameField.getText().isEmpty() || addressField.getText().isEmpty() || emailField.getText().isEmpty()
                || phoneNumberField.getText().isEmpty() || dobField.getValue() == null || genderField.getValue() == null){
            showAlert(Alert.AlertType.ERROR, "Lỗi","Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if(!isValidEmail(emailField.getText()) && !isValidPhoneNumber(phoneNumberField.getText())){
            showAlert(Alert.AlertType.ERROR, "Lỗi","Email và SĐT không hợp lệ! Vui lòng nhập lại!");
            return;
        }

        if(!isValidEmail(emailField.getText())){
            showAlert(Alert.AlertType.ERROR, "Lỗi","Email không hợp lệ! Vui lòng nhập lại!");
            return;
        }

        if(!isValidPhoneNumber(phoneNumberField.getText())){
            showAlert(Alert.AlertType.ERROR, "Lỗi","SĐT không hợp lệ! Vui lòng nhập lại!");
            return;
        }

        selectedPatient.setName(nameField.getText());
        selectedPatient.setAddress(addressField.getText());
        selectedPatient.setEmail(emailField.getText());
        selectedPatient.setPhone(phoneNumberField.getText());
        selectedPatient.setBirthdate(dobField.getValue());
        selectedPatient.setGender(genderField.getSelectionModel().getSelectedItem());

        patientDAO.updatePatient(selectedPatient);
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Sửa thông tin thành công!");
        refreshTable();

        nameField.clear();
        addressField.clear();
        emailField.clear();
        phoneNumberField.clear();
        dobField.setValue(null);
        genderField.getSelectionModel().clearSelection();

    }

    @FXML
    void delete(ActionEvent event) {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if(selectedPatient == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn sinh viên cần xóa!");
            return;
        }
        patientDAO.deletePatient(selectedPatient.getId());
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đã xóa thành công!");
        refreshTable();

    }

    @FXML
    void reset(ActionEvent event) {
        patientDAO.deleteAllPatient();
        refreshTable();
        showAlert(Alert.AlertType.INFORMATION, "Thông báo!", "Đã làm mới toàn bộ danh sách!");
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
                String fontPath = "src/main/resources/Fonts/arial.ttf"; // Đảm bảo đúng đường dẫn
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
        patientTable.getItems().addAll(patientDAO.getAllPatient());
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
}
