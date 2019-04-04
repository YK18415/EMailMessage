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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static Context context;

    EditText editTextReceiver;
    EditText editTextConcerning;
    EditText editTextMessage;
    TextView textViewDate;

    List<Person> personList = new ArrayList<>();
    ArrayList personsSelected = new ArrayList();
    List<String> emailadressesList = new ArrayList<>();
    boolean[] personSelectedBoolean;

    //Storage:
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        // Storage:
        settings = context.getSharedPreferences("emailmessagedetails", MODE_PRIVATE); // For reading.

        // Layout components:
        Button btnShowPeople = findViewById(R.id.btn_show_people);
        editTextConcerning = findViewById(R.id.editTextConcerning);
        editTextReceiver = findViewById(R.id.editTextReceiver);
        textViewDate = findViewById(R.id.textViewDate);
        setEditTextDate(textViewDate);
        editTextMessage = findViewById(R.id.editTextMessage);

        // Load data, which are stored in the preferences:
        loadPersonList();
        // PersonSelected:
        String personSelectedStorageString = settings.getString("personsSelected", "");
        // Create Gson object again.
        Gson gsonPersonSelected = new Gson();
        final int personSelectedStorageArray[] = gsonPersonSelected.fromJson(personSelectedStorageString, int[].class);

        loadEmailaddressesList();
        handlePersonSelected(personSelectedStorageArray);

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

    private void loadPersonList(){
        String popupReceiverListString = settings.getString("PERSONLIST", "");
        // Create Gson object and translate the json string to related java object array.
        Gson gson = new Gson();
        Person[] popupReceiverArray = gson.fromJson(popupReceiverListString, Person[].class);

        // Add standard-persons (It is a kind of a seeder):
        if(personList.size() == 0) {
            personList.add(new Person("Kira", "Schatzi", "Student", "kira.begau@gmx.de"));
            personList.add(new Person("Kirsten", "Büggener", "VW", "k.bueggener@gmx.de"));
        }

        if(popupReceiverArray != null) {
            for (int i = 0; i < popupReceiverArray.length; i++) {
                try{
                    if(personList.get(i).equals(popupReceiverArray[i])) {
                        personList.add(popupReceiverArray[i]);
                    }
                } catch (Exception e){
                    personList.add(popupReceiverArray[i]);
                }
            }
        }
    }

    private void loadEmailaddressesList() {
        String emailaddressesString = settings.getString("emailaddresses", "");
        Gson gson = new Gson();
        String[] emailaddressesArray = gson.fromJson(emailaddressesString, String[].class);

        if(emailaddressesArray != null) {
            for(int i = 0; i < emailaddressesArray.length; i++) {
                emailadressesList.add(emailaddressesArray[i]);
            }
        }
    }

    private void setEditTextDate(TextView textViewDate) {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String folderName = formatter.format(today);
        textViewDate.setText(folderName);
    }

    private void handlePersonSelected(int personSelectedStorageArray[]) {
        if(personSelectedStorageArray != null) {
            for (int personSelectedStorageItem: personSelectedStorageArray) {
                if(!personsSelected.contains(personSelectedStorageItem)) {
                    personsSelected.add(personSelectedStorageItem);
                }
            }
        }

        handlePersonSelectedBoolean(personList);
    }

    private void chooseReceivers() {
        // The method setMultiChoiceItmes wants to have an Array. So, the receivers-objects have to be converted, so that only the names are shown in the popup-menu.
        final String[] personsStringList = new String[personList.size()];

        for (int i = 0; i < personList.size(); i++) {
            personsStringList[i] = personList.get(i).getVorname() + " " + personList.get(i).getNachname();
        }

        openPopupDialog(personsSelected, personsStringList, personSelectedBoolean);
    }

    private void openPopupDialog(final ArrayList personsSelected, final String[] personsStringList, boolean[] personSelectedBoolean) {
        final ArrayList toAddToPersonSelected = new ArrayList();
        final ArrayList toRemoveFromPersonSelected = new ArrayList();
        final ArrayList<String> toRemoveFromEmailadressesList = new ArrayList();

        Dialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Empfängerwahl");
        builder.setMultiChoiceItems( personsStringList, personSelectedBoolean, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                if (isSelected) {
                    toAddToPersonSelected.add(selectedItemId);
                    //personsSelected.add(selectedItemId);
                } else if (personsSelected.contains(selectedItemId)) {
                    toRemoveFromPersonSelected.add(selectedItemId);
                    toRemoveFromEmailadressesList.add(personList.get(selectedItemId).getEmailadress());
                    //personsSelected.remove(Integer.valueOf(selectedItemId));
                }
            }
        })
                // Three buttons:
        .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // add to personSelected:
                for (Object personSelectedItem : toAddToPersonSelected) {
                    personsSelected.add(personSelectedItem);
                }
                // Remove from personSelected:
                for (Object personSelectedItem : toRemoveFromPersonSelected) {
                    personsSelected.remove(Integer.valueOf((Integer) personSelectedItem));
                }

                for(String emailaddress : toRemoveFromEmailadressesList) {
                    emailadressesList.remove(emailaddress);
                }
                for (Object personSelectedItem : personsSelected) {
                    if(!emailadressesList.contains(personList.get((Integer) personSelectedItem).getEmailadress())) {
                        emailadressesList.add(personList.get((Integer) personSelectedItem).getEmailadress());
                    }
                }

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
                Person newPerson = (Person) Objects.requireNonNull(data.getExtras()).getSerializable("PERSON");

                // added the newPerson, who was created by the user in the popup-menu:
                if(newPerson != null && !personList.contains(newPerson)) {
                    personList.add(newPerson);
                }

                handlePersonSelectedBoolean(personList);
            }
        }
    }

    private void handlePersonSelectedBoolean(List<Person> personList) {
        personSelectedBoolean = new boolean[personList.size()];
        try{
            for (int i = 0; i < personsSelected.size(); i++) {     // Über personSelected iterieren.
                for(int j = 0; j < personSelectedBoolean.length; j++) {   // Über personSelectedBoolean iterieren.
                    if(j == (int)personsSelected.get(i)) {
                        personSelectedBoolean[j] = true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace().toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit(); // For writing.

        // Store the data:
        editor.putString("message", String.valueOf(editTextMessage.getText()));
        editor.putString("concerning", String.valueOf(editTextConcerning.getText()));

        Gson gson = new Gson();

        storeComplexData(gson,"PERSONLIST", personList, editor);    // Store personList:
        storeComplexData(gson, "personsSelected", personsSelected, editor);  // Store persons, who are selected in the popup-menu
        storeComplexData(gson, "emailaddresses", emailadressesList, editor);    // Store the emailaddressList
        
        editor.commit();
    }

    private void storeComplexData(Gson gson, String key, List data, SharedPreferences.Editor editor) {
        String dataString = gson.toJson(data);
        editor.putString(key, dataString);
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

        // Hangs the e-mail-addresses together:
        String emailAddressesString = combineEMailAddresses();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(emailAddressesString)); // The e-mail addresses that should be delivered to.
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, betreff); // A constant string holding the (desired subject line == Betreffzeile) of a message.
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            clearSelectedPersons();
            emailadressesList.clear();
            startActivity(Intent.createChooser(emailIntent, "Öffne E-Mail-Client..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,"Es sind keine E-Mail-Clients installiert.", Toast.LENGTH_SHORT).show();
        }
    }

    private String combineEMailAddresses() {
        StringBuilder sb = new StringBuilder("mailto:");
        sb.append(emailadressesList.get(0)); // The first address.
        for(int i = 1; i < emailadressesList.size(); i++) {
            sb.append(",");
            sb.append(emailadressesList.get(i));
        }

        return sb.toString();
    }

    private void clearSelectedPersons() {
        personsSelected.clear();
        for(int i = 0; i < personSelectedBoolean.length; i++) {
            personSelectedBoolean[i] = false;
        }
    }
}
