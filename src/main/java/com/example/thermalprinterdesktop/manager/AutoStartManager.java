package com.example.thermalprinterdesktop.manager;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class AutoStartManager {

    public AutoStartManager() {
    }

    public void manageRegistryExec(boolean runOnStartup) {
        String cmdLine;
        File file = new File("TicketPrinter.exe");
        String absolute = file.getAbsolutePath();
        if(runOnStartup){
            String REG_ADD_CMD = "reg add \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \"{0}\" /d \"{1}\" /t REG_EXPAND_SZ";
            cmdLine = MessageFormat.format(REG_ADD_CMD, "TicketPrinter", absolute);
        } else {
            String REG_DELETE_CMD = "reg delete \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \"TicketPrinter\" /f";
            cmdLine = MessageFormat.format(REG_DELETE_CMD, "TicketPrinter", absolute);
        }

        try {
            Runtime.getRuntime().exec(cmdLine);
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Error with registry");
        }

    }
}
