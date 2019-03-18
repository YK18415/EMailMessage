package radke.yannick.android.emailmessage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextReceiver;
    EditText editTextConcerning;
    Person[] persons;
    String emailadresses[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Layout components:
        Button btnShowPeople = findViewById(R.id.btn_show_people);
        editTextConcerning = findViewById(R.id.editTextConcerning);
        editTextReceiver = findViewById(R.id.editTextReceiver);
        EditText editTextDate = findViewById(R.id.editTextDate);


        // Date:
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String folderName = formatter.format(today);
        editTextDate.setText(folderName);

        // Person-choose:
        btnShowPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseReceivers();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Die E-Mail wurde verschickt.", Toast.LENGTH_LONG).show();
                sendEmail();
            }
        });
    }

    private void chooseReceivers() {

        // The method setMultiChoiceItmes wants to have an Array. So, the receivers-objects have to be converted, so that only the names are shown in the popup-menu.
        final Person[] receivers = fillAndGetReceivers();
        final String[] personsStringList = new String[receivers.length];
        emailadresses = new String[receivers.length];

        for (int i = 0; i < receivers.length; i++) {
            personsStringList[i] = receivers[i].getVorname() + " " + receivers[i].getNachname();
            emailadresses[i] = receivers[i].getEmailadress();
        }

        final ArrayList personsSelected = new ArrayList();

        Dialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(" Empfängerwahl");
        builder.setMultiChoiceItems( personsStringList, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                if (isSelected) {
                    personsSelected.add(selectedItemId);
                } else if (personsSelected.contains(selectedItemId)) {
                    personsSelected.remove(Integer.valueOf(selectedItemId));
                }
            }
        })
                // Two buttons:
                .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Add receivers the the 'Betreff':
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < personsSelected.size(); i++) {
                            sb.append(personsStringList[(int) personsSelected.get(i)]);
                            if(i+1 < personsSelected.size()) {
                                sb.append("; ");
                            }
                        }

                        editTextReceiver.setText(sb.toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "Abgebrochen", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Neue Person", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "Neue Activity soll gestartet werden.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, NewPersonActivity.class);
                        startActivityForResult(intent, 1);
                        // startActivity(intent);

                        // Get the new Persons:

                        // Add this to the persons[]:

                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Person newPerson = (Person) data.getExtras().getSerializable("PERSON"); // getParcelableExtra("object");
            }
        }
    }

    private Person[] fillAndGetReceivers() {
        Person[] persons = {new Person("Kira", "Schatzi", "Student", "kira.begau@gmx.de"),
                new Person("Kirsten", "Büggener", "VW", "k.bueggener@gmx.de")};

        return persons;
    }

    protected void sendEmail() {
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        String message = editTextMessage.getText().toString();
        String betreff = editTextConcerning.getText().toString();

        String[] TO = emailadresses;
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        // Ohne dem funktioniert es nicht, wieso?
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);       // A String[] holding e-mail addresses that should be delivered to.
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, betreff); // A constant string holding the (desired subject line == Betreffzeile) of a message.
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
