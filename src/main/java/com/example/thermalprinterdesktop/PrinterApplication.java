package com.example.thermalprinterdesktop;

import com.example.thermalprinterdesktop.database.OracleDatabaseConnection;
import com.example.thermalprinterdesktop.manager.AppSettingsManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static com.example.thermalprinterdesktop.database.OracleDatabaseConnection.DB_SETTINGS_FILE_PATH;
import static com.example.thermalprinterdesktop.manager.AppSettingsManager.SETTINGS_FILE_PATH;


public class PrinterApplication extends Application {


    private Stage stage;
    private final Properties properties;

    private static final String iconImageLoc =
            "/images/Printer-blue-icon.png";

    public PrinterApplication() {
        this.properties = new AppSettingsManager().readProperties();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.stage = primaryStage;

        Platform.setImplicitExit(false);
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

        FXMLLoader fxmlLoader = new FXMLLoader(PrinterApplication.class.getResource("printer-resp.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Drukarka biletów");
        try {
            InputStream iconStream = getClass().getResourceAsStream(iconImageLoc);
            if (iconStream != null) {
                javafx.scene.image.Image iconImage = new javafx.scene.image.Image(iconStream);
                primaryStage.getIcons().add(iconImage);
            } else {
                System.err.println("Nie można znaleźć pliku obrazu.");
            }
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas wczytywania obrazu: " + e.getMessage());
        }

        primaryStage.setScene(scene);

        if (properties.getProperty("running").equals("true")){
            primaryStage.hide();
        } else {
            primaryStage.show();
        }
    }

    private void addAppToTray() {
        try {
            java.awt.Toolkit.getDefaultToolkit();

            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support.");
                Platform.setImplicitExit(true);
                Platform.runLater(() -> {
                    stage.show();
                    stage.setOnCloseRequest(event -> System.exit(0));
                });
            } else{
                java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

                BufferedImage icon;
                try {
                    icon = ImageIO.read(Objects.requireNonNull(getClass().getResource(iconImageLoc)));
                } catch (IllegalArgumentException | IOException e) {
                    icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                }

                java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(icon);

                trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

                java.awt.MenuItem openItem = new java.awt.MenuItem("Drukarka biletów");
                openItem.addActionListener(event -> Platform.runLater(this::showStage));

                java.awt.Font defaultFont = java.awt.Font.decode(null);
                java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
                openItem.setFont(boldFont);

                java.awt.MenuItem exitItem = new java.awt.MenuItem("Zamknij");
                exitItem.addActionListener(event -> {
                    System.exit(0);
                    tray.remove(trayIcon);
                });

                final java.awt.PopupMenu popup = new java.awt.PopupMenu();
                popup.add(openItem);
                popup.addSeparator();
                popup.add(exitItem);
                trayIcon.setPopupMenu(popup);
                trayIcon.setToolTip("Drukarka biletów");

                tray.add(trayIcon);
            }


        } catch (AWTException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    public static void main(String[] args) {
        File settingsFile = new File(SETTINGS_FILE_PATH);
        File dbSettingsFile = new File(DB_SETTINGS_FILE_PATH);
        if (!settingsFile.exists()) {
            AppSettingsManager.writeDefaultProperties();
        }
        if (!dbSettingsFile.exists()) {
            OracleDatabaseConnection.writeDefaultProperties();
            System.out.println("Enter database connection settings in: " + DB_SETTINGS_FILE_PATH);
        }
        launch();
    }

}
