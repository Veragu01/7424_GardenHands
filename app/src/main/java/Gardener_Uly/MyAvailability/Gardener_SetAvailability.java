package Gardener_Uly.MyAvailability;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Gardener_Uly.Jobs;


public class Gardener_SetAvailability extends AppCompatActivity {

    DatePicker datePicker;

    Spinner spnrTime, spnrBooking;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    Button btnUpdateAvailability;

    String date, timeslot_session, availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardener_set_availability);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        date = "";

        CollectionReference colRef = db.collection("timeslots");
        updateSpinners(colRef);

        datePicker = findViewById(R.id.GSA_DatePicker);
        spnrTime = findViewById(R.id.spnrGSA_Timeslot);
        spnrBooking = findViewById(R.id.spnrGSA_Booking);
        btnUpdateAvailability = findViewById(R.id.btnGSA_UpdateTimeslot);

        Calendar today = Calendar.getInstance();

        String[] AMPM = {"Please Select", "AM", "PM"};
        String[] lstAvailability = {"Please Select", "Available", "Unavailable"};

        ArrayAdapter<String> adTime = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, AMPM);
        adTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrTime.setAdapter(adTime);

        ArrayAdapter<String> adAvailability = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, lstAvailability);
        adAvailability.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrBooking.setAdapter((adAvailability));

        datePicker.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() { // changes date string to new selection
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        // Display selected date in Toast message
                        date = day + "/" + (month + 1) + "/" + year;
//                        Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
                        if (spnrTime.getSelectedItemPosition()!=0) { // if time spinner is n/a don't update booking spinner
                            updateSpinners(colRef);
                        }
                    }
                }
        );
        // gives starting date string after calendar init
        date = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth()+1) + "/" + datePicker.getYear();
        spnrTime.setSelection(0);
        spnrBooking.setSelection(0);
        timeslot_session=AMPM[0];
        availability=lstAvailability[0];


        spnrTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeslot_session=AMPM[position];
                updateSpinners(colRef);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnrBooking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                availability=lstAvailability[position];
//                Toast.makeText(getApplicationContext(), spnrBooking.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        btnUpdateAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!timeslot_session.equals("Please Select") && !availability.equals("Please Select")) {
//                    Toast.makeText(getApplicationContext(), (date + ", " + timeslot_session + ", " + availability), Toast.LENGTH_SHORT).show();
                    colRef.whereEqualTo("date", date).whereEqualTo("session", timeslot_session).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean timeslotexists = false;
                                for (QueryDocumentSnapshot document : task.getResult()) { // timeslot exists
                                    timeslotexists = true;

                                    DocumentReference docRef = db.collection("timeslots").document(document.getId());
                                    docRef.update("availability", spnrBooking.getSelectedItem().toString());
                                    Toast.makeText(getApplicationContext(), "Success: Timeslot Updated", Toast.LENGTH_LONG).show();

                                }
                                if (!timeslotexists) { // timeslot does not exist
                                    //create new timeslot
                                    Map<String, Object> newTimeslot = new HashMap<>();
                                    newTimeslot.put("date", date);
                                    newTimeslot.put("session", timeslot_session);
                                    newTimeslot.put("availability", spnrBooking.getSelectedItem().toString() );
                                    newTimeslot.put("gardener", user.getUid());
                                    db.collection("timeslots").document().set(newTimeslot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Success: Timeslot Created", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error: Please select a Timeslot and Availability", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void updateSpinners(CollectionReference colRef) {

        colRef.whereEqualTo("date", date).whereEqualTo("session", timeslot_session).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    boolean hasTimeslot = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        hasTimeslot = true;
                        Log.d("", document.getId() + " => " + document.getData());
                        if (document.get("availability").equals("Available")) {
                            spnrBooking.setSelection(1);
                        } else {
                            spnrBooking.setSelection(2);
                        }
                    }
                    if (hasTimeslot==false) {
                        spnrBooking.setSelection(0);
                    }
                }
            }
        });

    }



}


// date is set to today with both spinners set to N/A    - done
// date is selected -> date variable is changed (if time spinner != N/A, update booked spinner)    -done
// time spinner changed -> timeslot variable changed & booked spinner updated to unavailable, or available if in database
// update button checks if either spinner = n/a first, then creates or updates timeslot in database