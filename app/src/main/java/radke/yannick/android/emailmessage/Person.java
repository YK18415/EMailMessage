package radke.yannick.android.emailmessage;

import java.io.Serializable;

public class Person implements Serializable {

    private String vorname;
    private String nachname;
    private String beruf;
    private String emailadress;

    public Person(String vorname, String nachname, String beruf, String emailadress) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.beruf = beruf;
        this.emailadress = emailadress;
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

    public String getEmailadress() {
        return emailadress;
    }

    public void setEmailadress(String enailadress) {
        this.emailadress = enailadress;
    }

}
