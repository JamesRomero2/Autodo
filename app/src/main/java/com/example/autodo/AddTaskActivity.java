package com.example.autodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Spinner spinnerLevel;
    private ArrayAdapter adapter;
    private TextView date;
    private EditText taskName, taskDetails;
    String taskUrgency;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private String getGoogleID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        adapter = ArrayAdapter.createFromResource(this, R.array.levelOfUrgencyEntries, R.layout.dropdown_spinner);
        adapter.setDropDownViewResource(R.layout.dropdown_spinner);


        spinnerLevel = (Spinner) findViewById(R.id.levelUrgency);
        date = (TextView) findViewById(R.id.datepicker);
        taskName = (EditText) findViewById(R.id.editTextTextPersonName);
        taskDetails = (EditText) findViewById(R.id.editTextTextPersonName2);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            getGoogleID = account.getId().toString().trim();
        }

        spinnerLevel.setAdapter(adapter);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePick = new DatePickerFragment();

                datePick.show(getSupportFragmentManager(), "Date Picker");
            }
        });
        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taskUrgency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddTaskActivity.this, "Please Select One", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        date.setText(selectedDate);
    }
    public void ClickCancelAddTask(View view) {
        MainActivity.redirectActivity(this, MainActivity.class);
    }
    public void ClickAddTask(View view) {
        uploadDataToDatabase();
    }

    private void uploadDataToDatabase() {
        Date currentTime = Calendar.getInstance().getTime();

        String googleId = getGoogleID;
        String dateTimeCreated = currentTime.toString().trim();
        String name = taskName.getText().toString().trim();
        String details = taskDetails.getText().toString().trim();
        String urgency = taskUrgency;
        String dateTimeDeadline = date.getText().toString().trim();
        boolean status = false;

        TaskData taskData = new TaskData(googleId, dateTimeCreated, name, details, urgency, dateTimeDeadline, status);

        FirebaseDatabase.getInstance().getReference(googleId).child(dateTimeCreated).setValue(taskData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddTaskActivity.this, "Task Saved", Toast.LENGTH_SHORT).show();
                    clearField();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddTaskActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearField() {
        taskName.setText("");
        taskDetails.setText("");
        date.setText("");
    }
}