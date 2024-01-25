package com.example.thermalprinterdesktop.service;

import com.example.thermalprinterdesktop.model.Ticket;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;

import java.io.IOException;
import java.util.Properties;

public interface TicketPrinter {

    void printTicket(String printerName, Ticket ticket) throws IOException;
    EscPosConst.Justification getJustification(Properties settings);
    Style.FontSize getFontSize(Properties settings);
    String getNumberWithLeadingZeros(Properties settings, String num);

    String applyMask(String mask, String input);
}
