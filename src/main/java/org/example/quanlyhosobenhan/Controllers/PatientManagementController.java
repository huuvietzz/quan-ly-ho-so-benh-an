package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.Patient;

// Excel
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFRow;

// Word
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFTable;
//import org.apache.poi.xwpf.usermodel.XWPFTableRow;

//import java.io.File;
//import java.io.FileOutputStream;
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

        // Chon mac dinh la excel
//        exportBtn.getSelectionModel().selectFirst();
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
//        String selectedFormat = exportBtn.getSelectionModel().getSelectedItem();
//
//        switch (selectedFormat) {
//            case "Excel (.xlsx)":
//                exportToExcel();
//                break;
//            case "Word (.docx)":
//                exportToWord();
//                break;
//            case "PDF (.pdf)":
//                exportToPDF();
//                break;
//            default:
//                showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa chọn định dạng xuất!");
//                break;
//        }
    }
//
//    private void exportToExcel() {
//        try {
//            // Tạo workbook và sheet
//            XSSFWorkbook workbook = new XSSFWorkbook();
//            XSSFSheet sheet = workbook.createSheet("Patients");
//
//            // Tạo tiêu đề
//            XSSFRow headerRow = sheet.createRow(0);
//            headerRow.createCell(0).setCellValue("ID");
//            headerRow.createCell(1).setCellValue("Name");
//            headerRow.createCell(2).setCellValue("Gender");
//            headerRow.createCell(3).setCellValue("DOB");
//            headerRow.createCell(4).setCellValue("Address");
//            headerRow.createCell(5).setCellValue("Email");
//            headerRow.createCell(6).setCellValue("Phone Number");
//
//            // Thêm dữ liệu vào sheet
//            int rowNum = 1;
//            for (Patient patient : patientTable.getItems()) {
//                XSSFRow row = sheet.createRow(rowNum++);
//                row.createCell(0).setCellValue(patient.getId());
//                row.createCell(1).setCellValue(patient.getName());
//                row.createCell(2).setCellValue(patient.getGender().toString());
//                row.createCell(3).setCellValue(patient.getBirthdate().format(VIETNAMESE_DATE_FORMATTER));
//                row.createCell(4).setCellValue(patient.getAddress());
//                row.createCell(5).setCellValue(patient.getEmail());
//                row.createCell(6).setCellValue(patient.getPhone());
//            }
//
//            // Ghi file Excel
//            File file = new File("patients.xlsx");
//            FileOutputStream fileOut = new FileOutputStream(file);
//            workbook.write(fileOut);
//            fileOut.close();
//            workbook.close();
//            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xuất file Excel thành công!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất file Excel!");
//        }
//    }
//
//    private void exportToWord() {
//        try {
//            XWPFDocument document = new XWPFDocument();
//
//            // Tạo bảng trong Word
//            XWPFTable table = document.createTable();
//            XWPFTableRow headerRow = table.getRow(0);
//            headerRow.getCell(0).setText("ID");
//            headerRow.addNewTableCell().setText("Name");
//            headerRow.addNewTableCell().setText("Gender");
//            headerRow.addNewTableCell().setText("DOB");
//            headerRow.addNewTableCell().setText("Address");
//            headerRow.addNewTableCell().setText("Email");
//            headerRow.addNewTableCell().setText("Phone Number");
//
//            // Thêm dữ liệu vào bảng
//            for (Patient patient : patientTable.getItems()) {
//                XWPFTableRow row = table.createRow();
//                row.getCell(0).setText(String.valueOf(patient.getId()));
//                row.getCell(1).setText(patient.getName());
//                row.getCell(2).setText(patient.getGender().toString());
//                row.getCell(3).setText(patient.getBirthdate().format(VIETNAMESE_DATE_FORMATTER));
//                row.getCell(4).setText(patient.getAddress());
//                row.getCell(5).setText(patient.getEmail());
//                row.getCell(6).setText(patient.getPhone());
//            }
//
//            // Ghi file Word
//            File file = new File("patients.docx");
//            FileOutputStream fileOut = new FileOutputStream(file);
//            document.write(fileOut);
//            fileOut.close();
//            document.close();
//            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xuất file Word thành công!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất file Word!");
//        }
//    }
//
//    private void exportToPDF() {
//        try {
//            // Tạo tài liệu PDF
//            Document document = new Document();
//            PdfWriter.getInstance(document, new FileOutputStream("patients.pdf"));
//            document.open();
//
//            // Tạo bảng trong PDF
//            PdfPTable table = new PdfPTable(7); // 7 cột
//            table.addCell("ID");
//            table.addCell("Name");
//            table.addCell("Gender");
//            table.addCell("DOB");
//            table.addCell("Address");
//            table.addCell("Email");
//            table.addCell("Phone Number");
//
//            // Thêm dữ liệu vào bảng
//            for (Patient patient : patientTable.getItems()) {
//                table.addCell(String.valueOf(patient.getId()));
//                table.addCell(patient.getName());
//                table.addCell(patient.getGender().toString());
//                table.addCell(patient.getBirthdate().format(VIETNAMESE_DATE_FORMATTER));
//                table.addCell(patient.getAddress());
//                table.addCell(patient.getEmail());
//                table.addCell(patient.getPhone());
//            }
//
//            // Thêm bảng vào tài liệu PDF
//            document.add(table);
//            document.close();
//            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xuất file PDF thành công!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất file PDF!");
//        }
//    }

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
}
