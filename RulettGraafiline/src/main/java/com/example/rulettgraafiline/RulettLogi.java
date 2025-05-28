package com.example.rulettgraafiline;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.control.TextArea;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class RulettLogi {
    private final String logifail = "rulettilog.txt";

    // salvestab mängutulemuse logifaili
    public void salvestaLogisse(String tüüp, int number, String värv) {

        // loob kuupäeva ja kellaaja stringi praeguse hetke põhjal
        String aeg = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // rida, mida salvestab logisse
        String rida = aeg + " - " + tüüp + ": " + number + " (" + värv + ")";

        // kirjutab logifaili
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logifail, true))) {
            bw.write(rida);
            bw.newLine();
        } catch (IOException e) {
            Alert hoiatus = new Alert(AlertType.ERROR);
            hoiatus.setHeaderText("Logi salvestamise viga");
            hoiatus.setContentText(e.getMessage());
            hoiatus.showAndWait();
        }
    }


    // kuvab mängulogi uues aknas
    public void kuvaLogi() {
        StringBuilder sisu = new StringBuilder(); // kogub logifaili sisu

        try (Scanner sc = new Scanner(new File(logifail))) { // avab faili ja loeb sisu
            while (sc.hasNextLine()) {
                sisu.append(sc.nextLine()).append("\n");
            }
        } catch (IOException e) {
            sisu.append("Logi ei leitud või lugemine ebaõnnestus.");
        }

        // loob uue akna, kus saab logisi vaadata
        Alert aken = new Alert(AlertType.INFORMATION);
        aken.setTitle("Mängulogi");
        aken.setHeaderText("Eelmised tulemused:");

        // ala teksti jaoks
        TextArea ala = new TextArea(sisu.toString());
        ala.setEditable(false); // ei saa muuta
        ala.setWrapText(true); // vahetab rida
        ala.setPrefSize(400, 300);
        aken.getDialogPane().setContent(ala);
        aken.showAndWait();
    }
}
