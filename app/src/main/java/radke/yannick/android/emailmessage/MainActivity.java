package radke.yannick.android.emailmessage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static Context context;

    EditText editTextReceiver;
    EditText editTextConcerning;
    Person newPerson;
    List<String> emailadressesList;
    String emailadressesArray[];
    List<Person> personList = new ArrayList<>();
    EditText editTextMessage;
    String s;

    //Storage:
    SharedPreferences.Editor editor;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        // Storage:
        settings = context.getSharedPreferences("userdetails", MODE_PRIVATE); // Zum Lesen.
        editor = settings.edit(); // Zum Schreiben.

        // Layout components:
        Button btnShowPeople = findViewById(R.id.btn_show_people);
        editTextConcerning = findViewById(R.id.editTextConcerning);
        editTextReceiver = findViewById(R.id.editTextReceiver);
        EditText editTextDate = findViewById(R.id.editTextDate);
        editTextMessage = findViewById(R.id.editTextMessage);

        // Date:
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String folderName = formatter.format(today);
        editTextDate.setText(folderName);

        // Add standard-persons (It is a kind of a seeder):
        personList.add(new Person("Kira", "Schatzi", "Student", "kira.begau@gmx.de"));
        personList.add(new Person("Kirsten", "Büggener", "VW", "k.bueggener@gmx.de"));

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
                // check, if there are at least one receiver:
                if(!editTextReceiver.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Die E-Mail wird gleich verschickt.", Toast.LENGTH_LONG).show();
                    sendEmail();
                } else {
                    Toast.makeText(MainActivity.this, "Sie haben keinen Empfänger angegeben.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void chooseReceivers() {
        // added the newPerson, who was created by the user in the popup-menu:
        if(newPerson != null && !personList.contains(newPerson)) {
            personList.add(newPerson);
        }

        // The method setMultiChoiceItmes wants to have an Array. So, the receivers-objects have to be converted, so that only the names are shown in the popup-menu.
        final String[] personsStringList = new String[personList.size()];
        emailadressesList = new ArrayList<>();

        for (int i = 0; i < personList.size(); i++) {
            personsStringList[i] = personList.get(i).getVorname() + " " + personList.get(i).getNachname();
        }

        final ArrayList personsSelected = new ArrayList();

        openPopupDialog(personsSelected, personsStringList);
    }

    private void openPopupDialog(final ArrayList personsSelected, final String[] personsStringList) {
        Dialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Empfängerwahl");
        builder.setMultiChoiceItems( personsStringList, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                if (isSelected) {
                    personsSelected.add(selectedItemId);
                    emailadressesList.add(personList.get(Integer.valueOf(selectedItemId)).getEmailadress());
                } else if (personsSelected.contains(selectedItemId)) {
                    personsSelected.remove(Integer.valueOf(selectedItemId));
                }
            }
        })
                // Three buttons:
                .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Add receivers the the 'Betreff':
                        addReceiversToConcerning(personsSelected, personsStringList);
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
                        Intent intent = new Intent(MainActivity.this, NewPersonActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    private void addReceiversToConcerning(ArrayList personsSelected, String[] personsStringList) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < personsSelected.size(); i++) {
            sb.append(personsStringList[(int) personsSelected.get(i)]);
            if(i+1 < personsSelected.size()) {
                sb.append("; ");
            }
        }

        editTextReceiver.setText(sb.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                newPerson = (Person) data.getExtras().getSerializable("PERSON");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Store the data:
        editor.putString("message", String.valueOf(editTextMessage.getText()));
        editor.putString("concerning", String.valueOf(editTextConcerning.getText()));
        editor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
         editTextMessage.setText(settings.getString("message",""));
         editTextConcerning.setText(settings.getString("concerning", ""));
    }

    protected void sendEmail() {

        String message = editTextMessage.getText().toString();
        String betreff = editTextConcerning.getText().toString();

        emailadressesArray = emailadressesList.toArray(new String[emailadressesList.size()]);
        String[] TO = emailadressesArray;

        /*
        String mailto = "mailto:bob@example.org.yannick.radke@gmx.de" +
                "?cc=" + "alice@example.com" +
                "&body=" + Uri.encode(message);


        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        //emailIntent.setData(Uri.parse(mailto));
        emailIntent.setData(Uri.parse("mailto:first.mail@gmail.com,second.mail@gmail.com"));
        //emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"user@example.com", "yannick.radke@gmx.de"});


        Intent emailIntent = new Intent(Intent.ACTION_SEND);//, Uri.parse("mailto:" + "yannick.radke@gmx.de")); //ACTION_SEND); // Oder ACTION_SENDTO, oder ACTION_MAILTO (Nur Mail-Programme.)
*/
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        // Ohne dem funktioniert es nicht, wieso?
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);       // A String[] holding e-mail addresses that should be delivered to.
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, betreff); // A constant string holding the (desired subject line == Betreffzeile) of a message.
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);


        try {
            startActivity(Intent.createChooser(emailIntent, "Verschicke E-Mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
