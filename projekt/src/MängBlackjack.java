import java.util.Scanner;

public class MängBlackjack {
    private Pakk pakk;
    private MängijaBlackjack mängija;
    private Diiler diiler;
    private Scanner scanner;
    private boolean esimeneKord;

    public MängBlackjack(KasiinoMängija kasiinoMängija) {
        pakk = new Pakk();
        mängija = new MängijaBlackjack(kasiinoMängija);
        diiler = new Diiler();
        scanner = new Scanner(System.in);
    }

    public void mäng() {
        System.out.println("--- BLACKJACK ---");

        boolean mängKäib = true;
        while (mängKäib) {
            System.out.println("\nSinu saldo: " + mängija.getRaha() + " eurot.");
            System.out.print("Sisesta panuse summa (0, et lahkuda): ");
            int panuseSumma = scanner.nextInt();

            if (panuseSumma == 0) {
                System.out.println("Lahkud blackjacki lauast. Näeme varsti jälle!");
                break;
            }
            if (!mängija.panusta(panuseSumma)) {
                continue;
            }

            diiler = new Diiler();
            mängija.nulliKäed();
            Käsi mängijaKäsi = mängija.getKäsi();
            mängijaKäsi.lisaKaart(pakk.jagaKaart());
            mängijaKäsi.lisaKaart(pakk.jagaKaart());
            diiler.getKäsi().lisaKaart(pakk.jagaKaart());
            diiler.getKäsi().lisaKaart(pakk.jagaKaart());


            System.out.println("Sinu käsi: " + mängijaKäsi + " (Summa: " + mängijaKäsi.summa() + ")");
            System.out.println("Diileri avatud kaart: " + diiler.getKäsi().getEsimeneKaart());

            mängijaKäik(mängijaKäsi);
            if (!mängijaKäsi.onLäbi()) {
                diiler.mängi(pakk);
                System.out.println("Diileri käsi: " + diiler.getKäsi() + " (Summa: " + diiler.getKäsi().summa() + ")");
            }

            kontrolliVõitjat(mängijaKäsi);
            System.out.println("Sinu uus saldo on: " + mängija.getRaha());
        }
    }

    private void mängijaKäik(Käsi käsi) {
        if (käsi.summa() == 21) {
            System.out.println("Said 21!");
            return;
        }
        esimeneKord = true;
        while (esimeneKord) {
            System.out.print("Vali: (1) Hit, (2) Stand: ");
            int valik = scanner.nextInt();

            if (valik == 1) {
                käsi.lisaKaart(pakk.jagaKaart());
                System.out.println("Sinu uus käsi: " + käsi + " (Summa: " + käsi.summa() + ")");
                if (käsi.onLäbi()) {
                    System.out.println("Läksid üle 21! Kaotasid :(");
                    return;
                }
            } else if (valik == 2) {
                break;
            } else {
                System.out.println("Vale valik! Proovi uuesti.");
            }
        }
    }

    private void kontrolliVõitjat(Käsi käsi) {
        if (käsi.onLäbi()) {
            return;
        }

        int mängijaSumma = käsi.summa();
        int diileriSumma = diiler.getKäsi().summa();

        if (diileriSumma > 21) {
            System.out.println("Diiler läks üle 21! Sina võitsid!");
            int võidusumma = mängija.getPanus() * 2;
            System.out.println("Teenisid " + (võidusumma / 2) + " eurot.");
            mängija.võidaPanus(2);

        } else if (mängijaSumma > diileriSumma) {
            int võidusumma = mängija.getPanus() * 2;
            System.out.println("Teenisid " + (võidusumma / 2) + " eurot.");
            mängija.võidaPanus(2);

        } else if (mängijaSumma == diileriSumma) {
            System.out.println("Viik! Saad oma panuse tagasi.");
            mängija.võidaPanus(1);

        } else {
            System.out.println("Diiler võitis!");
        }
    }
}
