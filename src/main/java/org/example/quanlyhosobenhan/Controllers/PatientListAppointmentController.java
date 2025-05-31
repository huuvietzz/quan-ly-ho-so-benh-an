package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import org.example.quanlyhosobenhan.Dao.AppointmentDAO;
import org.example.quanlyhosobenhan.Model.Appointment;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PatientListAppointmentController {
    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, String> doctorColumn;

    @FXML
    private TableColumn<Appointment, Integer> idAppointmentColumn;

    @FXML
    private TableColumn<Appointment, String> reasonColumn;

    @FXML
    private TableColumn<Appointment, String> roomNumberColumn;

    @FXML
    private TableColumn<Appointment, String> statusColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> timeColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private DatePicker dateAppointment;

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


        doctorColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDoctor().getFullName()));

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

    private void getNameAccount() {
        Patient patient = LoginController.loggedInPatient;
        String userName = patient.getUserName();
        String initial = userName.trim().substring(0, 1);
        avatarLabel.setText(initial);
        Tooltip.install(userAvatar, new Tooltip(patient.getUserName()));
    }

    private void filterRecordsCombined(String keyword) {
        LocalDate selectedDate = dateAppointment.getValue();

        List<Appointment> allAppointmentsByPatient = appointmentDAO.getAppointmentsByPatientId(LoginController.loggedInPatient.getId());

        List<Appointment> filtered = allAppointmentsByPatient.stream()
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

    private void refreshTable() {
        appointmentTable.getItems().clear();
        List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(LoginController.loggedInPatient.getId());
        appointmentTable.getItems().addAll(appointments);
    }
}
