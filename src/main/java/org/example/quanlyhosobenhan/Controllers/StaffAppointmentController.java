package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.AppointmentDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StaffAppointmentController {

    @FXML
    private TableColumn<Appointment, String> doctorColumn;

    @FXML
    private TableColumn<Appointment, Integer> idAppointmentColumn;

    @FXML
    private TableColumn<Appointment, String> patientColumn;

    @FXML
    private TableColumn<Appointment, String> reasonColumn;

    @FXML
    private TableColumn<Appointment, String> roomNumberColumn;

    @FXML
    private TableColumn<Appointment, String> statusColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> timeColumn;

    @FXML
    private DatePicker dateAppointment;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private Label avatarLabel;

    @FXML
    private StackPane userAvatar;


    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public static final DateTimeFormatter VIETNAMESE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        getNameAccount();

        appointmentTable.setPlaceholder(new Label("❌ Không có cuộc hẹn nào."));
        appointmentTable.setEditable(true);

        idAppointmentColumn.setCellValueFactory(cellData
                -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        patientColumn.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue().getPatient();
            return new SimpleStringProperty(patient != null ? patient.getFullName() : "");
        });

        patientColumn.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();

            {
                link.setUnderline(true);
                link.setFocusTraversable(false);
                link.setOnAction((ActionEvent event) -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    Patient patient = appointment.getPatient();
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
                    TableRow<?> currentRow = getTableRow();
                    if (currentRow != null && currentRow.isSelected()) {
                        link.setStyle("-fx-text-fill: #fff;");
                    } else {
                        link.setStyle("-fx-text-fill: #2a73ff;");
                    }

                    currentRow.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if (isNowSelected) {
                            link.setStyle("-fx-text-fill: #fff;");
                        } else {
                            link.setStyle("-fx-text-fill: #2a73ff;");
                        }
                    });
                }
            }
        });

        doctorColumn.setCellValueFactory(cellData -> {
            Doctor doctor = cellData.getValue().getDoctor();
            return new SimpleStringProperty(doctor != null ? doctor.getFullName() : "");
        });


        doctorColumn.setCellFactory(cellData -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();

            {
                link.setUnderline(true);
                link.setFocusTraversable(false);
                link.setOnAction((ActionEvent event) -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    Doctor doctor = appointment.getDoctor();
                    openDoctorDetail(doctor);
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
                    if (currentRow != null && currentRow.isSelected()) {
                        link.setStyle("-fx-text-fill: #fff");
                    } else {
                        link.setStyle("-fx-text-fill: #2a73ff;");
                    }

                    // Bắt sự kiện thay đổi chọn dòng
                    currentRow.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if (isNowSelected) {
                            link.setStyle("-fx-text-fill: #fff");
                        } else {
                            link.setStyle("-fx-text-fill: #2a73ff;");
                        }
                    });
                }
            }
        });


        reasonColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getReason()));

        roomNumberColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoomNumber()));

        roomNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        roomNumberColumn.setOnEditCommit(event -> {
            Appointment appointment = event.getRowValue();
            appointment.setRoomNumber(event.getNewValue());
        });

        statusColumn.setCellValueFactory(cellData -> {
            Appointment.Status status = cellData.getValue().getStatus();
            String displayText;

            switch (status) {
                case Pending -> displayText = "Đang chờ";
                case Confirmed -> displayText = "Đã xác nhận";
                case Cancelled -> displayText = "Đã hủy";
                default -> displayText = "Đã hoàn thành";
            }

            return new SimpleStringProperty(displayText);
        });

        timeColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getAppointmentTime()));

        timeColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

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

        dateAppointment.setConverter(converter);

        // Xử lý tìm kiếm
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRecordsCombined(newValue);
        });

        // Xu ly chon khoang ngay sinh
        dateAppointment.valueProperty().addListener((obs, oldDate, newDate) -> {
            filterRecordsCombined(searchTextField.getText());
        });

        // Bổ sung xử lý khi người dùng xóa ngày bằng bàn phím rồi rời focus
        dateAppointment.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (!newFocus) { // Khi mất focus
                filterRecordsCombined(searchTextField.getText());
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        appointmentTable.getItems().clear();
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        appointmentTable.getItems().addAll(appointments);
    }

    private void openPatientDetail(Patient patient) {
        if (patient == null) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedBirthDate = patient.getBirthdate().format(formatter);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin bệnh nhân");
        alert.setHeaderText(patient.getFullName());
        alert.setContentText(
                "ID: " + patient.getId() + "\n" +
                        "Tên: " + patient.getFullName() + "\n" +
                        "Giới tính: " + convertGenderToVietnamese(patient.getGender()) + "\n" +
                        "Ngày sinh: " + formattedBirthDate + "\n" +
                        "Địa chỉ: " + patient.getAddress() + "\n" +
                        "Email: " + patient.getEmail() + "\n" +
                        "SĐT: " + patient.getPhone() + "\n" +
                        "Số căn cước công dân: " + patient.getNationalId() + "\n" +
                        "Số thẻ BHYT: " + patient.getHealthInsuranceId()
        );
        alert.showAndWait();
    }

    private String convertGenderToVietnamese(Patient.Gender gender) {
        if (gender == null) return "Không rõ";
        switch (gender) {
            case Male:
                return "Nam";
            case Female:
                return "Nữ";
            case Other:
            default:
                return "Khác";
        }
    }

    private void openDoctorDetail(Doctor doctor) {
        if (doctor == null) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedBirthDate = doctor.getBirthDate() != null ? doctor.getBirthDate().format(formatter) : "Chưa cập nhật";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin bác sĩ");
        alert.setHeaderText(doctor.getFullName());
        alert.setContentText(
                "ID: " + doctor.getId() + "\n" +
                        "Tên: " + doctor.getFullName() + "\n" +
                        "Giới tính: " + convertGenderToVietnamese(doctor.getGender()) + "\n" +
                        "Ngày sinh: " + formattedBirthDate + "\n" +
                        "Địa chỉ: " + (doctor.getAddress() != null ? doctor.getAddress() : "Chưa cập nhật") + "\n" +
                        "Email: " + doctor.getEmail() + "\n" +
                        "SĐT: " + doctor.getPhone() + "\n" +
                        "Phòng ban: " + (doctor.getDepartment() != null ? doctor.getDepartment() : "Chưa cập nhật") + "\n" +
                        "Chuyên môn: " + (doctor.getSpecialization() != null ? doctor.getSpecialization() : "Chưa cập nhật") + "\n" +
                        "Chức vụ: " + (doctor.getPosition() != null ? doctor.getPosition() : "Chưa cập nhật")
        );
        alert.showAndWait();
    }

    private String convertGenderToVietnamese(Doctor.Gender gender) {
        if (gender == null) return "Không rõ";
        switch (gender) {
            case Male:
                return "Nam";
            case Female:
                return "Nữ";
            case Other:
            default:
                return "Khác";
        }
    }

    @FXML
    void confirmBtn(ActionEvent event) {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus(Appointment.Status.Confirmed);
            appointmentDAO.update(selected);
            refreshTable();
        } else {
            showAlert("Vui lòng chọn một cuộc hẹn để xác nhận.");
        }
    }

    @FXML
    void cancelBtn(ActionEvent event) {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus(Appointment.Status.Cancelled);
            appointmentDAO.update(selected);
            refreshTable();
        } else {
            showAlert("Vui lòng chọn một cuộc hẹn để hủy.");
        }
    }

    @FXML
    void deleteBtn(ActionEvent event) {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            appointmentDAO.delete(selected.getId());
            refreshTable();
        } else {
            showAlert("Vui lòng chọn một cuộc hẹn để xóa.");
        }
    }

    private void filterRecordsCombined(String keyword) {
        LocalDate selectedDate = dateAppointment.getValue();

        List<Appointment> allAppointments = appointmentDAO.getAllAppointments();

        List<Appointment> filtered = allAppointments.stream()
                .filter(appointment -> {
                    LocalDateTime appointmentTime = appointment.getAppointmentTime();
                    if (appointmentTime == null) return false;

                    // Lọc theo ngày được chọn
                    if (selectedDate != null) {
                        LocalDate appointmentDate = appointmentTime.toLocalDate();
                        if (!appointmentDate.equals(selectedDate)) {
                            return false;
                        }
                    }

                    // Lọc theo từ khóa nếu có
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        try {
                            // Thử parse thành số để tìm theo ID appointment
                            int appointmentId = Integer.parseInt(keyword);
                            return appointment.getId() == appointmentId;
                        } catch (NumberFormatException e) {
                            // Tìm kiếm theo text trong các trường
                            String patientName = appointment.getPatient() != null ?
                                    appointment.getPatient().getFullName() : "";
                            String doctorName = appointment.getDoctor() != null ?
                                    appointment.getDoctor().getFullName() : "";
                            String reason = appointment.getReason() != null ?
                                    appointment.getReason() : "";
                            String roomNumber = appointment.getRoomNumber() != null ?
                                    appointment.getRoomNumber() : "";

                            // Chuyển đổi status thành text hiển thị
                            String statusText = "";
                            if (appointment.getStatus() != null) {
                                switch (appointment.getStatus()) {
                                    case Pending -> statusText = "Đang chờ";
                                    case Confirmed -> statusText = "Đã xác nhận";
                                    case Cancelled -> statusText = "Đã hủy";
                                    default -> statusText = "Đã hoàn thành";
                                }
                            }

                            // Kết hợp tất cả thông tin để tìm kiếm
                            String combined = (
                                    patientName + " " +
                                            doctorName + " " +
                                            reason + " " +
                                            roomNumber + " " +
                                            statusText + " " +
                                            appointment.getId() + " " +
                                            (appointmentTime != null ?
                                                    appointmentTime.format(VIETNAMESE_DATE_TIME_FORMATTER) : "")
                            ).toLowerCase();

                            return combined.contains(keyword.trim().toLowerCase());
                        }
                    }

                    return true; // Nếu không có từ khóa, chỉ lọc theo ngày
                })
                .collect(Collectors.toList());

        appointmentTable.getItems().setAll(filtered);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void getNameAccount() {
        Staff staff = LoginController.loggedInStaff;
        String userName = staff.getUserName();
        String initial = userName.trim().substring(0, 1);
        avatarLabel.setText(initial);
        Tooltip.install(userAvatar, new Tooltip(staff.getUserName()));
    }

}
