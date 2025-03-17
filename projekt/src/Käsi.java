import java.util.List;
import java.util.ArrayList;

public class Käsi {
    private List<Kaart> kaardid;

    public Käsi() {
        this.kaardid = new ArrayList<>();
    }

    public Kaart getEsimeneKaart() {
        return kaardid.get(0);
    }

    public void lisaKaart(Kaart kaart) {
        kaardid.add(kaart);
    }

    public int summa() {
        int summa = 0;
        int ässadeArv = 0;

        for (Kaart kaart : kaardid) {
            summa += kaart.getVäärtus();
            if (kaart.getVäärtus() == 11) {
                ässadeArv++;
            }
        }

        while (summa > 21 && ässadeArv > 0) {
            summa -= 10;
            ässadeArv--;
        }

        return summa;
    }

    public boolean onBlackjack() {
        return kaardid.size() == 2 && summa() == 21;
    }

    public boolean onLäbi() {
        return summa() > 21;
    }

    public String toString() {
        return kaardid.toString();
    }
}

