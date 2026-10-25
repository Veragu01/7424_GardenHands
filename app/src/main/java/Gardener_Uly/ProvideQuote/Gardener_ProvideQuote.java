package Gardener_Uly.ProvideQuote;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.AGU.gardenhands.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Gardener_Uly.Gardener_Dashboard;

public class Gardener_ProvideQuote extends AppCompatActivity {

    DatePicker dpCalendarSpinner;
    EditText etxtFee, etxtNotes;
    Button btnSendQuote, btnCancelQuote;
    int dayOfMonth, monthIndex, year;
    String date, jobID, session="AM", timeslotID="", timeslotAvailability="";
    MaterialCardView crdFeeCard, crdDateCard;
    LinearLayout mainLayout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    DocumentSnapshot jobDocument, timeslotDocument;
    CollectionReference colRef, quoteCollection;
    RadioButton rbAM, rbPM;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardener_provide_quote);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dpCalendarSpinner = findViewById(R.id.dpGPQ_CalendarSpinner);
        etxtFee = findViewById(R.id.txtGPQ_Fee);
        etxtNotes = findViewById(R.id.txtGPQ_Notes);
        btnSendQuote = findViewById(R.id.btnGPQ_SendQuote);
        btnCancelQuote = findViewById(R.id.btnGPQCancel);
        crdDateCard = findViewById(R.id.cardGPQ_DateCard);
        crdFeeCard = findViewById(R.id.cardGPQ_FeeCard);
        Calendar today = Calendar.getInstance();
        rbAM = findViewById(R.id.rbGPQ_AM);
        rbPM = findViewById(R.id.rbGPQ_PM);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        jobID = intent.getStringExtra("docID");

        dpCalendarSpinner.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() { // changes date string to new selection
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        setDate();
                    }
                }
        );
        setDate();

        rbAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPM.setChecked(false);
                rbAM.setChecked(true);
                session = "AM";
            }
        });

        rbPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPM.setChecked(true);
                rbAM.setChecked(false);
                session = "PM";
            }
        });


        DocumentReference jobDocRef = db.collection("jobs").document(jobID);
        jobDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    jobDocument = task.getResult();

                }
            }
        });

        btnSendQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Quote in database

                if (!etxtFee.getText().toString().trim().isEmpty() && (rbAM.isChecked() || rbPM.isChecked())) {
                    colRef = db.collection("timeslots");
                    colRef.whereEqualTo("date", date).whereEqualTo("session", session).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean timeslotexists = false;
                                for (QueryDocumentSnapshot document : task.getResult()) { // timeslot exists
                                    timeslotexists = true;
                                    timeslotID = document.getId();
                                    timeslotAvailability = document.get("availability").toString();
                                    getTimeslot();
                                }
                                if (!timeslotexists)  {
                                    //create new timeslot
                                    Map<String, Object> newTimeslot = new HashMap<>();
                                    newTimeslot.put("date", date);
                                    newTimeslot.put("session", session);
                                    newTimeslot.put("availability", jobDocument.get("job_title") );
                                    newTimeslot.put("gardener", user.getUid());
                                    db.collection("timeslots").document().set(newTimeslot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Success: Timeslot Created", Toast.LENGTH_LONG).show();
                                                getTimeslot();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });







                }

            }
        });

        btnCancelQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mainLayout = findViewById(R.id.main);
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mainLayout.getLayoutParams();
//        params.setMargins(0, 0, 0, 0);
//        mainLayout.setLayoutParams(params);

        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mainLayout.getWindowVisibleDisplayFrame(r);
                int heightDiff = mainLayout.getRootView().getHeight() - (r.bottom - r.top);

                // Check if the keyboard is visible
                if (heightDiff > 200) { // 200 can be adjusted based on your needs
                    // Keyboard is open
                    if (etxtNotes.hasFocus()){
                        crdDateCard.setVisibility(View.GONE);
                        crdFeeCard.setVisibility(View.GONE);
//                        params.setMargins(0, -450, 0, 0);
//                        mainLayout.setLayoutParams(params);
                    }
                } else {
                    // Keyboard is closed
                    crdDateCard.setVisibility(View.VISIBLE);
                    crdFeeCard.setVisibility(View.VISIBLE);
//                    params.setMargins(0, 0, 0, 0);
//                    mainLayout.setLayoutParams(params);

                }
            }
        });



    }

    public void setDate() {
        dayOfMonth = dpCalendarSpinner.getDayOfMonth();
        monthIndex = dpCalendarSpinner.getMonth();
        year = dpCalendarSpinner.getYear();
        date = dayOfMonth+"/"+(monthIndex+1)+"/"+year;

    }

    public void getTimeslot() {
        colRef.whereEqualTo("date", date).whereEqualTo("session", session).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() ) {
                    timeslotDocument = task.getResult().getDocuments().get(0);
                    timeslotID = timeslotDocument.getId();
                    timeslotAvailability = timeslotDocument.getString("availability");
                    if (!timeslotAvailability.equals("Unavailable")) {
                        checkQuotes();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: Timeslot Unavailable", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    public void checkQuotes() {
        db.collection("quotes").whereEqualTo("gardenerID", user.getUid()).whereEqualTo("jobID", jobDocument.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Boolean quoteExists = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {// timeslot exists
                        quoteExists = true;
                        Toast.makeText(getApplicationContext(), "Error: Quote already sent", Toast.LENGTH_SHORT).show();
                    }
                    if (!quoteExists) {
                        createQuote();
                    }
                }
            }
        });

    }
    public void createQuote() {
        Map<String, Object> newQuote = new HashMap<>();
        newQuote.put("customerID", jobDocument.get("customer"));
        newQuote.put("fee", etxtFee.getText().toString());
        newQuote.put("gardenerID", user.getUid());
        newQuote.put("notes", etxtNotes.getText().toString());
        newQuote.put("timeslot", timeslotDocument.getId());
        newQuote.put("jobID", jobDocument.getId());
        db.collection("quotes").document().set(newQuote).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Success: Quote Sent", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), Gardener_Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    finish();

                }
            }
        });
    }
}