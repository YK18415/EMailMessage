package radke.yannick.android.emailmessage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

public class NewPersonActivity extends AppCompatActivity {

    EditText editTextVorname;
    EditText editTextNachname;
    EditText editTextBeruf;
    EditText editTextEMailadress;
    Person person;

    String vorname;
    String nachname;
    String beruf;
    String emailadress;

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
                vorname = editTextVorname.getText().toString();
                nachname = editTextNachname.getText().toString();
                beruf = editTextBeruf.getText().toString();
                emailadress = editTextEMailadress.getText().toString();

                if(vorname.equals("") || nachname.equals("") || beruf.equals("") || emailadress.equals("")) {
                    Toast.makeText(NewPersonActivity.this, "Sie haben nicht alle Felder ausgefüllt.", Toast.LENGTH_LONG).show();
                } else {
                    addNewPerson();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("PERSON", (Serializable) person);
                    setResult(RESULT_OK, resultIntent);
                    finish(); // ends current activity
                }
            }
        });
    }

    private void addNewPerson() {
        person = new Person(vorname, nachname, beruf, emailadress);
     }
}
