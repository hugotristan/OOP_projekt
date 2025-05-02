package com.example.rulett_graafiline;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RulettGraafiline extends Application {
    private int saldo = 100;
    private double currentAngle = 0; // Add this to the class
    private final String logifail = "rulettilog.txt";
    private final Random random = new Random();

    private Label saldoLabel;
    private Label tulemusLabel;
    private TextField panusField;
    private ComboBox<String> panuseTüüp;

    private static final double WHEEL_RADIUS = 150;
    private static final double CENTER_X = 200;
    private static final double CENTER_Y = 200;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Graafiline Rulett");

        // Create the wheel with sectors
        Group wheelGroup = new Group();
        int numSectors = 37; // 37 sectors, 0 to 36
        double angleStep = 360.0 / numSectors; // Angle for each sector

        int[] EUROPEAN_ORDER = {
                0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8,
                23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12,
                35, 3, 26
        };

        for (int i = 0; i < EUROPEAN_ORDER.length; i++) {
            int number = EUROPEAN_ORDER[i];
            double startAngle = i * angleStep;
            double endAngle = (i + 1) * angleStep;

            Color sectorColor = determineSectorColor(number);
            Path sector = createSectorPath(startAngle, endAngle, sectorColor);
            wheelGroup.getChildren().add(sector);

            Text sectorNumber = createSectorNumber(number, startAngle + angleStep / 2);
            wheelGroup.getChildren().add(sectorNumber);
        }

        wheelGroup.setRotate(355); // Adjusts so that sector 0 points upward (aligned with the red arrow)
        currentAngle = 355; // Important to initialize this too!

        // Nool (punane kolmnurk) - now on the right side pointing to center
        Polygon nool = new Polygon(0, 0, -10, -30, 10, -30);
        nool.setFill(Color.RED);
        nool.setRotate(90); // Point to the left

        StackPane ratasPane = new StackPane(wheelGroup, nool);
        StackPane.setAlignment(nool, Pos.CENTER_RIGHT); // Place it on the right side
        StackPane.setMargin(nool, new Insets(0, 60, 0, 0)); // Move it toward center


        // Controls
        saldoLabel = new Label("Sinu saldo: 100 €");
        panusField = new TextField();
        panusField.setPromptText("Panuse summa");

        panuseTüüp = new ComboBox<>();
        panuseTüüp.getItems().addAll(
                "Punane", "Must", "Täpne number", "Paaris", "Paaritu", "Kõrge (19-36)", "Madal (1-18)"
        );
        panuseTüüp.setValue("Punane");

        Button spinButton = new Button("SPIN");
        spinButton.setOnAction(e -> spin(wheelGroup));

        Button viewLogButton = new Button("Vaata logi");
        viewLogButton.setOnAction(e -> kuvaLogi());

        tulemusLabel = new Label("Pane panus ja vajuta SPIN.");
        tulemusLabel.setWrapText(true);
        tulemusLabel.setMaxWidth(300);
        tulemusLabel.setStyle("-fx-font-size: 14px;");

        VBox controls = new VBox(10, saldoLabel, panusField, panuseTüüp, spinButton, viewLogButton, tulemusLabel);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15));

        BorderPane root = new BorderPane();
        root.setCenter(ratasPane);
        root.setBottom(controls);

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void spin(Group wheelGroup) {
        int[] EUROPEAN_ORDER = {
                0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8,
                23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12,
                35, 3, 26
        };

        int panus;
        try {
            panus = Integer.parseInt(panusField.getText());
            if (panus <= 0 || panus > saldo) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tulemusLabel.setText("Sisesta sobiv panus (positiivne ja mitte suurem kui saldo).");
            return;
        }

        int number = random.nextInt(37);
        int index = -1;
        for (int i = 0; i < 37; i++) {
            if (EUROPEAN_ORDER[i] == number) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            tulemusLabel.setText("Viga: numberit ei leitud.");
            return;
        }

        double anglePerNumber = 360.0 / 37;
        double targetAngle = 360 - index * anglePerNumber;


        // Add full rotations for smoothness
        double spinRotation = 360 * 5 + targetAngle - (currentAngle % 360)-5;

        RotateTransition rt = new RotateTransition(Duration.seconds(3), wheelGroup);
        rt.setByAngle(spinRotation);
        rt.setOnFinished(e -> {
            currentAngle += spinRotation; // Update total rotation
            boolean võit = kontrolliVõitu(number);
            String värv = number == 0 ? "roheline" : kasPunane(number) ? "punane" : "must";

            if (võit) {
                int võitSumma = panus * (panuseTüüp.getValue().equals("Täpne number") ? 35 : 2);
                saldo += võitSumma;
                tulemusLabel.setText("Pall langes numbrile: " + number + " (" + värv + "). Võitsid " + võitSumma + " €!");
                salvestaLogisse("Võit: ", number, värv);
            } else {
                saldo -= panus;
                tulemusLabel.setText("Pall langes numbrile: " + number + " (" + värv + "). Kahjuks kaotasid.");
                salvestaLogisse("Kaotus: ", number, värv);
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

    private void salvestaLogisse(String tulemus, int number, String värv) {
        String ajatempel = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logiRida = String.format("%s - %s: %d (%s)", ajatempel, tulemus, number, värv);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logifail, true))) {
            writer.write(logiRida);
            writer.newLine();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Logi salvestamise viga");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void kuvaLogi() {
        StringBuilder logiSisu = new StringBuilder();
        try (java.util.Scanner scanner = new java.util.Scanner(new java.io.File(logifail))) {
            while (scanner.hasNextLine()) {
                logiSisu.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            logiSisu.append("Logi ei leitud või ei saa lugeda.");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mängulogi");
        alert.setHeaderText("Varasemad tulemused:");

        TextArea textArea = new TextArea(logiSisu.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(300);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    // Determine color for each sector
    private Color determineSectorColor(int number) {
        if (number == 0) {
            return Color.GREEN;
        } else if (kasPunane(number)) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }


    // Create the path for each sector with proper circular design
    private Path createSectorPath(double startAngle, double endAngle, Color color) {
        Path path = new Path();
        path.setFill(color);

        // Move to the center of the circle
        path.getElements().add(new MoveTo(CENTER_X, CENTER_Y));

        // Draw the first line to the outer edge of the sector
        path.getElements().add(new LineTo(
                CENTER_X + WHEEL_RADIUS * Math.cos(Math.toRadians(startAngle)),
                CENTER_Y + WHEEL_RADIUS * Math.sin(Math.toRadians(startAngle))
        ));

        // Draw the arc for the sector
        path.getElements().add(new ArcTo(
                WHEEL_RADIUS, WHEEL_RADIUS, 0,
                CENTER_X + WHEEL_RADIUS * Math.cos(Math.toRadians(endAngle)),
                CENTER_Y + WHEEL_RADIUS * Math.sin(Math.toRadians(endAngle)),
                false, false
        ));

        // Close the path by drawing a line back to the center
        path.getElements().add(new LineTo(CENTER_X, CENTER_Y));

        // Return the path object
        return path;
    }

    // Create the number text for each sector with correct rotation
    private Text createSectorNumber(int number, double angle) {
        Text text = new Text(String.valueOf(number));
        text.setFont(new Font(14));
        text.setFill(Color.WHITE);

        // Calculate position with inward offset to avoid edge clipping
        double textRadius = WHEEL_RADIUS - 25;
        double rad = Math.toRadians(angle);
        double x = CENTER_X + textRadius * Math.cos(rad);
        double y = CENTER_Y + textRadius * Math.sin(rad);

        // Position the text
        text.setLayoutX(x - text.getLayoutBounds().getWidth() / 2);
        text.setLayoutY(y + text.getLayoutBounds().getHeight() / 4);

        // Rotate perpendicular to the wheel radius (no flipping)
        text.setRotate(angle - 270);

        return text;
    }



    public static void main(String[] args) {
        launch(args);
    }
}