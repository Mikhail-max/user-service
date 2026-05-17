package com.example.dao;

import com.example.model.User;
import com.example.validation.Validation; // Импорт класса валидации
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    @Override
    public void saveUser(User user) {
        logger.debug("Начало сохранения пользователя в БД: Name={}, Email={}",
                user.getName(), user.getEmail());

        // Валидация перед сохранением
        Validation.validateName(user.getName());
        Validation.validateEmail(user.getEmail());
        Validation.validateAge(user.getAge());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.save(user);
                transaction.commit();
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
        } catch (Exception e) {
            logger.error("Критическая ошибка при открытии сессии Hibernate для сохранения пользователя: ", e);
            throw e;
        }
    }

    @Override
    public User getUserById(Long id) {
        logger.debug("Поиск пользователя по ID: {}", id);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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
        logger.debug("Обновление пользователя с ID={}: Name={}, Email={}",
                user.getId(), user.getName(), user.getEmail());

        Validation.validateName(user.getName());
        Validation.validateEmail(user.getEmail());
        Validation.validateAge(user.getAge());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.update(user);
                transaction.commit();
                logger.debug("Пользователь с ID={} успешно обновлён", user.getId());
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Ошибка при обновлении пользователя с ID={}: ", user.getId(), e);
                throw e;
            }
        } catch (Exception e) {
            logger.error("Критическая ошибка при открытии сессии Hibernate для обновления пользователя: ", e);
            throw e;
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        logger.debug("Удаление пользователя с ID: {}", id);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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
