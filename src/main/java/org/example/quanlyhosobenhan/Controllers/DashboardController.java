package org.example.quanlyhosobenhan.Controllers;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.quanlyhosobenhan.Dao.AppointmentDAO;
import org.example.quanlyhosobenhan.Dao.MedicalRecordDAO;
import org.example.quanlyhosobenhan.Dao.PatientDAO;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.Patient;

import java.util.Map;


public class DashboardController {

    @FXML
    private Label totalMedicalRecords;

    @FXML
    private Label totalPatients;

    @FXML
    private Label totalAppointments;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label avatarLabel;

    @FXML
    private Circle avatarCircle;

    @FXML
    private StackPane userAvatar;

    PatientDAO patientDAO = new PatientDAO();
    MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    AppointmentDAO appointmentDAO = new AppointmentDAO();

    @FXML
    public void initialize() {
        loadCard();
        loadChart();
        getNameAccount();
    }

    public void loadCard(){
        Long patientCount = patientDAO.countByDoctorId(LoginController.loggedInDoctor.getId());
        Long recordCount = medicalRecordDAO.countByDoctorId(LoginController.loggedInDoctor.getId());
        Long appointmentCount = appointmentDAO.countByDoctorId(LoginController.loggedInDoctor.getId());

        totalPatients.setText(String.valueOf(patientCount));
        totalMedicalRecords.setText(String.valueOf(recordCount));
        totalAppointments.setText(String.valueOf(appointmentCount));
    }

    public void loadChart(){
        Map<Patient.Gender, Long> counts = patientDAO.getPatientCountByGender(LoginController.loggedInDoctor.getId());
        Long soNam = counts.getOrDefault(Patient.Gender.Nam, 0L);
        Long soNu = counts.getOrDefault(Patient.Gender.Nữ, 0L);
        Long soKhac = counts.getOrDefault(Patient.Gender.Khác, 0L);
        Long tong = soNam + soNu + soKhac;

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Nam", soNam),
                new PieChart.Data("Nữ", soNu),
                new PieChart.Data("Khác", soKhac)
        );
        pieChart.setData(pieChartData);
        pieChart.setTitle("Tỷ lệ giới tính bệnh nhân");
         
        // Cap nhat ten  de hien thi %
        for(PieChart.Data data : pieChartData){
            double percent = (data.getPieValue() / tong) * 100;
            data.setName(String.format("%s - %.1f%%", data.getName(), percent));

            // Khi rê chuột vào từng phần
            Tooltip tooltip = new Tooltip((int)data.getPieValue() + " bệnh nhân");
            Tooltip.install(data.getNode(), tooltip);

            // Tạo hiệu ứng DropShadow (nổi khối 3D)
            DropShadow shadow = new DropShadow();
            shadow.setRadius(10);
            shadow.setOffsetX(0);
            shadow.setOffsetY(0);
            shadow.setColor(Color.rgb(0, 0, 0, 0.3));

            // Khi rê chuột vào
            data.getNode().setOnMouseEntered(e -> {
                // Scale mượt mà
                ScaleTransition st = new ScaleTransition(Duration.millis(200), data.getNode());
                st.setToX(1.1);
                st.setToY(1.1);
                st.play();

                // Hiệu ứng nổi
                data.getNode().setEffect(shadow);
            });

            // Khi rời chuột ra
            data.getNode().setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), data.getNode());
                st.setToX(1);
                st.setToY(1);
                st.play();

                // Xoá hiệu ứng nổi
                data.getNode().setEffect(null);
            });
        }
    }

    public void getNameAccount() {
        Doctor doctor = LoginController.loggedInDoctor;
        String userName = doctor.getUserName();
        String initial = userName.trim().substring(0, 1);
        avatarLabel.setText(initial);
        Tooltip.install(userAvatar, new Tooltip(doctor.getUserName()));
    }
}
