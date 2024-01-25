package com.example.thermalprinterdesktop.service;

import com.example.thermalprinterdesktop.database.OracleDatabaseConnection;
import com.example.thermalprinterdesktop.model.Ticket;
import javafx.beans.binding.ObjectExpression;

import java.sql.*;

public class TicketPoller {

    private static final int MACHINE_ID = 2;
    private static final int DAILY_NUMBER = 1;
    private static final int INFO_TEXT = 3;

    private Connection connection;

    public TicketPoller() {
        openConnection();
    }

    public Ticket getTicket(int station){
        Ticket ticket = new Ticket();
        String sql = "SELECT * FROM (SELECT * FROM PRINT_REQUEST WHERE MACHINE_ID = ? ORDER BY DAILY_NUMBER) WHERE ROWNUM = 1";
        String deleteSql = "DELETE FROM PRINT_REQUEST WHERE DAILY_NUMBER = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {

            preparedStatement.setInt(1, station);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Object dailyNumber = resultSet.getObject(DAILY_NUMBER);
                    Object machineId = resultSet.getObject(MACHINE_ID);
                    Object infoText = resultSet.getObject(INFO_TEXT);

                    if (dailyNumber == null) {
                        System.out.println("DAILY_NUMBER is NULL");
                        // Handle NULL case if needed
                    } else {
                        System.out.println("DAILY_NUMBER: " + dailyNumber);

                        ticket.setDailyNumber(dailyNumber.toString());
                        ticket.setInfoText(infoText.toString());
                        ticket.setMachineId(machineId.toString());

                        long recordIdToDelete = resultSet.getLong(1);

                        deleteStatement.setLong(1, recordIdToDelete);
                        deleteStatement.executeUpdate();
                    }
                }
            }
        }
        catch (java.sql.SQLRecoverableException e) {
            System.err.println("Closed connection. Waiting before retry...");
            try {
                openConnection();
                Thread.sleep(30000);}
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ticket;
    }

    public void openConnection(){
        try {
            connection = OracleDatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
