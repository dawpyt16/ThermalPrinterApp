package com.example.thermalprinterdesktop.controller;

import com.example.thermalprinterdesktop.manager.AppSettingsManager;
import com.example.thermalprinterdesktop.manager.AutoStartManager;
import com.example.thermalprinterdesktop.service.PrinterTask;
import com.example.thermalprinterdesktop.service.TicketPoller;
import com.example.thermalprinterdesktop.service.TicketPrinterImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PrinterController implements Initializable {

    @FXML
    public Button startButton;
    @FXML
    public CheckBox runOnStartupChB;
    @FXML
    public ChoiceBox<String> printerCB;
    @FXML
    private Label status;

    @FXML
    private ChoiceBox<String> fontSizeCB;
    @FXML
    private ChoiceBox<String> lineSpacingCB;
    @FXML
    private ChoiceBox<String> justificationCB;
    @FXML
    private ChoiceBox<String> leadingZerosCB;
    @FXML
    private TextField stationId;
    @FXML
    private TextField mask;
    @FXML
    private Label previewOffice;
    @FXML
    private Label previewTime;
    @FXML
    private Label previewNumber;
    @FXML
    private VBox previewVBox;

    private final ObservableList<String> fontSizeList = FXCollections
            .observableArrayList("1", "2");
    private final ObservableList<String> lineSpacingList = FXCollections
            .observableArrayList("1", "2");
    private final ObservableList<String> justificationList = FXCollections
            .observableArrayList("Left", "Center", "Right");
    private final ObservableList<String> leadingZerosList = FXCollections
            .observableArrayList("0", "1", "2", "3", "default");

    private final ObservableList<String> printerList = FXCollections.observableArrayList();

    private final AppSettingsManager settingsManager;
    private final TicketPoller ticketPoller;
    private final TicketPrinterImpl ticketPrinter;
    private ScheduledFuture<?> scheduledFuture;
    private final ScheduledExecutorService scheduler;
    private final AutoStartManager autoStartManager;
    private boolean isRunning = false;

    public PrinterController() {
        this.autoStartManager = new AutoStartManager();
        this.ticketPoller = new TicketPoller();
        this.settingsManager = new AppSettingsManager();
        this.ticketPrinter = new TicketPrinterImpl();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }


    public void getPrinterList() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        if (printServices.length > 0) {
            for (PrintService printService : printServices) {
                printerList.add(printService.getName());
            }
        } else {
            System.out.println("No available printers.");
        }
    }

    private void setPreviewFontSize() {
        if ("1".equals(fontSizeCB.getValue())) {
            previewOffice.setStyle("-fx-font-size: 18");
            previewTime.setStyle("-fx-font-size: 18");
        } else {
            previewOffice.setStyle("-fx-font-size: 24");
            previewTime.setStyle("-fx-font-size: 24");
        }
    }

    private void setPreviewLineSpacing() {
        if (Objects.equals(lineSpacingCB.getValue(), "1")){
            previewVBox.setSpacing(0);
        } else {
            previewVBox.setSpacing(10);
        }
    }

    private void setPreviewLeadingZeros() {
        switch (leadingZerosCB.getValue()) {
            case "1" ->
                previewNumber.setText("01");
            case "2" ->
                previewNumber.setText("001");
            case "3" ->
                previewNumber.setText("0001");
            default ->
                previewNumber.setText("1");
        }
    }

    private void setPreviewJustification() {
        switch (justificationCB.getValue()) {
            case "Left" -> {
                previewOffice.setAlignment(Pos.CENTER_LEFT);
                previewTime.setAlignment(Pos.CENTER_LEFT);
            }
            case "Right" -> {
                previewOffice.setAlignment(Pos.CENTER_RIGHT);
                previewTime.setAlignment(Pos.CENTER_RIGHT);
            }
            default -> {
                previewOffice.setAlignment(Pos.CENTER);
                previewTime.setAlignment(Pos.CENTER);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Properties properties = settingsManager.readProperties();

        fontSizeCB.setValue(properties.getProperty("fontSize"));
        lineSpacingCB.setValue(properties.getProperty("lineSpacing"));
        justificationCB.setValue(properties.getProperty("justification"));
        leadingZerosCB.setValue(properties.getProperty("leadingZeros"));
        stationId.setText(properties.getProperty("stationId"));
        mask.setText(properties.getProperty("mask"));
        runOnStartupChB.setSelected(Boolean.parseBoolean(properties.getProperty("runOnStartup")));
        printerCB.setValue(properties.getProperty("printerName"));

        fontSizeCB.setItems(fontSizeList);
        lineSpacingCB.setItems(lineSpacingList);
        justificationCB.setItems(justificationList);
        leadingZerosCB.setItems(leadingZerosList);

        getPrinterList();
        printerCB.setItems(printerList);

        setPreviewJustification();
        setPreviewLeadingZeros();
        setPreviewLineSpacing();
        setPreviewFontSize();

        if (properties.getProperty("running").equals("true")){
            onStartButtonClick();
        }

    }

    @FXML
    protected void onSaveButtonClick(){
        settingsManager.saveProperties(
                fontSizeCB.getValue(),
                lineSpacingCB.getValue(),
                justificationCB.getValue(),
                leadingZerosCB.getValue(),
                stationId.getText(),
                mask.getText(),
                runOnStartupChB.isSelected(),
                printerCB.getValue(),
                isRunning
        );
        setPreviewJustification();
        setPreviewLeadingZeros();
        setPreviewLineSpacing();
        setPreviewFontSize();
        autoStartManager.manageRegistryExec(runOnStartupChB.isSelected());
        status.setText("Zapisano");
    }

    @FXML
    protected void onStartButtonClick() {
        status.setText("");
        onSaveButtonClick();
        if (!isRunning) {
            if (!Objects.equals(stationId.getText(), "")) {
                isRunning = true;
                settingsManager.setRunning(true);
                startButton.setText("Stop");
                status.setText("");
                String stationIdText = stationId.getText();
                scheduledFuture = scheduler.scheduleWithFixedDelay(new PrinterTask(
                                    ticketPoller,
                                    ticketPrinter,
                                    stationIdText,
                                    printerCB.getValue()),
                            1,
                            2,
                            TimeUnit.SECONDS);
            } else {
                status.setText("Numer stanowiska nie może być pusty!");
            }
        } else {
            isRunning = false;
            settingsManager.setRunning(false);
            startButton.setText("Start");
            scheduledFuture.cancel(true);
        }
    }
}
