package com.example.dao;

import com.example.model.User;
import com.example.validation.Validation; // Импорт класса валидации
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private final SessionFactory sessionFactory;
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    public UserDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void saveUser(User user) {
        logger.debug("Начало сохранения пользователя в БД: Name={}, Email={}",
                user.getName(), user.getEmail());

        Validation.validateName(user.getName());
        Validation.validateEmail(user.getEmail());
        Validation.validateAge(user.getAge());

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.save(user);
                transaction.commit();
                // Явно обновляем объект, чтобы получить ID
                if (user.getId() == null) {
                    user.setId((Long) session.getIdentifier(user));
                }
                logger.debug("Пользователь успешно сохранён в БД: ID={}, Email={}",
                        user.getId(), user.getEmail());
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Ошибка при сохранении пользователя (Name={}, Email={}): ",
                        user.getName(), user.getEmail(), e);
                throw e;
            }
        }
    }



    @Override
    public User getUserById(Long id) {
        logger.debug("Поиск пользователя по ID: {}", id);

        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);

            if (user != null) {
                logger.debug("Найден пользователь по ID={}: Name={}, Email={}",
                        id, user.getName(), user.getEmail());
            } else {
                logger.warn("Пользователь с ID={} не найден в базе данных", id);
            }

            return user;
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя с ID={}: ", id, e);
            throw e;
        }
    }

    @Override
    public List<User> getAllUsers() {
        logger.debug("Запрос всех пользователей из БД");

        try (Session session = sessionFactory.openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            logger.debug("Получено {} пользователей из БД", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка всех пользователей: ", e);
            throw e;
        }
    }

    @Override
    public void updateUser(User user) {
        logger.debug("Обновление пользователя с ID: {}", user.getId());
        Validation.validateName(user.getName());
        Validation.validateEmail(user.getEmail());
        Validation.validateAge(user.getAge());
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                User managedUser = session.get(User.class, user.getId());
                if (managedUser == null) {
                    throw new IllegalArgumentException("Пользователь с ID " + user.getId() + " не найден");
                }
                managedUser.setName(user.getName());
                managedUser.setEmail(user.getEmail());
                managedUser.setAge(user.getAge());
                tx.commit();
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }



    @Override
    public boolean deleteUser(Long id) {
        logger.debug("Удаление пользователя с ID: {}", id);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                User user = session.get(User.class, id);
                if (user != null) {
                    session.remove(user);
                    transaction.commit();
                    logger.debug("Пользователь с ID={} успешно удалён из БД", id);
                    return true;
                } else {
                    logger.warn("Попытка удаления несуществующего пользователя с ID={}", id);
                    return false;
                }
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Ошибка при удалении пользователя с ID={}: ", id, e);
                return false;
            }
        } catch (Exception e) {
            logger.error("Критическая ошибка при открытии сессии Hibernate для удаления пользователя: ", e);
            return false;
        }
    }
}
