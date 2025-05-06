package com.example.rulett_graafiline;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

public class RulettGraafiline extends Application {
    private int saldo = 100;
    private double praeguneNurk = 355.0;
    private final String logifail = "rulettilog.txt";
    private final Random suvaline = new Random();
    private Label saldoSilt;
    private Label tulemusSilt;
    private TextField panuseVäli;
    private ComboBox<String> panuseTüüp;
    private int täpneNumber = -1;

    private final int[] ruleti_ratta_numbrid = {
            0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13,
            36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20,
            14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26
    };

    public void start(Stage lava) {
        lava.setTitle("Graafiline Rulett");
        Group ratasGrupp = new Group();
        double nurgaSamm = 360.0 / ruleti_ratta_numbrid.length;

        // loome iga numbri jaoks sektori ja numbri teksti selle sekotiri peale
        for (int i = 0; i < ruleti_ratta_numbrid.length; i++) {
            int number = ruleti_ratta_numbrid[i];
            double algusNurk = i * nurgaSamm;
            double lõppNurk = (i + 1) * nurgaSamm;

            Color värv = leiaSektoriVärv(number);
            Path sektor = looSektor(algusNurk, lõppNurk, värv);
            Text tekst = looSektoriTekst(number, algusNurk + nurgaSamm / 2);
            ratasGrupp.getChildren().addAll(sektor, tekst);
        }

        // keerab ratast
        ratasGrupp.setRotate(praeguneNurk);

        // punane nool, näitamaks tulemust
        Polygon nool = new Polygon(0.0, 0.0, -10.0, -30.0, 10.0, -30.0);
        nool.setFill(Color.RED);
        nool.setRotate(90);

        StackPane ratasPaneel = new StackPane(ratasGrupp, nool);
        StackPane.setAlignment(nool, Pos.CENTER_RIGHT);
        StackPane.setMargin(nool, new Insets(0, 60, -30, 0));

        // kasutaja info ja sisend
        saldoSilt = new Label("Sinu saldo: 100 €");
        panuseVäli = new TextField();
        panuseVäli.setPromptText("Panuse summa");

        panuseTüüp = new ComboBox<>();
        panuseTüüp.getItems().addAll("Punane", "Must", "Täpne number", "Paaris", "Paaritu", "Kõrge (19–36)", "Madal (1–18)");
        panuseTüüp.setValue("Punane");

        Button spinNupp = new Button("SPIN");
        spinNupp.setOnAction(e -> spin(ratasGrupp));

        Button logiNupp = new Button("Vaata logi");
        logiNupp.setOnAction(e -> kuvaLogi());

        tulemusSilt = new Label("Pane panus ja vajuta SPIN.");
        tulemusSilt.setWrapText(true);
        tulemusSilt.setMaxWidth(300);
        tulemusSilt.setStyle("-fx-font-size: 14px;");

        // juhtpaneel - nupud + info
        VBox juhtpaneel = new VBox(10, saldoSilt, panuseVäli, panuseTüüp, spinNupp, logiNupp, tulemusSilt);
        juhtpaneel.setAlignment(Pos.CENTER);
        juhtpaneel.setPadding(new Insets(15));

        // paigutame ruleti ratta keskele ja juhtpaneeli alla
        BorderPane juur = new BorderPane();
        juur.setCenter(ratasPaneel);
        juur.setBottom(juhtpaneel);

        Scene stseen = new Scene(juur, 500, 600);
        lava.setScene(stseen);
        lava.show();
    }

    private void spin(Group ratasGrupp) {
        int panus;

        // Kontrollib kas on korrektne panus, ei sobi kui panus väiksem 0 või suurem saldost
        try {
            panus = Integer.parseInt(panuseVäli.getText());
            if (panus <= 0 || panus > saldo)
                throw new NumberFormatException();
        }
        catch (NumberFormatException e) {
            tulemusSilt.setText("Sisesta sobiv panus (positiivne ja mitte suurem kui saldo).");
            return;
        }

        // Kui valiti täpne number, küsime mängijalt, mis numbri peale soovitakse panustada
        if (panuseTüüp.getValue().equals("Täpne number")) {
            TextInputDialog dialoog = new TextInputDialog();
            dialoog.setTitle("Täpne number");
            dialoog.setHeaderText("Sisesta number vahemikus 0–36");
            dialoog.setContentText("Number:");
            try {
                täpneNumber = Integer.parseInt(dialoog.showAndWait().orElse("-1"));
            }
            catch (Exception ignore) {
                täpneNumber = -1;
            }
        }

        int võiduNumber = suvaline.nextInt(37); //suvaline võidunumber vahemikus 0-36

        // leiab võidunumbri asukoha ruletirattas
        int indeks = -1;
        for (int i = 0; i < ruleti_ratta_numbrid.length; i++) {
            if (ruleti_ratta_numbrid[i] == võiduNumber) {
                indeks = i;
                break;
            }
        }

        if (indeks == -1) {
            tulemusSilt.setText("Viga: võidunumber ei leitud.");
            return;
        }

        // arvutab kui palju peab ratast keerama
        double nurkSektor = 360.0 / ruleti_ratta_numbrid.length;
        double sihtnurk = indeks * nurkSektor;

        // arvutame praeguse nurga
        double praeguneNormaliseeritud = praeguneNurk % 360.0;
        if (praeguneNormaliseeritud < 0)
            praeguneNormaliseeritud += 360.0;

        // keerutab ratast viis täispööret + sihtnurk
        double pööramisnurk = 5 * 360 + (360.0 - sihtnurk - praeguneNormaliseeritud);

        // pöörlemise animatsioon
        RotateTransition pöörle = new RotateTransition(Duration.seconds(3), ratasGrupp);
        pöörle.setByAngle(pööramisnurk);
        pöörle.setOnFinished(e -> {
            praeguneNurk = (praeguneNurk + pööramisnurk) % 360.0;
            boolean võit = kontrolliVõitu(võiduNumber);

            String värv;
            if (võiduNumber == 0) {
                värv = "roheline";
            } else if (onPunane(võiduNumber)) {
                värv = "punane";
            } else {
                värv = "must";
            }

            if (võit) { // kui võit suurendame saldot
                int koef = panuseTüüp.getValue().equals("Täpne number") ? 35 : 1;
                int võidusumma = panus * koef;
                saldo += võidusumma;

                tulemusSilt.setText("Pall jäi numbrile: " + võiduNumber + " (" + värv + "). Võitsid " + võidusumma + " €!");
                salvestaLogisse("Võit", võiduNumber, värv);
            }
            else { // kui kaotus siis vähendame saldot
                saldo -= panus;
                tulemusSilt.setText("Pall jäi numbrile: " + võiduNumber + " (" + värv + "). Kahjuks kaotasid.");
                salvestaLogisse("Kaotus", võiduNumber, värv);
            }

            // uuendab saldot
            saldoSilt.setText("Sinu saldo: " + saldo + " €");
        });
        pöörle.play();
    }

    private boolean kontrolliVõitu(int number) {
        String valitudPanus = panuseTüüp.getValue();

        // Kui panus on punane
        if (valitudPanus.equals("Punane")) {
            return onPunane(number);
        }
        // Kui panus on must
        else if (valitudPanus.equals("Must")) {
            return number != 0 && !onPunane(number);
        }
        // Kui panus oli täpne number
        else if (valitudPanus.equals("Täpne number")) {
            return number == täpneNumber;
        }
        // Kui panus oli paaris
        else if (valitudPanus.equals("Paaris")) {
            return number != 0 && number % 2 == 0;
        }

        // Kui panus oli paaritu
        else if (valitudPanus.equals("Paaritu")) {
            return number % 2 == 1;
        }

        // Kui panus oli kõrge (19–36)
        else if (valitudPanus.equals("Kõrge (19–36)")) {
            return number >= 19;
        }

        // Kui panus oli madal (1–18)
        else if (valitudPanus.equals("Madal (1–18)")) {
            return number >= 1 && number <= 18;
        }
        else {
            return false; // Kui ükski ei sobi
        }
    }

    private boolean onPunane(int number) {
        int[] punased = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int p : punased)
            if (p == number)
                return true;
        return false;
    }

    private void salvestaLogisse(String tüüp, int number, String värv) {
        // Loob kuupäeva ja kellaaja stringi praeguse hetke põhjal
        String aeg = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Rida mida salvestame logisse
        String rida = aeg + " - " + tüüp + ": " + number + " (" + värv + ")";

        // Kirjutame logifaili
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logifail, true))) {
            bw.write(rida);
            bw.newLine();
        }
        catch (IOException e) {
            Alert hoiatus = new Alert(AlertType.ERROR);
            hoiatus.setHeaderText("Logi salvestamise viga");
            hoiatus.setContentText(e.getMessage());
            hoiatus.showAndWait();
        }
    }

    private void kuvaLogi() {
        StringBuilder sisu = new StringBuilder(); //kogub logifaili sisu

        try (Scanner sc = new Scanner(new File(logifail))) { // avab faili ja loeb sisu
            while (sc.hasNextLine()) {
                sisu.append(sc.nextLine()).append("\n");
            }
        } catch (IOException e) {
            sisu.append("Logi ei leitud või lugemine ebaõnnestus.");
        }

        //loob uue akna kus saab logisi vaadata
        Alert aken = new Alert(AlertType.INFORMATION);
        aken.setTitle("Mängulogi");
        aken.setHeaderText("Eelmised tulemused:");

        //ala teksti jaoks
        TextArea ala = new TextArea(sisu.toString());
        ala.setEditable(false); //ei saa muuta
        ala.setWrapText(true); //vahetab rida
        ala.setPrefSize(400, 300);
        aken.getDialogPane().setContent(ala);
        aken.showAndWait();
    }

    //ez
    private Color leiaSektoriVärv(int number) {
        if (number == 0) {
            return Color.GREEN;
        }
        else if (onPunane(number)) {
            return Color.RED;
        }
        else {
            return Color.BLACK;
        }
    }

    //loob ühe sektori
    //algus - alguskraadid, lõpp - lõppkraadid
    //MIDA PERSET PEAB ÜLE VAATAMA ARU SAAMA ALLIKA LEIDMA VMS
    private Path looSektor(double algus, double lõpp, Color värv) {
        Path tee = new Path();
        tee.setFill(värv);
        tee.getElements().add(new MoveTo(200, 200));
        tee.getElements().add(new LineTo(200 + 150 * Math.cos(Math.toRadians(algus)), 200 + 150 * Math.sin(Math.toRadians(algus))));
        tee.getElements().add(new ArcTo(150, 150, 0, 200 + 150 * Math.cos(Math.toRadians(lõpp)), 200 + 150 * Math.sin(Math.toRadians(lõpp)), false, false));
        tee.getElements().add(new LineTo(200, 200));
        return tee;
    }

    private Text looSektoriTekst(int number, double nurkKraadides) {
        Text tekst = new Text(String.valueOf(number));
        tekst.setFont(new Font(14));
        tekst.setFill(Color.WHITE);

        double raadius = 125; //kaugus keskpunktist
        double rad = Math.toRadians(nurkKraadides); //nurk radiaanidesse, et saaks -cos ja -sin teha

        // x ja y koordinaadid, kuhu tekst läheb
        double x = 200 + raadius * Math.cos(rad);
        double y = 200 + raadius * Math.sin(rad);

        //paigutame teksti nii, et see oleks keskpunktiga õigesti joondatud
        tekst.setLayoutX(x - tekst.getLayoutBounds().getWidth() / 2);
        tekst.setLayoutY(y + tekst.getLayoutBounds().getHeight() / 4);

        //pöörame teksti, et see sobiks sektorinurgaga visuaalset paremini
        tekst.setRotate(nurkKraadides - 270);

        return tekst;
    }

    public static void main(String[] args) {
        launch(args);
    }
}