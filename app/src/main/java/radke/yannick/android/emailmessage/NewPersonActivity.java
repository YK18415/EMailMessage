package radke.yannick.android.emailmessage;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;

public class NewPersonActivity extends AppCompatActivity {

    EditText editTextVorname;
    EditText editTextNachname;
    EditText editTextBeruf;
    EditText editTextEMailadress;
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_person);

        editTextVorname = findViewById(R.id.editTextVorname);
        editTextNachname = findViewById(R.id.editTextNachname);
        editTextBeruf = findViewById(R.id.editTextBeruf);
        editTextEMailadress = findViewById(R.id.editTextEMailadress);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPerson();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("PERSON", (Serializable) person); // data is the new person.
                setResult(RESULT_OK, resultIntent);
                finish(); // ends current activity
            }
        });
    }

    private void addNewPerson() {
        String vorname = editTextVorname.getText().toString();
        String nachname = editTextNachname.getText().toString();
        String beruf = editTextBeruf.getText().toString();
        String emailadress = editTextEMailadress.getText().toString();

        person = new Person(vorname, nachname, beruf, emailadress);
    }
}
