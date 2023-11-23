package com.example.thermalprinterdesktop.service;

import com.example.thermalprinterdesktop.model.Ticket;

import java.io.IOException;

public class PrinterTask implements Runnable{

    private final TicketPoller ticketPoller;
    private final TicketPrinterImpl ticketPrinter;
    private final String station;
    private final String printerName;

    public PrinterTask(TicketPoller ticketPoller, TicketPrinterImpl ticketPrinter, String station, String printerName) {
        this.ticketPoller = ticketPoller;
        this.ticketPrinter = ticketPrinter;
        this.station = station;
        this.printerName = printerName;
    }

    @Override
    public void run() {
        Ticket ticket = ticketPoller.getTicket(Integer.parseInt(station));
        if (ticket.getDailyNumber() != null){
            try {
                ticketPrinter.printTicket(printerName, ticket);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
