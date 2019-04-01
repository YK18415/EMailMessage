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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
    TextView textViewDate;

    ArrayList personsSelected = new ArrayList();
    boolean[] personSelectedBoolean;

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
        textViewDate = findViewById(R.id.textViewDate);
        setEditTextDate(textViewDate);
        editTextMessage = findViewById(R.id.editTextMessage);

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

    private void setEditTextDate(TextView textViewDate) {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String folderName = formatter.format(today);
        textViewDate.setText(folderName);
    }

    private void chooseReceivers() {
        loadPersonList();

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

        //Load personSelected:
        List<String> personSelectedStorage = new ArrayList<>();

        // Get saved string data in it.
        String personSelectedStorageString = settings.getString("personsSelected", "");

        // Create Gson object and translate the json string to related java object array.
        Gson gson = new Gson();
        int personSelectedStorArray[] = gson.fromJson(personSelectedStorageString, int[].class);

        if(personSelectedStorArray != null) {
            for (int personSelectedStorageItem: personSelectedStorArray) {
                if(!personsSelected.contains(personSelectedStorageItem)) {
                    personsSelected.add(personSelectedStorageItem);
                }
            }
        }

        personSelectedBoolean = new boolean[personsStringList.length];
        int i = 0;
        int j = 0;

        try{
            for (i = 0; i < personsSelected.size(); i++) {     // Über personSelected iterieren.
                for(j = 0; j < personSelectedBoolean.length; j++) {   // Über personSelectedBoolean iterieren.
                    if(j == (int)personsSelected.get(i)) {
                        personSelectedBoolean[j] = true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("I: " + i + " J: " + j);
            System.out.println(e.getStackTrace().toString());
        }


        openPopupDialog(personsSelected, personsStringList, personSelectedBoolean);
    }

    private void openPopupDialog(final ArrayList<Integer> personsSelected, final String[] personsStringList, boolean[] personSelectedBoolean) {
        Dialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Empfängerwahl");
        builder.setMultiChoiceItems( personsStringList, personSelectedBoolean, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                if (isSelected) {
                   // if(!personList.contains(selectedItemId)) {
                        personsSelected.add(selectedItemId);

                    //}
                } else if (personsSelected.contains(selectedItemId)) {
                    personsSelected.remove(Integer.valueOf(selectedItemId));
                }
            }
        })
                // Three buttons:
                .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (Integer personSelectedItem: personsSelected) {
                            emailadressesList.add(personList.get(personSelectedItem).getEmailadress());
                        }

                        // Add receivers the the 'Betreff':
                        addReceiversToConcerning(personsSelected, personsStringList);
                        // Store personsSelected:
                        Gson gson = new Gson();
                        String personSelectedStor = gson.toJson(personsSelected);

                        editor.putString("personsSelected", personSelectedStor);

                        editor.commit();
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

    private void loadPersonList() {
        // Get saved string data in it.
        String popupReceiverListString = settings.getString("PERSONLIST", "");

        // Create Gson object and translate the json string to related java object array.
        Gson gson = new Gson();
        Person userInfoDtoArray[] = gson.fromJson(popupReceiverListString, Person[].class);

        if(userInfoDtoArray != null) {
            for (int i = 0; i < userInfoDtoArray.length; i++) {
                try{
                    if(personList.get(i).equals(userInfoDtoArray[i])) {
                        personList.add(userInfoDtoArray[i]);
                    }
                } catch (Exception e){
                    personList.add(userInfoDtoArray[i]);
                }

                /*if(userInfoDtoArray.length == personList.size()) {
                    if(personList.get(i).equals(userInfoDtoArray[i])) {
                        personList.add(userInfoDtoArray[i]);
                    }
                } else {
                    try {
                        if(personList.get(i).equals(userInfoDtoArray[i])) {

                        }
                    } catch (Exception e) {
                        personList.add(userInfoDtoArray[i]);
                    }
                }*/
            }
        }
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
                newPerson = (Person) data.getExtras().getSerializable("PERSON");;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Store the data:
        editor.putString("message", String.valueOf(editTextMessage.getText()));
        editor.putString("concerning", String.valueOf(editTextConcerning.getText()));

        // Store personList:
        Gson gson = new Gson();
        String s = gson.toJson(personList);
        editor.putString("PERSONLIST", s);

        editor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        editTextMessage.setText(settings.getString("message",""));
        editTextConcerning.setText(settings.getString("concerning", ""));
    }

    protected void sendEmail() {

        String message = editTextMessage.getText().toString() + "\n\n----\nGeschrieben am: " + textViewDate.getText();
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
            // Clear for the selectedPersons:
            personsSelected.clear();
            for(int i = 0; i < personSelectedBoolean.length; i++) {
                personSelectedBoolean[i] = false;
            }

            startActivity(Intent.createChooser(emailIntent, "Verschicke E-Mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
