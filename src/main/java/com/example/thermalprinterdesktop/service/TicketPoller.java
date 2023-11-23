package com.example.thermalprinterdesktop.service;

import com.example.thermalprinterdesktop.database.OracleDatabaseConnection;
import com.example.thermalprinterdesktop.model.Ticket;

import java.sql.*;

public class TicketPoller {

    private Connection connection;

    public TicketPoller() {
        openConnection();
    }

    public Ticket getTicket(int station){
        Ticket ticket = new Ticket();
        try {
            String sql = "SELECT * FROM (SELECT * FROM PRINT_REQUEST WHERE MACHINE_ID = ? ORDER BY DAILY_NUMBER) WHERE ROWNUM = 1";
            String deleteSql = "DELETE FROM PRINT_REQUEST WHERE DAILY_NUMBER = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);

            preparedStatement.setInt(1, station);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ticket.setDailyNumber(resultSet.getString("DAILY_NUMBER"));
                ticket.setInfoText(resultSet.getString("INFO_TEXT"));
                ticket.setMachineId(resultSet.getString("MACHINE_ID"));

                long recordIdToDelete = resultSet.getLong("DAILY_NUMBER");

                deleteStatement.setLong(1, recordIdToDelete);
                deleteStatement.executeUpdate();
            }
            resultSet.close();
            preparedStatement.close();
            deleteStatement.close();

        }catch (java.sql.SQLRecoverableException e) {
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
