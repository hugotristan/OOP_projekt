package com.example.rulett_graafiline;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RulettGraafiline extends Application {
    private int saldo = 100;
    private final String logifail = "rulettilog.txt";
    private final Random random = new Random();

    private Label saldoLabel;
    private Label tulemusLabel;
    private TextField panusField;
    private ComboBox<String> panuseTüüp;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Graafiline Rulett");

        // Rulett-ratta pilt
        Image image = new Image(getClass().getResourceAsStream("/com/example/rulett_graafiline/ratas.jpg"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);

        // Nool (punane kolmnurk)
        Polygon nool = new Polygon(0, 0, -10, -30, 10, -30);
        nool.setFill(Color.RED);

        StackPane ratasPane = new StackPane(imageView, nool);
        StackPane.setAlignment(nool, Pos.TOP_CENTER);

        // Kontrollid
        saldoLabel = new Label("Sinu saldo: 100 €");
        panusField = new TextField();
        panusField.setPromptText("Panuse summa");

        panuseTüüp = new ComboBox<>();
        panuseTüüp.getItems().addAll(
                "Punane", "Must", "Täpne number", "Paaris", "Paaritu", "Kõrge (19-36)", "Madal (1-18)"
        );
        panuseTüüp.setValue("Punane");

        Button spinButton = new Button("SPIN");
        spinButton.setOnAction(e -> spin(imageView));

        tulemusLabel = new Label("Pane panus ja vajuta SPIN.");
        tulemusLabel.setWrapText(true);
        tulemusLabel.setMaxWidth(300);
        tulemusLabel.setStyle("-fx-font-size: 14px;");

        VBox controls = new VBox(10, saldoLabel, panusField, panuseTüüp, spinButton, tulemusLabel);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15));

        BorderPane root = new BorderPane();
        root.setCenter(ratasPane);
        root.setBottom(controls);

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void spin(ImageView imageView) {
        int panus;
        try {
            panus = Integer.parseInt(panusField.getText());
            if (panus <= 0 || panus > saldo) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tulemusLabel.setText("Sisesta sobiv panus (positiivne ja mitte suurem kui saldo).");
            return;
        }

        int number = random.nextInt(37);
        int rotation = 360 * (3 + random.nextInt(3)) + number * (360 / 37);

        RotateTransition rt = new RotateTransition(Duration.seconds(3), imageView);
        rt.setByAngle(rotation);
        rt.setOnFinished(e -> {
            boolean võit = kontrolliVõitu(number);
            String värv = number == 0 ? "roheline" : kasPunane(number) ? "punane" : "must";

            if (võit) {
                int võitSumma = panus * (panuseTüüp.getValue().equals("Täpne number") ? 35 : 2);
                saldo += võitSumma;
                tulemusLabel.setText("Pall langes numbrile: " + number + " (" + värv + "). Võitsid " + võitSumma + " €!");
                salvestaLogisse("Võit: " + number);
            } else {
                saldo -= panus;
                tulemusLabel.setText("Pall langes numbrile: " + number + " (" + värv + "). Kahjuks kaotasid.");
                salvestaLogisse("Kaotus: " + number);
            }
            saldoLabel.setText("Sinu saldo: " + saldo + " €");
        });
        rt.play();
    }

    private boolean kontrolliVõitu(int number) {
        String tüüp = panuseTüüp.getValue();
        return switch (tüüp) {
            case "Punane" -> kasPunane(number);
            case "Must" -> number != 0 && !kasPunane(number);
            case "Täpne number" -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Sisesta number");
                dialog.setHeaderText("Sisesta number 0-36");
                dialog.setContentText("Number:");
                int sisestatud = -1;
                try {
                    sisestatud = Integer.parseInt(dialog.showAndWait().orElse("-1"));
                } catch (Exception ignored) {}
                yield sisestatud == number;
            }
            case "Paaris" -> number != 0 && number % 2 == 0;
            case "Paaritu" -> number % 2 == 1;
            case "Kõrge (19-36)" -> number >= 19;
            case "Madal (1-18)" -> number >= 1 && number <= 18;
            default -> false;
        };
    }

    private boolean kasPunane(int number) {
        int[] punased = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int p : punased) if (p == number) return true;
        return false;
    }

    private void salvestaLogisse(String rida) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logifail, true))) {
            writer.write(rida);
            writer.newLine();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Logi salvestamise viga");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
