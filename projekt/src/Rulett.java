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
        System.out.println("Sinu saldo: " + mängija.getRaha() + " eurot.");
        System.out.println("Sisesta panuse summa: ");
        int panuseSumma = scanner.nextInt();

        if (!mängija.panePanus(panuseSumma)) {
            System.out.println("Sul pole piisavalt raha panustamiseks.");
        }

        System.out.println("Vali panus:");
        System.out.println("1 - Punane");
        System.out.println("2 - Must");
        System.out.println("3 - Täpne number");
        System.out.println("Sinu valik: ");
        int panuseValik = scanner.nextInt();

        int keerutusetulemus = random.nextInt(37);
        boolean onPunane = kasPunane(keerutusetulemus);

        System.out.println("Ratas keerleb...");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Pall langes numbrile: " + keerutusetulemus);

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
        } else {
            System.out.println("Vale valik!");
            return;
        }

        if (võitis) {
            System.out.println("Palju õnne! Võitsid " + võiduSumma + " eurot.");
            mängija.lisaRaha(võiduSumma);
        } else {
            System.out.println("Kahjuks kaotasid panuse");

        }
        System.out.println("Sinu uus saldo on: " + mängija.getRaha());
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
