package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Controllers.LoginController;
import org.example.quanlyhosobenhan.Model.Patient;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PatientDAO {
    public void savePatient(Patient patient) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Gán bác sĩ cho bệnh nhân (dùng doctor đã login)
            patient.setDoctor(LoginController.loggedInDoctor);

            session.save(patient);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePatient(Patient patient) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(patient);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Patient> getAllPatient() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Patient", Patient.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deletePatient(Integer id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Patient patient = session.get(Patient.class, id);
            if(patient != null) {
                session.delete(patient);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllPatient() {
        String hql = "delete from Patient";
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery(hql).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Patient> query = session.createQuery("FROM Patient WHERE email = :email", Patient.class);
            query.setParameter("email", email);
            return query.uniqueResult() != null;
        } finally {
            session.close();
        }
    }

    public boolean existsByNumberPhone(String phoneNumber) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Patient> query = session.createQuery("FROM Patient WHERE phone = :phoneNumber", Patient.class);
            query.setParameter("phoneNumber", phoneNumber);
            return query.uniqueResult() != null;
        } finally {
            session.close();
        }
    }

    public List<Patient> getPatientsByDoctorId(int doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Patient where doctor.id = :doctorId", Patient.class)
                    .setParameter("doctorId", doctorId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
