package com.example.thermalprinterdesktop.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Ticket {

    private String dailyNumber;

    private String infoText;

    private String machineId;

    public Ticket() {
    }

    public List<String> extractOffices() {
        List<String> offices = new ArrayList();

        String[] visits = infoText.split("@");

        for (String visit : visits) {
            String[] parts = visit.split("#");
            if (parts.length >= 2) {
                offices.add(parts[1]);
            }
        }

        return offices;
    }

    public List<String> extractHours() {
        List<String> hours = new ArrayList();

        String[] visits = infoText.split("@");

        for (String visit : visits) {
            String[] parts = visit.split("#");
            if (parts.length >= 3) {
                hours.add(parts[2]);
            }
        }

        return hours;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "dailyNumber='" + dailyNumber + '\'' +
                ", infoText='" + infoText + '\'' +
                ", machineId='" + machineId + '\'' +
                '}';
    }
}
