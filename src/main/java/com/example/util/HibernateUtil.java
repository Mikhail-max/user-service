package com.example.util;

import com.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    public static SessionFactory buildSessionFactory() {
        try {
            // Создаём реестр служб
            Configuration configuration = new Configuration();

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure("hibernate-main.cfg.xml")
                    .build();
            configuration.configure("hibernate-main.cfg.xml");


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
            Configuration configuration = getConfiguration(jdbcUrl, username, password);
            configuration.addAnnotatedClass(User.class);
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            // Строим SessionFactory
            return configuration.buildSessionFactory(registry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static Configuration getConfiguration(String jdbcUrl, String username, String password) {
        Configuration configuration = new Configuration();

        // Задаём все свойства программно (эквивалент hibernate-test.cfg.xml)
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", jdbcUrl);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);

        // Настройки из XML
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
        configuration.setProperty("hibernate.cache.use_query_cache", "false");
        return configuration;
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
