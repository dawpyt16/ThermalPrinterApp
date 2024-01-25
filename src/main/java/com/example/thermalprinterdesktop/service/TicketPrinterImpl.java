package com.example.thermalprinterdesktop.service;

import com.example.thermalprinterdesktop.manager.AppSettingsManager;
import com.example.thermalprinterdesktop.model.Ticket;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.*;
import com.github.anastaciocintra.output.PrinterOutputStream;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


public class TicketPrinterImpl implements TicketPrinter{

    private final AppSettingsManager settingsManager;

    public TicketPrinterImpl() {
        settingsManager = new AppSettingsManager();
    }



    @Override
    public void printTicket(String printerName, Ticket ticket) throws IOException {
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        Properties properties = settingsManager.readProperties();

        String imageName = properties.getProperty("image");
        String imageAltText = properties.getProperty("imageAlt");

        RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();

        escpos = new EscPos(new PrinterOutputStream(printService));

        Style userStyle = new Style()
                .setJustification(getJustification(properties))
                .setFontSize(getFontSize(properties), getFontSize(properties));

        Style additionalInformationStyle = new Style()
                .setJustification(EscPosConst.Justification.Center)
                .setFontSize(Style.FontSize._1, Style.FontSize._1);

        Style imageAltStyle = new Style()
                .setJustification(EscPosConst.Justification.Center)
                .setFontSize(Style.FontSize._2, Style.FontSize._2);


        Style ticketNumber = new Style()
                .setFontSize(Style.FontSize._8, Style.FontSize._8)
                .setJustification(EscPosConst.Justification.Center);

        imageWrapper.setJustification(EscPosConst.Justification.Center);

        List<String> offices = ticket.extractOffices();
        List<String> hours = ticket.extractHours();

        int targetWidth = 400;
        int targetHeight = 400;
        Bitonal algorithm = new BitonalThreshold(200);

        if (imageName != null && !imageName.isEmpty()){
            try {
                BufferedImage originalImage = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/" + imageName)));
//                BufferedImage originalImage = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream(imageName)));
                double scaleX = (double) targetWidth / originalImage.getWidth();
                double scaleY = (double) targetHeight / originalImage.getHeight();
                double scale = Math.min(scaleX, scaleY);
                int newWidth = (int) (originalImage.getWidth() * scale);
                int newHeight = (int) (originalImage.getHeight() * scale);

                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();

                EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(resizedImage), algorithm);

                escpos.write(imageWrapper, escposImage)
                        .feed(1);
            }catch (NullPointerException e){
                escpos.writeLF(imageAltStyle, imageAltText)
                        .feed(1);
            }

        } else if (imageAltText != null && !imageAltText.isEmpty()) {
            escpos.writeLF(imageAltStyle, imageAltText)
                    .feed(1);
        }

        escpos.writeLF(ticketNumber, getNumberWithLeadingZeros(properties, ticket.getDailyNumber()));

        for (int i = 0; i < offices.size(); i++){
            escpos.writeLF(additionalInformationStyle,"_______________________________");
            escpos.writeLF(userStyle, "Gabinet: " + offices.get(i))
                    .feed(Integer.parseInt(properties.getProperty("lineSpacing")));
            escpos.writeLF(userStyle, "Godzina: " + hours.get(i));
        }

        escpos.writeLF(additionalInformationStyle,"_______________________________");
        escpos.writeLF(additionalInformationStyle, properties.getProperty("additionalInformation"));



        escpos.feed(3)
                .cut(EscPos.CutMode.FULL);
        escpos.close();
    }

    @Override
    public String applyMask(String mask, String input) {
        if(mask == null || mask.equals("")){
            return input;
        }
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char digit = input.charAt(i);

            if (Character.isDigit(digit) && digit >= '0' && digit <= '9') {
                int maskIndex = digit - '0';

                if (maskIndex < mask.length()) {
                    result.append(mask.charAt(maskIndex));
                } else {
                    result.append(digit);
                }
            } else {
                result.append(digit);
            }
        }

        return result.toString();
    }

    @Override
    public EscPosConst.Justification getJustification(Properties settings){
        EscPosConst.Justification enumJustification;
        switch (settings.getProperty("justification")) {
            case "Left" -> enumJustification = EscPosConst.Justification.Left_Default;
            case "Right" -> enumJustification = EscPosConst.Justification.Right;
            default ->
                    enumJustification = EscPosConst.Justification.Center;
        }
        return enumJustification;
    }

    @Override
    public Style.FontSize getFontSize(Properties settings){
        Style.FontSize enumFontSize;
        if ("1".equals(settings.getProperty("fontSize"))) {
            enumFontSize = Style.FontSize._2;
        } else {
            enumFontSize = Style.FontSize._3;
        }
        return enumFontSize;
    }

    @Override
    public String getNumberWithLeadingZeros(Properties settings, String num) {
        if ("default".equals(settings.getProperty("leadingZeros"))) {
            return applyMask(settings.getProperty("mask"), num);
        }

        int number;
        try {
            number = Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return num;
        }

        String leadingZeros = settings.getProperty("leadingZeros");
        String format = switch (leadingZeros) {
            case "1" -> "%02d";
            case "2" -> "%03d";
            case "3" -> "%04d";
            default -> "%d";
        };

        String formattedNumber = String.format(format, number);

        return applyMask(settings.getProperty("mask"), formattedNumber);
    }
}
