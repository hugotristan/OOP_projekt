import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Rulett {
    private KasiinoMängija mängija;
    private Scanner scanner;
    private Random random;

    public Rulett(KasiinoMängija mängija) {
        this.mängija = mängija;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void mäng() {
        System.out.println("--- RULETT ---");

        boolean mängKäib = true;
        while (mängKäib) {
            System.out.println("\nSinu saldo: " + mängija.getRaha() + " eurot.");
            System.out.println("Sisesta panuse summa (0, et lahkuda): ");

            int panuseSumma = 0;
            boolean õigeSisend = false;

            // Keep asking for the input until the user enters a valid integer
            while (!õigeSisend) {
                try {
                    panuseSumma = scanner.nextInt();
                    õigeSisend = true;
                } catch (InputMismatchException e) {
                    System.out.println("Sisestatud väärtus ei ole korrektne! Palun sisesta täisarv.");
                    scanner.nextLine();  // Clear the invalid input
                }
            }

            if (panuseSumma == 0) {
                System.out.println("Lahkud ruletilauast.");
                break;
            }
            if (!mängija.panePanus(panuseSumma)) {
                System.out.println("Sul pole piisavalt raha panustamiseks.");
                continue;
            }

            System.out.println("Vali panus:");
            System.out.println("1 - Punane");
            System.out.println("2 - Must");
            System.out.println("3 - Täpne number");
            System.out.println("4 - Paaris arv");
            System.out.println("5 - Paaritu arv");
            System.out.println("6 - Kõrged arvud (19-36)");
            System.out.println("7 - Madalad arvud (1-18)");
            System.out.println("Sinu valik: ");
            int panuseValik = scanner.nextInt();

            if (panuseValik < 1 || panuseValik > 7) {
                System.out.println("Vale valik! Palun vali number 1-7.");
                mängija.lisaRaha(panuseSumma);
                continue;
            }
            int keerutusetulemus = random.nextInt(37);
            boolean onPunane = kasPunane(keerutusetulemus);

            System.out.println("Ratas keerleb...");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean onKõrge = keerutusetulemus > 18;
            boolean onPaaris = keerutusetulemus % 2 == 0;

            boolean võitis = false;
            int võiduSumma = 0;

            if (panuseValik == 1) {
                if (onPunane) {
                    võitis = true;
                    võiduSumma = panuseSumma * 2;
                }
            } else if (panuseValik == 2) {
                if (!onPunane && keerutusetulemus != 0) {
                    võitis = true;
                    võiduSumma = panuseSumma * 2;
                }
            } else if (panuseValik == 3) {
                System.out.println("Sisesta number vahemikus 0-36: ");
                int mängijaNumber = scanner.nextInt();
                if (mängijaNumber == keerutusetulemus) {
                    võitis = true;
                    võiduSumma = panuseSumma * 35;
                }
            }
            else if (panuseValik == 4) {
                if(onPaaris) {
                    võitis = true;
                    võiduSumma = panuseSumma * 2;
                }

            } else if (panuseValik == 5) {
                if(!onPaaris && keerutusetulemus != 0) {
                    võitis = true;
                    võiduSumma = panuseSumma * 2;
                }
            }
            else if (panuseValik == 6) {
                if (onKõrge) {
                    võitis = true;
                    võiduSumma = panuseSumma * 2;
                }
            }
            else if (panuseValik == 7) {
                if (!onKõrge) {
                    võitis = true;
                    võiduSumma = panuseSumma * 2;
                }
            }

            System.out.println("Pall langes numbrile: " + keerutusetulemus);

            if (võitis) {
                System.out.println("Palju õnne! Võitsid " + võiduSumma + " eurot.");
                mängija.lisaRaha(võiduSumma);
            } else {
                System.out.println("Kahjuks kaotasid panuse");

            }
        }
    }

    private boolean kasPunane ( int number){

        int[] punased = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int punane : punased) {
            if (punane == number) {
                return true;
            }
        }
        return false;
    }

}
