package com.example.rulettgraafiline;

import javafx.animation.RotateTransition;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

public class RulettLoogika {
    private int saldo = 100; // mängija algsaldo
    private double praeguneNurk = 355.0; // ratta algne pöördenurk kraadides
    private double ratasRaadius = 150; // ruletiratta raadius
    private double keskX = 200;
    private double keskY = 200;
    private int täpneNumber = -1; // kui valitakse täpne number

    private final Random suvaline = new Random();  // suvalise numbri generaator

    private final int[] ruleti_ratta_numbrid = {
            0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13,
            36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20,
            14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26
    };


    // arvutab ratta mõõdud vastavalt akna suurusele ja joonistab sektorid
    public void uuendaMõõdud(Group ratasGrupp, Pane konteiner) {
        // arvutab ratta keskpunkti koordinaadid konteineri laiuse ja kõrguse põhjal
        keskX = konteiner.getWidth() / 2;
        keskY = konteiner.getHeight() / 2;

        // määrab ruletiratta raadiuse nii, et see mahuks konteinerisse (95% väiksema telje pikkusest)
        ratasRaadius = Math.min(keskX, keskY) * 0.95;

        // eemaldab eelnevad elemendid (sektorid ja tekstid), et uuesti joonistada
        ratasGrupp.getChildren().clear();

        // arvutab iga sektori suuruse kraadides (ruletiratta jagamine võrdseteks osadeks)
        double nurgaSamm = 360.0 / ruleti_ratta_numbrid.length;

        // loob iga numbri jaoks vastava sektori ja tekstielemendi
        for (int i = 0; i < ruleti_ratta_numbrid.length; i++) {
            int number = ruleti_ratta_numbrid[i]; // võtame järjekorras oleva ruletinumbri
            double algusNurk = i * nurgaSamm; // sektori algusnurk kraadides
            double lõppNurk = (i + 1) * nurgaSamm; // sektori lõppnurk kraadides

            // loob värvilise sektori antud nurkadega ja värviga vastavalt numbrile
            Path sektor = looSektor(algusNurk, lõppNurk, leiaSektoriVärv(number));

            // loob tekstielemendi numbri jaoks ja asetame selle sektori keskele
            Text tekst = looSektoriTekst(number, algusNurk + nurgaSamm / 2);

            // lisab sektori ja teksti ratta grupi (ehk kuvatavate objektide hulka)
            ratasGrupp.getChildren().addAll(sektor, tekst);
        }

        // määrab ratta pöördenurga, et säilitada õige visuaalne asend
        ratasGrupp.setRotate(praeguneNurk);
    }


    // noole muutmine ruletiratta suhtes
    public void paigutaNool(Polygon nool, Pane konteiner) {
        // arvutab dünaamilise kauguse ja mõõtkava
        double skaala = Math.min(konteiner.getWidth(), konteiner.getHeight()) / 400.0; // 400 on umbkaudne algsuurus
        nool.setScaleX(skaala);
        nool.setScaleY(skaala);

        double kaugus = Math.min(konteiner.getWidth(), konteiner.getHeight()) / 2 + 10;
        nool.setLayoutX(keskX + kaugus - 15 * skaala);
        nool.setLayoutY(keskY + 20 * skaala);
    }

    // loob sinise noole
    public Polygon looNool() {
        double noolSuurus = ratasRaadius * 0.2;
        Polygon nool = new Polygon(0.0, 0.0, -noolSuurus / 2, -noolSuurus, +noolSuurus / 2, -noolSuurus);
        nool.setFill(Color.BLUE);
        nool.setRotate(90);
        return nool;
    }


    // käivitab ratta, määrab tulemuse ja uuendab mängu seisu
    public void spin(Group ratasGrupp, String panusTekst, String panuseTüüp, Label saldoSilt, Label tulemusSilt, RulettLogi logi ) {
        int panus;

        // kontrollib kas on korrektne panus, ei sobi kui panus väiksem 0 või suurem saldost
        try {
            panus = Integer.parseInt(panusTekst);
            if (panus <= 0 || panus > saldo)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tulemusSilt.setText("Sisesta sobiv panus (positiivne ja mitte suurem kui saldo).");
            return;
        }

        // kui valiti täpne number, küsime mängijalt, mis numbri peale soovitakse panustada
        if (panuseTüüp.equals("Täpne number")) {
            TextInputDialog dialoog = new TextInputDialog();
            dialoog.setTitle("Täpne number");
            dialoog.setHeaderText("Sisesta number vahemikus 0–36");
            dialoog.setContentText("Number:");

            int sisestatud = -1; // muutuja, kuhu salvestab kasutaja sisestuse

            // küsib kasutajalt sisestust seni, kuni see on korrektne
            while (true) {
                TextInputDialog dialoog1 = new TextInputDialog();
                dialoog1.setTitle("Täpne number");
                dialoog1.setHeaderText("Sisesta number vahemikus 0–36");
                dialoog1.setContentText("Number:");

                try {
                    // loeb kasutaja sisestuse (või -1, kui ta vajutab cancel)
                    String sisend = dialoog1.showAndWait().orElse("-1");
                    sisestatud = Integer.parseInt(sisend);

                    // kontrollib, kas sisestatud number jääb vahemikku
                    if (sisestatud >= 0 && sisestatud <= 36) {
                        täpneNumber = sisestatud;
                        break; // sobiv number – lõpetame küsimise
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {

                    // kui sisestus on vale annab veateate
                    Alert hoiatus = new Alert(Alert.AlertType.ERROR);
                    hoiatus.setTitle("Vigane sisestus");
                    hoiatus.setHeaderText("Vigane number");
                    hoiatus.setContentText("Palun sisesta täisarv vahemikus 0 kuni 36.");
                    hoiatus.showAndWait();
                }
            }


        }

        // suvaline võidunumber vahemikus 0-36
        int võiduNumber = suvaline.nextInt(37);

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

        // arvutab, kui palju peab ratast keerama
        double nurkSektor = 360.0 / ruleti_ratta_numbrid.length;
        double sihtnurk = indeks * nurkSektor;

        // arvutab praeguse nurga
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
            boolean võit = kontrolliVõitu(võiduNumber, panuseTüüp);

            String värv;
            if (võiduNumber == 0) {
                värv = "roheline";
            } else if (onPunane(võiduNumber)) {
                värv = "punane";
            } else {
                värv = "must";
            }

            if (võit) { // kui on võit, suurendame saldot
                int koef = panuseTüüp.equals("Täpne number") ? 35 : 1;
                int võidusumma = panus * koef;
                saldo += võidusumma;

                tulemusSilt.setText("Pall jäi numbrile: " + võiduNumber + " (" + värv + "). Võitsid " + võidusumma + " €!");
                logi.salvestaLogisse("Võit", võiduNumber, värv);
            } else { // kui on kaotus, vähendame saldot
                saldo -= panus;
                tulemusSilt.setText("Pall jäi numbrile: " + võiduNumber + " (" + värv + "). Kahjuks kaotasid.");
                logi.salvestaLogisse("Kaotus", võiduNumber, värv);
            }

            // uuendab saldot
            saldoSilt.setText("Sinu saldo: " + saldo + " €");
        });
        pöörle.play();
    }


    // kontrollib, kas kasutaja võitis
    private boolean kontrolliVõitu(int number, String valitudPanus) {

        // kui panus on punane
        if (valitudPanus.equals("Punane")) {
            return onPunane(number);
        }
        // kui panus on must
        else if (valitudPanus.equals("Must")) {
            return number != 0 && !onPunane(number);
        }
        // kui panus on täpne number
        else if (valitudPanus.equals("Täpne number")) {
            return number == täpneNumber;
        }
        // kui panus on paaris
        else if (valitudPanus.equals("Paaris")) {
            return number != 0 && number % 2 == 0;
        }

        // kui panus on paaritu
        else if (valitudPanus.equals("Paaritu")) {
            return number % 2 == 1;
        }

        // kui panus on kõrge (19–36)
        else if (valitudPanus.equals("Kõrge (19–36)")) {
            return number >= 19;
        }

        // kui panus on madal (1–18)
        else if (valitudPanus.equals("Madal (1–18)")) {
            return number >= 1 && number <= 18;
        } else {
            return false; // kui ükski ei sobi
        }
    }


    // kontrollib, kas number on punane
    private boolean onPunane(int number) {
        int[] punased = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int p : punased)
            if (p == number)
                return true;
        return false;
    }


    // tagastab värvi vastavalt numbrile
    private Color leiaSektoriVärv(int number) {
        if (number == 0) {
            return Color.GREEN;
        } else if (onPunane(number)) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }


    // loob ratta lõigu kindla värviga
    private Path looSektor(double algus, double lõpp, Color värv) {
        Path tee = new Path();
        tee.setFill(värv);

        // alustab sektori joonistamist ratta keskpunktist (sektori üks tipp)
        tee.getElements().add(new MoveTo(keskX, keskY));

        // joonistab sirglõigu keskpunktist sektori algusnurgas olevasse punkti
        tee.getElements().add(new LineTo(
                keskX + ratasRaadius * Math.cos(Math.toRadians(algus)), // x koordinaat algusnurgas
                keskY + ratasRaadius * Math.sin(Math.toRadians(algus)))); // y koordinaat algusnurgas

        // joonistab kaare alguspunktist lõppnurgani mööda ratta serva
        tee.getElements().add(new ArcTo(
                ratasRaadius, ratasRaadius, // kaare raadius horisontaalselt ja vertikaalselt
                0, // x telje pöördenurk (ei muutu)
                keskX + ratasRaadius * Math.cos(Math.toRadians(lõpp)), // kaare lõpp-punkti x koordinaat
                keskY + ratasRaadius * Math.sin(Math.toRadians(lõpp)), // kaare lõpp-punkti y koordinaat
                false, // largeArcFlag – kas kaar peab olema suurem kui 180° (false = väiksem)
                false)); // sweepFlag – määrab suuna, kuidas kaar joonistatakse

        // viib joone tagasi keskpunkti, et sektori piir oleks suletud
        tee.getElements().add(new LineTo(keskX, keskY));
        return tee;
    }


    // loob sektori numbri teksti ja paneb õigesse kohta
    private Text looSektoriTekst(int number, double nurkKraadides) {
        Text tekst = new Text(String.valueOf(number));
        tekst.setFont(new Font(14 * (ratasRaadius / 150)));
        tekst.setFill(Color.WHITE);

        double raadius = ratasRaadius * 0.83; // kaugus keskpunktist
        double rad = Math.toRadians(nurkKraadides); // nurk radiaanidesse, et saaks -cos ja -sin teha

        // x ja y koordinaadid, kuhu tekst läheb
        double x = keskX + raadius * Math.cos(rad);
        double y = keskY + raadius * Math.sin(rad);

        // paigutab teksti nii, et see oleks keskpunktiga õigesti joondatud
        tekst.setLayoutX(x - tekst.getLayoutBounds().getWidth() / 2);
        tekst.setLayoutY(y + tekst.getLayoutBounds().getHeight() / 4);

        // pöörab teksti, et see sobiks sektorinurgaga visuaalset paremini
        tekst.setRotate(nurkKraadides - 270);
        return tekst;
    }
}
