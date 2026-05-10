package com.example;

import com.example.dao.UserDAO;
import com.example.dao.UserDAOImpl;
import com.example.service.UserService;
import com.example.ui.ConsoleUI;
import com.example.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Запуск приложения user-service...");

        try {
            UserDAO userDAO = new UserDAOImpl();
            UserService userService = new UserService(userDAO);
            ConsoleUI consoleUI = new ConsoleUI(userService);


            consoleUI.start();

            logger.info("Приложение завершено корректно.");
        } catch (Exception e) {
            logger.error("Критическая ошибка при запуске приложения: ", e);
            System.err.println("Произошла непредвиденная ошибка. Приложение будет закрыто.");
            e.printStackTrace();
        } finally {

            HibernateUtil.shutdown();
        }
    }
}
