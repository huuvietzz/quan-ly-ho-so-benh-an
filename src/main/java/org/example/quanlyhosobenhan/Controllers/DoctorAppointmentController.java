package org.example.quanlyhosobenhan.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import org.example.quanlyhosobenhan.Dao.AppointmentDAO;
import org.example.quanlyhosobenhan.Model.Appointment;
import org.example.quanlyhosobenhan.Model.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DoctorAppointmentController {

    @FXML
    private TableView<Appointment> appointmentTable;

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

    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public static final DateTimeFormatter VIETNAMESE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
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

        refreshTable();
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

    private void refreshTable() {
        appointmentTable.getItems().clear();
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDoctorId(LoginController.loggedInDoctor.getId());
        appointmentTable.getItems().addAll(appointments);
    }
}
