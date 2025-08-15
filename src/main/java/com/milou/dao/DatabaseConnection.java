package com.milou.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // لود درایور
                Class.forName("com.mysql.cj.jdbc.Driver");
                logger.info("MySQL JDBC Driver loaded successfully");

                Properties props = new Properties();
                InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties");
                if (is == null) {
                    logger.error("db.properties file not found");
                    throw new RuntimeException("db.properties file not found");
                }
                props.load(is);
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
                logger.info("Database connection established");
            } catch (Exception e) {
                logger.error("Failed to connect to database", e);
                throw new RuntimeException("Database connection failed", e);
            }
        }
        return connection;
    }
}