package org.example.quanlyhosobenhan.Dao;

import org.example.quanlyhosobenhan.Controllers.PasswordEncoder;
import org.example.quanlyhosobenhan.Model.Doctor;
import org.example.quanlyhosobenhan.Model.Staff;
import org.example.quanlyhosobenhan.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class StaffDAO {
        public Staff login(String userName, String plainPassword) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                String hql = "from Staff where userName=:userName";
                Query<Staff> query = session.createQuery(hql, Staff.class);
                query.setParameter("userName", userName);

                Staff staff = query.uniqueResult();
                if (staff != null && PasswordEncoder.checkPassword(plainPassword, staff.getPassword())) {
                    return staff;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    public boolean register(Staff staff) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Hash password trước khi lưu vào database
            String hashedPassword = PasswordEncoder.hashPassword(staff.getPassword());
            staff.setPassword(hashedPassword);

            session.save(staff);
            session.getTransaction().commit();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(String userName, String newPassword){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();

            String hql = "from Staff where userName = :userName";
            Query<Staff> query = session.createQuery(hql, Staff.class);
            query.setParameter("userName", userName);
            Staff staff = query.uniqueResult();

            if(staff != null){
                // Ma hoa mat khau moi
                String hashedPassword = PasswordEncoder.hashPassword(newPassword);
                staff.setPassword(hashedPassword);
                session.update(staff);
                session.getTransaction().commit();
                return true;
            } else {
                return false; // Ko tìm thấy user
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
