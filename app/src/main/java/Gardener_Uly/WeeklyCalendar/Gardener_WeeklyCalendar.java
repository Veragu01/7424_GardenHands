package Gardener_Uly.WeeklyCalendar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.AGU.gardenhands.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Gardener_WeeklyCalendar extends AppCompatActivity {


    TextView txtCurrentWeek, txtMondayTitle, txtTuesdayTitle, txtWednesdayTitle, txtThursdayTitle, txtFridayTitle;
    TextView txtMondayAM, txtMondayPM, txtTuesdayAM, txtTuesdayPM, txtWednesdayAM, txtWednesdayPM, txtThursdayAM, txtThursdayPM, txtFridayAM, txtFridayPM;

    Date date = new Date();

    ArrayList<String> months = new ArrayList<String>();
    String todaysDate;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    ImageButton btnPreviousWeek, btnNextWeek;
    int monthIndex, dayOfMonth, dayOfWeek, firstdayOfWeek, year;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardener_weekly_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCurrentWeek = findViewById(R.id.txtGWP_CurrentWeek);
        txtMondayTitle = findViewById(R.id.txtGWP_MondayTitle);
        txtTuesdayTitle = findViewById(R.id.txtGWP_TuesdayTitle);
        txtWednesdayTitle = findViewById(R.id.txtGWP_WednesdayTitle);
        txtThursdayTitle = findViewById(R.id.txtGWP_ThursdayTitle);
        txtFridayTitle = findViewById(R.id.txtGWP_FridayTitle);

        txtMondayAM = findViewById(R.id.txtGWP_MondayAM);
        txtMondayPM = findViewById(R.id.txtGWP_MondayPM);
        txtTuesdayAM = findViewById(R.id.txtGWP_TuesdayAM);
        txtTuesdayPM = findViewById(R.id.txtGWP_TuesdayPM);
        txtWednesdayAM = findViewById(R.id.txtGWP_WednesdayAM);
        txtWednesdayPM = findViewById(R.id.txtGWP_WednesdayPM);
        txtThursdayAM = findViewById(R.id.txtGWP_ThursdayAM);
        txtThursdayPM = findViewById(R.id.txtGWP_ThursdayPM);
        txtFridayAM = findViewById(R.id.txtGWP_FridayAM);
        txtFridayPM = findViewById(R.id.txtGWP_FridayPM);

        btnPreviousWeek = findViewById(R.id.btnGWP_PreviousWeek);
        btnNextWeek = findViewById(R.id.btnGWP_NextWeek);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        months.add("Jan");  months.add("Feb");    months.add("March");
        months.add("April");    months.add("May");  months.add("Jun");
        months.add("Jul");  months.add("Aug");  months.add("Sept");
        months.add("Oct");  months.add("Nov");   months.add("Dec");

        calendar = Calendar.getInstance();
        monthIndex = calendar.get(Calendar.MONTH); // 0 based
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 1 based
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 based
        year = calendar.get(Calendar.YEAR);
        todaysDate = dayOfMonth+"/"+(monthIndex+1)+"/"+year;

        updateWeek();



        btnPreviousWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.WEEK_OF_YEAR, -1); // Move to last week
                monthIndex = calendar.get(Calendar.MONTH); // 0 based
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 1 based
                dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 based
                year = calendar.get(Calendar.YEAR);

                updateWeek();
            }
        });

        btnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.WEEK_OF_YEAR, +1); // Move to next week
                monthIndex = calendar.get(Calendar.MONTH); // 0 based
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 1 based
                dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 based
                year = calendar.get(Calendar.YEAR);

                updateWeek();
            }
        });

        txtCurrentWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance(); // Reset to this week
                monthIndex = calendar.get(Calendar.MONTH); // 0 based
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 1 based
                dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 based
                year = calendar.get(Calendar.YEAR);
                updateWeek();
            }
        });

    }

    public void updateCalendar(int firstDayOfWeek, int monthIndex, int year) {
        txtCurrentWeek.setText("Week of: " + firstDayOfWeek + " " + months.get(monthIndex));


        CollectionReference colRef = db.collection("timeslots");
        // Monday
        colRef.whereEqualTo("date", (firstDayOfWeek+"/"+(monthIndex+1)+"/"+year)).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean hasDoc = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("session").equals("AM")) {
                            txtMondayAM.setText("AM: " + document.get("availability"));
                        } else if (document.get("session").equals("PM")) {
                            txtMondayPM.setText("PM: " + document.get("availability"));
                        }
                        hasDoc = true;
                    }
                    if (!hasDoc) {
                        txtMondayAM.setText("AM: ");
                        txtMondayPM.setText("PM: ");

                    }
                }
            }
        });

        // Tuesday
        colRef.whereEqualTo("date", ((firstDayOfWeek+1)+"/"+(monthIndex+1)+"/"+year)).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean hasDoc = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("session").equals("AM")) {
                            txtTuesdayAM.setText("AM: " + document.get("availability"));
                        } else if (document.get("session").equals("PM")) {
                            txtTuesdayPM.setText("PM: " + document.get("availability"));
                        }
                        hasDoc = true;
                    }
                    if (!hasDoc) {
                        txtTuesdayAM.setText("AM: ");
                        txtTuesdayPM.setText("PM: ");

                    }                }
            }
        });

        // Wednesday
        colRef.whereEqualTo("date", ((firstDayOfWeek+2)+"/"+(monthIndex+1)+"/"+year)).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean hasDoc = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("session").equals("AM")) {
                            txtWednesdayAM.setText("AM: " + document.get("availability"));
                        } else if (document.get("session").equals("PM")) {
                            txtWednesdayPM.setText("PM: " + document.get("availability"));
                        }
                        hasDoc = true;
                    }
                    if (!hasDoc) {
                        txtWednesdayAM.setText("AM: ");
                        txtWednesdayPM.setText("PM: ");

                    }                }
            }
        });

        // Thursday
        colRef.whereEqualTo("date", ((firstDayOfWeek+3)+"/"+(monthIndex+1)+"/"+year)).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean hasDoc = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("session").equals("AM")) {
                            txtThursdayAM.setText("AM: " + document.get("availability"));
                        } else if (document.get("session").equals("PM")) {
                            txtThursdayPM.setText("PM: " + document.get("availability"));
                        }
                        hasDoc = true;
                    }
                    if (!hasDoc) {
                        txtThursdayAM.setText("AM: ");
                        txtThursdayPM.setText("PM: ");

                    }                }
            }
        });

        // Friday
        colRef.whereEqualTo("date", ((firstDayOfWeek+4)+"/"+(monthIndex+1)+"/"+year)).whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean hasDoc = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("session").equals("AM")) {
                            txtFridayAM.setText("AM: " + document.get("availability"));
                        } else if (document.get("session").equals("PM")) {
                            txtFridayPM.setText("PM: " + document.get("availability"));
                        }
                        hasDoc = true;
                    }
                    if (!hasDoc) {
                        txtFridayAM.setText("AM: ");
                        txtFridayPM.setText("PM: ");

                    }
                }
            }
        });

    }

    public void updateWeek() {
        if (dayOfWeek==2) { // Monday
            firstdayOfWeek=dayOfMonth;
            updateCalendar(firstdayOfWeek, monthIndex, year);

        } else if (dayOfWeek==3) { // Tuesday
            firstdayOfWeek=dayOfMonth-1;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }
            updateCalendar(firstdayOfWeek, monthIndex, year);

        } else if (dayOfWeek==4) { // Wednesday
            firstdayOfWeek=dayOfMonth-2;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }
            updateCalendar(firstdayOfWeek, monthIndex, year);

        } else if (dayOfWeek==5) { // Thursday
            firstdayOfWeek=dayOfMonth-3;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }
            updateCalendar(firstdayOfWeek, monthIndex, year);


        } else if (dayOfWeek==6) { // Friday
            firstdayOfWeek=dayOfMonth-4;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }

            updateCalendar(firstdayOfWeek, monthIndex, year);

        } else if (dayOfWeek==7) { // Saturday
            firstdayOfWeek=dayOfMonth-5;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }

            updateCalendar(firstdayOfWeek, monthIndex, year);

        } else if (dayOfWeek==1) { // Sunday
            firstdayOfWeek=dayOfMonth+1;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }

            updateCalendar(firstdayOfWeek, monthIndex, year);
        }
    }

}