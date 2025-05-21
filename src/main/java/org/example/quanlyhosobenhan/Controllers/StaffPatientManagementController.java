package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.Patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StaffPatientManagementController {

    @FXML
    private TableColumn<Patient, String> addressColumn;

    @FXML
    private TextArea addressField;

    @FXML
    private TableColumn<Patient, String> dobColumn;

    @FXML
    private DatePicker dobField;

    @FXML
    private TableColumn<Patient, String> emailColumn;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField fullNameField;

    @FXML
    private TableColumn<Patient, Patient.Gender> genderColumn;

    @FXML
    private ComboBox<Patient.Gender> genderField;

    @FXML
    private TableColumn<Patient, Integer> idColumn;

    @FXML
    private TableColumn<Patient, String> nameColumn;

    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, String> phoneNumberColumn;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField searchTextField;

    @FXML
    private DatePicker startDatePicker;


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
                -> new SimpleStringProperty(cellData.getValue().getFullName()));
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

        genderField.getItems().setAll(Patient.Gender.values());

        refreshTable();

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

        // Xử lý tìm kiếm
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRecordsCombined(newValue);
        });

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

        patientTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
                if (selectedPatient != null) {
                    showPatientDetails(selectedPatient);
                }
            }
        });

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fullNameField.setText(newSelection.getFullName());
                addressField.setText(newSelection.getAddress());
                emailField.setText(newSelection.getEmail());
                phoneNumberField.setText(newSelection.getPhone());
                dobField.setValue(newSelection.getBirthdate());
                genderField.setValue(newSelection.getGender());
            }
        });

    }

    private void showPatientDetails(Patient selectedPatient) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Thông tin chi tiết bệnh nhân");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setPrefWidth(500);

        Function<String, Label> createWrappedLabel = (text) -> {
            Label label = new Label(text);
            label.setWrapText(true);
            if (text.length() > 50) {
                Tooltip tooltip = new Tooltip(text);
                tooltip.setWrapText(true);
                tooltip.setMaxWidth(400);
                Tooltip.install(label, tooltip);
            }
            return label;
        };

        vbox.getChildren().addAll(
                createWrappedLabel.apply("🆔 ID: " + selectedPatient.getId()),
                createWrappedLabel.apply("👤 Họ tên: " + selectedPatient.getFullName()),
                createWrappedLabel.apply("🚻 Giới tính: " + selectedPatient.getGender()),
                createWrappedLabel.apply("🎂 Ngày sinh: " + selectedPatient.getBirthdate().format(VIETNAMESE_DATE_FORMATTER)),
                createWrappedLabel.apply("🏠 Địa chỉ: " + selectedPatient.getAddress()),
                createWrappedLabel.apply("📧 Email: " + selectedPatient.getEmail()),
                createWrappedLabel.apply("📞 Số điện thoại: " + selectedPatient.getPhone()),
                createWrappedLabel.apply("🆔 Số CCCD: " + selectedPatient.getNationalId()),
                createWrappedLabel.apply("💳 Số thẻ BHYT: " + selectedPatient.getHealthInsuranceId())
        );

        Button closeBtn = new Button("Đóng");
        closeBtn.setOnAction(e -> detailsStage.close());

        HBox buttonBox = new HBox(closeBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        vbox.getChildren().add(buttonBox);

        Scene scene = new Scene(vbox);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    @FXML
    void update(ActionEvent event) {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi","Vui lòng chọn bệnh nhân cần cập nhật.");
            return;
        }

        if(fullNameField.getText().isEmpty() || addressField.getText().isEmpty() || emailField.getText().isEmpty()
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

        selectedPatient.setFullName(fullNameField.getText());
        selectedPatient.setAddress(addressField.getText());
        selectedPatient.setEmail(emailField.getText());
        selectedPatient.setPhone(phoneNumberField.getText());
        selectedPatient.setBirthdate(dobField.getValue());
        selectedPatient.setGender(genderField.getSelectionModel().getSelectedItem());

        patientDAO.updatePatient(selectedPatient);
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Sửa thông tin thành công!");
        refreshTable();

        patientTable.getSelectionModel().clearSelection();

        fullNameField.clear();
        addressField.clear();
        emailField.clear();
        phoneNumberField.clear();
        dobField.setValue(null);
        genderField.getSelectionModel().clearSelection();

    }

//    @FXML
//    void delete(ActionEvent event) {
//        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
//        if(selectedPatient == null) {
//            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn bệnh nhân cần xóa!");
//            return;
//        }
//        patientDAO.deletePatient(selectedPatient.getId());
//        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đã xóa thành công!");
//        refreshTable();
//    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "0\\d{9}";
        return phoneNumber != null && phoneNumber.matches(regex);
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void refreshTable() {
        patientTable.getItems().clear();
        patientTable.getItems().addAll(patientDAO.getAllPatient());
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
                                    (patient != null ? patient.getFullName() : "") + " " +
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
