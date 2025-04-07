package org.example.quanlyhosobenhan.Util;

import org.example.quanlyhosobenhan.Model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    // Hàm trả về một SessionFactory duy nhất
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Cấu hình Hibernate
                Configuration configuration = new Configuration()
                        .setProperty(Environment.URL, "jdbc:mysql://localhost/HospitalManagement")
                        .setProperty(Environment.DRIVER, "com.mysql.cj.jdbc.Driver")  // Sử dụng com.mysql.cj.jdbc.Driver thay cho com.mysql.jdbc.Driver
                        .setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect")
                        .setProperty("hibernate.show_sql", "true")
                        .setProperty("hibernate.format_sql", "true")
                        .setProperty("hibernate.connection.username", "root")
                        .setProperty("hibernate.connection.password", "Huuvietdz123@")
                        .setProperty("hibernate.hbm2ddl.auto", "update"); // Tùy chọn tạo bảng hoặc cập nhật bảng khi có sự thay đổi;

                configuration.addAnnotatedClass(Appointment.class);
                configuration.addAnnotatedClass(Doctor.class);
                configuration.addAnnotatedClass(MedicalRecord.class);
                configuration.addAnnotatedClass(Patient.class);
                configuration.addAnnotatedClass(Prescription.class);
                configuration.addAnnotatedClass(PrescriptionDetail.class);

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    // Hàm đóng SessionFactory khi không còn sử dụng
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
