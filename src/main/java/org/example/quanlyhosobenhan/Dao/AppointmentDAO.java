package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Model.Appointment;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.Patient;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentDAO {
    public Long countByDoctorId(int doctorId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long)session.createQuery("select count(m) from Appointment m where m.doctor.id = :doctorId")
                    .setParameter("doctorId", doctorId).uniqueResult();
        } catch(Exception e){
            e.printStackTrace();
            return 0L;
        }
    }

    public Long countConfirmedPatientsByDoctorId(int doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
            SELECT COUNT(DISTINCT a.patient.id)
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.status = :status
        """;
            return session.createQuery(hql, Long.class)
                    .setParameter("doctorId", doctorId)
                    .setParameter("status", Appointment.Status.Confirmed)
                    .uniqueResult();
        }
    }

    // 1. Tạo lịch hẹn mới (bệnh nhân đặt)
    public void createAppointment(Patient patient, Doctor doctor, LocalDateTime appointmentTime, String reason) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setDoctor(doctor);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setReason(reason);
            appointment.setStatus(Appointment.Status.Pending);
            appointment.setCreatedAt(LocalDateTime.now());
            appointment.setUpdatedAt(LocalDateTime.now());

            session.save(appointment);
            tx.commit();
        }
    }

    // 2. Lấy tất cả lịch hẹn chờ xác nhận
    public List<Appointment> getPendingAppointments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Appointment WHERE status = :status ORDER BY appointmentTime ASC";
            Query<Appointment> query = session.createQuery(hql, Appointment.class);
            query.setParameter("status", Appointment.Status.Pending);
            return query.list();
        }
    }

    // 3. Nhân viên xác nhận lịch (cập nhật trạng thái + số phòng)
    public void confirmAppointment(int appointmentId, String roomNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Appointment appointment = session.get(Appointment.class, appointmentId);
            if (appointment != null && appointment.getStatus() == Appointment.Status.Pending) {
                appointment.setStatus(Appointment.Status.Confirmed);
                appointment.setRoomNumber(roomNumber);
                appointment.setUpdatedAt(LocalDateTime.now());
                session.update(appointment);
            }

            tx.commit();
        }
    }

    public List<Appointment> getAllAppointments() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Appointment ", Appointment.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Appointment WHERE patient.id = :patientId";
            Query<Appointment> query = session.createQuery(hql, Appointment.class);
            query.setParameter("patientId", patientId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Appointment> getAppointmentsByDoctorId(int doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Appointment WHERE doctor.id = :doctorId";
            Query<Appointment> query = session.createQuery(hql, Appointment.class);
            query.setParameter("doctorId", doctorId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Patient> getConfirmedPatientsByDoctorId(int doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
            SELECT DISTINCT a.patient FROM Appointment a 
            WHERE a.doctor.id = :doctorId AND a.status = :status
        """;
            Query<Patient> query = session.createQuery(hql, Patient.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("status", Appointment.Status.Confirmed);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void update(Appointment appointment) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            appointment.setUpdatedAt(LocalDateTime.now());
            session.update(appointment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void delete(int appointmentId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Appointment appointment = session.get(Appointment.class, appointmentId);
            if (appointment != null) {
                session.delete(appointment);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public Long countAllAppointments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select count(a) from Appointment a", Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public Map<Integer, Long> countAppointmentsByMonth(int year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
            select month(a.appointmentTime), count(a)
            from Appointment a
            where year(a.appointmentTime) = :year
            group by month(a.appointmentTime)
        """;
            List<Object[]> rows = session.createQuery(hql, Object[].class)
                    .setParameter("year", year)
                    .getResultList();

            Map<Integer, Long> result = new HashMap<>();
            for (Object[] row : rows) {
                Integer month = (Integer) row[0];
                Long cnt     = (Long)    row[1];
                result.put(month, cnt);
            }
            return result;
        }
    }

    public Map<Integer, Long> countAppointmentsByMonthAndDoctor(int year, int doctorId) {
        Map<Integer, Long> result = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
            select month(a.appointmentTime), count(a)
            from Appointment a
            where year(a.appointmentTime) = :year
              and a.doctor.id = :doctorId
            group by month(a.appointmentTime)
            order by month(a.appointmentTime)
        """;
            List<Object[]> rows = session.createQuery(hql, Object[].class)
                    .setParameter("year", year)
                    .setParameter("doctorId", doctorId)
                    .getResultList();

            for (Object[] row : rows) {
                Integer month = (Integer) row[0];
                Long cnt      = (Long)    row[1];
                result.put(month, cnt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Long countByPatientId(int patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select count(a) from Appointment a where a.patient.id = :patientId", Long.class)
                    .setParameter("patientId", patientId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


}

