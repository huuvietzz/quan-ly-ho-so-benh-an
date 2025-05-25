package org.example.quanlyhosobenhan.Controllers;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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
import org.example.quanlyhosobenhan.Model.Staff;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;


public class StaffDashboardController {

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

    @FXML
    private BarChart<String, Number> patientBarChart;

    @FXML
    private LineChart<String, Number> appointmentLineChart;

    PatientDAO patientDAO = new PatientDAO();
    MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    AppointmentDAO appointmentDAO = new AppointmentDAO();

    // Hiệu ứng DropShadow dùng chung
    private final DropShadow hoverShadow = new DropShadow(10, Color.rgb(0, 0, 0, 0.3));


    @FXML
    public void initialize() {
        loadCard();
        loadChart();
        getNameAccount();

        int currentYear = LocalDate.now().getYear();
        loadMonthlyPatientChart(currentYear);
        loadMonthlyAppointmentChart(currentYear);
    }

    public void loadCard(){

        Long patientCount = patientDAO.countAllPatients();
        Long recordCount = medicalRecordDAO.countAllMedicalRecords();
        Long appointmentCount = appointmentDAO.countAllAppointments();

        totalPatients.setText(String.valueOf(patientCount));
        totalMedicalRecords.setText(String.valueOf(recordCount));
        totalAppointments.setText(String.valueOf(appointmentCount));
    }

    public void loadChart(){
        Map<Patient.Gender, Long> counts = patientDAO.countAllPatientsByGender();
        Long soNam = counts.getOrDefault(Patient.Gender.Male, 0L);
        Long soNu = counts.getOrDefault(Patient.Gender.Female, 0L);
        Long soKhac = counts.getOrDefault(Patient.Gender.Other, 0L);
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

    private void loadMonthlyPatientChart(int year) {
        Map<Integer, Long> counts =
                medicalRecordDAO.countDistinctPatientsByConsultationMonth(year);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bệnh nhân khám");

        for (int m = 1; m <= 12; m++) {
            long cnt = counts.getOrDefault(m, 0L);
            String monthLabel = "Thg " + m;
            XYChart.Data<String, Number> data = new XYChart.Data<>(monthLabel, cnt);
            series.getData().add(data);
        }

        patientBarChart.getData().setAll(series);
        patientBarChart.setTitle("Số bệnh nhân theo tháng " + year);

        patientBarChart.applyCss();
        patientBarChart.layout();

        // Tooltip + hiệu ứng hover
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();

            // Tooltip hiện ngay lập tức (nếu bạn muốn):
            Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue().longValue());
            tooltip.setShowDelay(Duration.ZERO);
            Tooltip.install(node, tooltip);

            // Hover effect
            node.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
                st.setToX(1.1);
                st.setToY(1.1);
                st.play();
                node.setEffect(hoverShadow);
            });
            node.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
                st.setToX(1);
                st.setToY(1);
                st.play();
                node.setEffect(null);
            });
        }
    }


    private void loadMonthlyAppointmentChart(int year) {
        Map<Integer, Long> counts =
                appointmentDAO.countAppointmentsByMonth(year);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lịch hẹn");

        for (int m = 1; m <= 12; m++) {
            long cnt = counts.getOrDefault(m, 0L);
            String label = Month.of(m).getDisplayName(TextStyle.SHORT, Locale.getDefault());
            XYChart.Data<String, Number> data = new XYChart.Data<>(label, cnt);
            series.getData().add(data);
        }

        appointmentLineChart.getData().setAll(series);
        appointmentLineChart.setTitle("Số lịch hẹn theo tháng " + year);

        appointmentLineChart.applyCss();
        appointmentLineChart.layout();

        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();  // đây thường là Circle symbol

            // Tooltip ngay lập tức
            Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue().longValue());
            tooltip.setShowDelay(Duration.ZERO);
            Tooltip.install(node, tooltip);

            // Hover effect
            node.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
                st.setToX(1.2);
                st.setToY(1.2);
                st.play();
                node.setEffect(hoverShadow);
            });
            node.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
                st.setToX(1);
                st.setToY(1);
                st.play();
                node.setEffect(null);
            });
        }
    }


    public void getNameAccount() {
        Staff staff = LoginController.loggedInStaff;
        String userName = staff.getUserName();
        String initial = userName.trim().substring(0, 1);
        avatarLabel.setText(initial);
        Tooltip.install(userAvatar, new Tooltip(staff.getUserName()));
    }
}
