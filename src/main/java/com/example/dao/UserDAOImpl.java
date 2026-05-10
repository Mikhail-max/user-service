package com.example.dao;

import com.example.model.User;
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public void saveUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    @Override
    public User getUserById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    @Override
    public void updateUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    @Override
    public void deleteUser(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user != null) {
                    session.remove(user);
                } else {
                    System.out.println("Пользователь с ID " + id + " не найден.");
                }
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}
