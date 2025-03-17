import java.util.Scanner;

public class Kasiino {
    private KasiinoMängija mängija;
    private Scanner scanner;

    public Kasiino() {
        scanner = new Scanner(System.in);
        System.out.println("Sisesta oma nimi: ");
        String nimi = scanner.nextLine();
        mängija = new KasiinoMängija(nimi, 100);
    }

    public void alusta(){
        System.out.println("Tere tulemast kasiinosse, " + mängija.getNimi() + "!");

        boolean töötab = true;
        while (töötab){
            System.out.println("Mida soovid mängida:");
            System.out.println("1 - Blackjack");
            System.out.println("2 - Rulett");
            System.out.println("3 - lahku kasiinost");
            System.out.println("Valik: ");

            int valik = scanner.nextInt();

            if (valik < 1 || valik > 3) {
                System.out.println("Vale valik! Palun vali number 1-3.");
                continue;
            }

            if (valik== 1) {
                MängBlackjack blackjack = new MängBlackjack(mängija);
                blackjack.mäng();
            } else if (valik == 2) {
                Rulett rulett = new Rulett(mängija);
                rulett.mäng();
            } else if (valik == 3) {
                System.out.println("Aitäh mängimast! Lahkud kasiinost.");
                System.out.println(mängija);
                töötab = false;
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Kasiino kasiino = new Kasiino();
        kasiino.alusta();
    }
}
