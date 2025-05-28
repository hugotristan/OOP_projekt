package com.example.rulettgraafiline;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;


public class RulettPeaklass extends Application {

    private final RulettLoogika loogika = new RulettLoogika();
    private final RulettLogi logi = new RulettLogi();

    private Label saldoSilt; // näitab saldot
    private Label tulemusSilt; // näitab spini tulemust
    private TextField panuseVäli; // sisend panuse jaoks
    private ComboBox<String> panuseTüüp; // panuse tüübi valik


    // loob ja kuvab FX kasutajaliideses
    public void start(Stage lava) {
        lava.setTitle("Graafiline Rulett");

        Group ratasGrupp = new Group();
        StackPane ratasKonteiner = new StackPane();
        Pane paigutusPane = new Pane(ratasGrupp);
        ratasKonteiner.getChildren().add(paigutusPane);

        // sinine nool
        Polygon nool = loogika.looNool();
        paigutusPane.getChildren().add(nool);

        // uuendab suurust ja paigutab noole õigesse kohta, kui aken muutub
        ratasKonteiner.widthProperty().addListener((obs, oldVal, newVal) -> {
            loogika.uuendaMõõdud(ratasGrupp, ratasKonteiner);
            loogika.paigutaNool(nool, ratasKonteiner);
        });
        ratasKonteiner.heightProperty().addListener((obs, oldVal, newVal) -> {
            loogika.uuendaMõõdud(ratasGrupp, ratasKonteiner);
            loogika.paigutaNool(nool, ratasKonteiner);
        });

        // alumine paneel
        VBox juhtpaneel = looJuhtpaneel(ratasGrupp);

        BorderPane juur = new BorderPane();
        juur.setCenter(ratasKonteiner);
        juur.setBottom(juhtpaneel);

        Scene stseen = new Scene(juur, 600, 700);
        lava.setScene(stseen);
        loogika.uuendaMõõdud(ratasGrupp, ratasKonteiner);
        loogika.paigutaNool(nool, ratasKonteiner);
        lava.show();
    }


    // loob juhtpaneeli
    private VBox looJuhtpaneel(Group ratasGrupp) {
        saldoSilt = new Label("Sinu saldo: 100 €");
        panuseVäli = new TextField();
        panuseVäli.setPromptText("Panuse summa");

        panuseTüüp = new ComboBox<>();
        panuseTüüp.getItems().addAll("Punane", "Must", "Täpne number", "Paaris", "Paaritu", "Kõrge (19–36)", "Madal (1–18)");
        panuseTüüp.setValue("Punane");

        Button spinNupp = new Button("SPIN");
        spinNupp.setOnAction(e -> loogika.spin(
                ratasGrupp,
                panuseVäli.getText(),
                panuseTüüp.getValue(),
                saldoSilt,
                tulemusSilt,
                logi
        ));

        Button logiNupp = new Button("Vaata logi");
        logiNupp.setOnAction(e -> logi.kuvaLogi());

        tulemusSilt = new Label("Pane panus ja vajuta SPIN.");
        tulemusSilt.setWrapText(true);
        tulemusSilt.setMaxWidth(300);
        tulemusSilt.setAlignment(Pos.CENTER);
        tulemusSilt.setStyle("-fx-font-size: 14px;");

        VBox paneel = new VBox(10, saldoSilt, panuseVäli, panuseTüüp, spinNupp, logiNupp, tulemusSilt);
        paneel.setAlignment(Pos.CENTER);
        paneel.setPadding(new Insets(15));
        return paneel;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
