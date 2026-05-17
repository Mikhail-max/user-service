package com.example.util;

import com.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Создаём реестр служб
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure() // читает hibernate.cfg.xml
                    .build();

            // Строим SessionFactory
            return new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static SessionFactory buildSessionFactoryForTest(String jdbcUrl, String username, String password) {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySetting("connection.driver_class", "org.postgresql.Driver")
                    .applySetting("connection.url", jdbcUrl)
                    .applySetting("connection.username", username)
                    .applySetting("connection.password", password)
                    .applySetting("dialect", "org.hibernate.dialect.PostgreSQLDialect")
                    .applySetting("show_sql", "true")
                    .applySetting("format_sql", "true")
                    .applySetting("hbm2ddl.auto", "create-drop") // Создаём схему для тестов и удаляем после
                    .applySetting("cache.use_second_level_cache", "false")
                    .build();

            return new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
