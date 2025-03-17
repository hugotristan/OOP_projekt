import java.util.List;
import java.util.ArrayList;

public class MängijaBlackjack {
    private KasiinoMängija kasiinoMängija;
    private int panus;
    private List<Käsi> käed;

    public MängijaBlackjack(KasiinoMängija kasiinoMängija) {
        this.kasiinoMängija = kasiinoMängija;
        this.käed = new ArrayList<>();
        this.käed.add(new Käsi());
    }

    public boolean panusta(int summa) {
        if (kasiinoMängija.panePanus(summa)) {
            panus = summa;
            return true;
        }
        return false;
    }

    public void võidaPanus(double kordaja) {
        kasiinoMängija.lisaRaha((int) (panus * kordaja));
    }

    public int getRaha() {
        return kasiinoMängija.getRaha();
    }

    public void nulliKäed() {
        käed.clear();
        käed.add(new Käsi());
    }

    public Käsi getKäsi() {
        return käed.get(0);
    }

    public int getPanus() {
        return panus;
    }
}

