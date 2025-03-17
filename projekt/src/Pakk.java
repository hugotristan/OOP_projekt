import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Pakk {
    private List<Kaart> kaardid;
    private Random rand = new Random();

    public Pakk() {
        String[] mastid = {"Ärtu", "Ruutu", "Risti", "Poti"};
        String[] nimetused = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Poiss", "Emand", "Kuningas", "Äss"};
        int[] väärtused = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};

        kaardid = new ArrayList<>();
        for (String mast : mastid) {
            for (int i = 0; i < nimetused.length; i++) {
                kaardid.add(new Kaart(mast, nimetused[i], väärtused[i]));
            }
        }
        sega();
    }

    public void sega() {
        Collections.shuffle(kaardid, rand);
    }

    public Kaart jagaKaart() {
        return kaardid.remove(0);
    }
}

