package org.example.quanlyhosobenhan.Dao;

import com.mysql.cj.protocol.x.Notice;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalRecordDAO {
    public void saveMedicalRecord(MedicalRecord medicalRecord) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(medicalRecord);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMedicalRecord(MedicalRecord medicalRecord) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(medicalRecord);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MedicalRecord getMedicalRecordById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(MedicalRecord.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from MedicalRecord ", MedicalRecord.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<MedicalRecord> getMedicalRecordsByDoctor(int doctorId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from MedicalRecord where doctor.id = :doctorId", MedicalRecord.class).setParameter("doctorId", doctorId).getResultList();
        }
    }

    public List<MedicalRecord> getRecordsByPatientId(int patientId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from MedicalRecord where patient.id = :patientId", MedicalRecord.class)
                    .setParameter("patientId", patientId)
                    .getResultList();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteMedicalRecord(Integer id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            MedicalRecord medicalRecord = session.get(MedicalRecord.class, id);
            if(medicalRecord != null) {
                session.delete(medicalRecord);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllMedicalRecords() {
        String hql = "delete from MedicalRecord ";
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery(hql).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long countByDoctorId(int doctorId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long)session.createQuery("select count(m) from MedicalRecord m where m.doctor.id = :doctorId")
                    .setParameter("doctorId", doctorId).uniqueResult();
        } catch(Exception e){
            e.printStackTrace();
            return 0L;
        }
    }

    public Long countAllMedicalRecords() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long) session.createQuery("select count(m) from MedicalRecord m").uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public Map<Integer, Long> countDistinctPatientsByConsultationMonth(int year) {
        Map<Integer, Long> result = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
            select month(m.consultationDate), count(distinct m.patient.id)
            from MedicalRecord m
            where year(m.consultationDate) = :year
            group by month(m.consultationDate)
            order by month(m.consultationDate)
        """;
            List<Object[]> rows = session.createQuery(hql, Object[].class)
                    .setParameter("year", year)
                    .getResultList();

            for (Object[] row : rows) {
                Integer month = (Integer) row[0];
                Long    cnt   = (Long)    row[1];
                result.put(month, cnt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public Map<Integer, Long> countDistinctPatientsByConsultationMonthByDoctor(int year, int doctorId) {
        Map<Integer, Long> result = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
            select month(m.consultationDate), count(distinct m.patient.id)
            from MedicalRecord m
            where year(m.consultationDate) = :year
              and m.doctor.id = :doctorId
            group by month(m.consultationDate)
            order by month(m.consultationDate)
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
                            "select count(m) from MedicalRecord m where m.patient.id = :patientId", Long.class)
                    .setParameter("patientId", patientId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

}
