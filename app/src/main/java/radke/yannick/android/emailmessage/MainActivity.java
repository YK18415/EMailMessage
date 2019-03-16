package radke.yannick.android.emailmessage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText editText_receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Layout components:
        Button btnShowPeople = findViewById(R.id.btn_show_people);
        editText_receiver = findViewById(R.id.editTextReceiver);
        EditText editTextDate = findViewById(R.id.editTextDate);


        // (1) get today's date
        Date today = Calendar.getInstance().getTime();

        // (2) create a date "formatter" (the date format we want)
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        // (3) create a new String using the date format we want
        String folderName = formatter.format(today);

        // (4) this prints "Folder Name = 2009-09-06-08.23.23"
        editTextDate.setText(folderName);



        // Person-choose:
        btnShowPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePersons();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Die E-Mail wurde verschickt.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void choosePersons() {

        // The method setMultiChoiceItmes wants to have an Array. So, the persons-objects have to be converted, so that only the names are shown in the popup-menu.
        final Person[] persons = {new Person("Kira", "Schatzi", "Student"), new Person("Kirsten", "Büggener", "VW")};
        final String[] personsStringList = new String[persons.length];
        for (int i = 0; i < persons.length; i++) {
            personsStringList[i] = persons[i].getVorname() + " " + persons[i].getNachname();
        }

        final ArrayList personsSelected = new ArrayList();

        Dialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Personenwahl");
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
                        // Add persons the the 'Betreff':
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < personsSelected.size(); i++) {
                            sb.append(personsStringList[(int) personsSelected.get(i)]);
                            if(i+1 < personsSelected.size()) {
                                sb.append("; ");
                            }
                        }

                        editText_receiver.setText(sb.toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "Abgebrochen", Toast.LENGTH_SHORT).show();


                    }
                });
        dialog = builder.create();
        dialog.show();
    }
}
