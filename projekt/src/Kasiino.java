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
            System.out.println("Mida soovid mängida=");
            System.out.println("1 - Blackjack");
            System.out.println("2 - Rulett");
            System.out.println("3 - lahku kasiinost");
            System.out.println("Valik: ");

            int choice = scanner.nextInt();

            if (choice == 1) {
                System.out.println("Blackjack pole veel valmis!"); // Hiljem lisame mängu
            } else if (choice == 2) {
                Rulett rulett = new Rulett(mängija);
                rulett.mäng();
                break;
            } else if (choice == 3) {
                System.out.println("Aitäh mängimast! Lahkud kasiinost.");
                töötab = false;
            } else {
                System.out.println("Vale valik! Proovi uuesti.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Kasiino kasiino = new Kasiino();
        kasiino.alusta();
    }
}
