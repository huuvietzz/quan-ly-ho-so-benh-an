package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Model.MedicalRecord;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

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

    public List<MedicalRecord> getAllMedicalRecords() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from MedicalRecord ", MedicalRecord.class).getResultList();
        } catch (Exception e) {
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
}
