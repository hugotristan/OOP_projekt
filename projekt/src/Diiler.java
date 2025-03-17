public class Diiler {
    private Käsi käsi;

    public Diiler() {
        this.käsi = new Käsi();
    }

    public void mängi(Pakk pakk) {
        while (käsi.summa() < 17) {
            käsi.lisaKaart(pakk.jagaKaart());
        }
    }

    public Käsi getKäsi() {
        return käsi;
    }
}

