public class Kaart {
    private String mast;
    private String nimetus;
    private int väärtus;

    public Kaart(String mast, String nimetus, int väärtus) {
        this.mast = mast;
        this.nimetus = nimetus;
        this.väärtus = väärtus;
    }

    public int getVäärtus() {
        return väärtus;
    }

    public String toString() {
        return nimetus + " " + mast;
    }
}

