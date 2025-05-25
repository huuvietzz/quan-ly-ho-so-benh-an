package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Controllers.LoginController;
import org.example.quanlyhosobenhan.Controllers.PasswordEncoder;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.Patient;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.*;

public class PatientDAO {
    public Patient login(String userName, String plainPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Patient WHERE userName = :userName";
            Query<Patient> query = session.createQuery(hql, Patient.class);
            query.setParameter("userName", userName);

            Patient patient = query.uniqueResult();

            if (patient != null && PasswordEncoder.checkPassword(plainPassword, patient.getPassword())) {
                return patient;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Sai username hoặc password
    }

    public boolean register(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Hash password trước khi lưu vào database
            String hashedPassword = PasswordEncoder.hashPassword(patient.getPassword());
            patient.setPassword(hashedPassword);

            session.save(patient);
            session.getTransaction().commit();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void savePatient(Patient patient) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Gán bác sĩ cho bệnh nhân (dùng doctor đã login)
            Set<Doctor> assignedDoctors = new HashSet<>();
            assignedDoctors.add(LoginController.loggedInDoctor);
            patient.setDoctors(assignedDoctors);

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
            return session.createQuery("select p from Patient p join p.doctors d where d.id = :doctorId", Patient.class)
                    .setParameter("doctorId", doctorId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long countByDoctorId(int doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long)session.createQuery("select count(p) from Patient p join p.doctors d where d.id = :doctorId")
                    .setParameter("doctorId", doctorId).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public Long countAllPatients() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long) session.createQuery("select count(p) from Patient p").uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


    public Map<Patient.Gender, Long> getPatientCountByGender(int doctorId) {
        Map<Patient.Gender, Long> genderCountMap = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "select p.gender, count(p) from Patient p join p.doctors d where d.id = :doctorId group by p.gender";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("doctorId", doctorId);

            for(Object[] row : query.getResultList()) {
                Patient.Gender gender = (Patient.Gender)row[0];
                Long count = (Long)row[1];
                genderCountMap.put(gender, count);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return genderCountMap;
    }

    public Map<Patient.Gender, Long> countAllPatientsByGender() {
        Map<Patient.Gender, Long> genderCountMap = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
            select p.gender, count(p)
            from Patient p
            group by p.gender
        """;
            Query<Object[]> query = session.createQuery(hql, Object[].class);

            for (Object[] row : query.getResultList()) {
                Patient.Gender gender = (Patient.Gender) row[0];
                Long count = (Long) row[1];
                genderCountMap.put(gender, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genderCountMap;
    }

}
