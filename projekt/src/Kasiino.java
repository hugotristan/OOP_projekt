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
    }
}
