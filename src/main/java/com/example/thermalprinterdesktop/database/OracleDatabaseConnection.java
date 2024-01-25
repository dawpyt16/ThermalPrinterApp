package com.example.thermalprinterdesktop.database;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class OracleDatabaseConnection {

    private static Connection connection;
    public static final String DB_SETTINGS_FILE_PATH = System.getProperty("user.home") + "/PrinterSettings/db.properties";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties properties = loadProperties();
                String url = properties.getProperty("db.url");
                String username = properties.getProperty("db.username");
                String password = properties.getProperty("db.password");

                connection = DriverManager.getConnection(url, username, password);
            } catch (IOException e) {
                throw new SQLException("Error loading database properties");
            }
        }
        return connection;
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        String userHome = System.getProperty("user.home");
        String filePath = userHome + "/PrinterSettings/db.properties";

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void writeDefaultProperties() {
        Properties properties = new Properties();

        properties.setProperty("db.url", "");
        properties.setProperty("db.username", "");
        properties.setProperty("db.password", "");

        try (OutputStream outputStream = new FileOutputStream(DB_SETTINGS_FILE_PATH)) {
            properties.store(outputStream, "Przykładowe ustawienia");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
