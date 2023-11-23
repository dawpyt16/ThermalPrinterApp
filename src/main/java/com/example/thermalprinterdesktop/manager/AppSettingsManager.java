package com.example.thermalprinterdesktop.manager;

import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

@Getter
public class AppSettingsManager {

    public static final String SETTINGS_FILE_PATH = System.getProperty("user.home") + "/PrinterSettings/settings.properties";
    private final Properties properties;

    public AppSettingsManager() {
        this.properties = readProperties();
    }

    public Properties readProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(SETTINGS_FILE_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    public void saveProperties(String fontSize,
                               String lineSpacing,
                               String justification,
                               String leadingZeros,
                               String stationId,
                               String mask,
                               boolean runOnStartup,
                               String printer,
                               boolean running){
        try {
            properties.setProperty("fontSize", fontSize);
            properties.setProperty("lineSpacing", lineSpacing);
            properties.setProperty("justification", justification);
            properties.setProperty("leadingZeros", leadingZeros);
            properties.setProperty("stationId", Objects.requireNonNullElse(stationId, ""));
            properties.setProperty("mask", Objects.requireNonNullElse(mask, ""));
            properties.setProperty("printerName", Objects.requireNonNullElse(printer, ""));
            properties.setProperty("runOnStartup", String.valueOf(runOnStartup));
            properties.setProperty("running", String.valueOf(running));
            properties.store(new FileOutputStream(SETTINGS_FILE_PATH), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRunning(boolean running){
        try {
            properties.setProperty("running", String.valueOf(running));
            properties.store(new FileOutputStream(SETTINGS_FILE_PATH), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeDefaultProperties() {
        Properties properties = new Properties();

        properties.setProperty("fontSize", "1");
        properties.setProperty("lineSpacing", "1");
        properties.setProperty("justification", "center");
        properties.setProperty("leadingZeros", "default");
        properties.setProperty("running", "false");
        properties.setProperty("image", "");
        properties.setProperty("imageAlt", "");
        properties.setProperty("additionalInformation", "");
        properties.setProperty("mask", "");
        properties.setProperty("printerName", "");
        properties.setProperty("running", "");
        properties.setProperty("runOnStartup", "");
        properties.setProperty("stationId", "");
        try{
            Files.createDirectories(Paths.get(System.getProperty("user.home") + "/PrinterSettings"));
        }catch (IOException e){
            e.printStackTrace();
        }
        try (OutputStream outputStream = new FileOutputStream(SETTINGS_FILE_PATH)) {
                        properties.store(outputStream, "Przykładowe ustawienia");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
