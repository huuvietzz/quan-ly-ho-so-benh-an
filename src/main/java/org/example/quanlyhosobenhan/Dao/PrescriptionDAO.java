package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Model.Prescription;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PrescriptionDAO {
    public static void savePrescription(Prescription prescription) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
           Transaction transaction =  session.beginTransaction();
           session.save(prescription);
           transaction.commit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Prescription getByMedicalRecordId(int recordId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Prescription where medicalRecord.id = :id", Prescription.class)
                    .setParameter("id", recordId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updatePrescription(Prescription prescription) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction =  session.beginTransaction();
            session.update(prescription);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
