package radke.yannick.android.emailmessage;

public class Person {

    private String vorname;
    private String nachname;
    private String beruf;

    public Person(String vorname, String nachname, String beruf) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.beruf = beruf;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getBeruf() {
        return beruf;
    }

    public void setBeruf(String beruf) {
        this.beruf = beruf;
    }

}
