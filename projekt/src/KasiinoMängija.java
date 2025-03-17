public class KasiinoMängija {
    private String nimi;
    private int raha;

    public KasiinoMängija(String nimi, int algneRaha) {
        this.nimi = nimi;
        this.raha = algneRaha;
    }

    public int getRaha() {
        return raha;
    }

    public void lisaRaha(int summa) {
        raha += summa;
    }

    public boolean panePanus(int summa) {
        if (summa <= 0){
            System.out.println("Panus peab olema positiivne number.");
        return false;
        }
        if (summa > raha) {
            System.out.println("Kahjuks pole piisavalt raha...");
            return false;
        }
        raha -= summa;
        return true;
    }

    public String getNimi() {

        return nimi;
    }
}

