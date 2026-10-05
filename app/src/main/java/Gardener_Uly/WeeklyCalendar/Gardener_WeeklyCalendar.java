package Gardener_Uly.WeeklyCalendar;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.AGU.gardenhands.R;

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


        months.add("Jan");  months.add("Feb");    months.add("March");
        months.add("April");    months.add("May");  months.add("Jun");
        months.add("Jul");  months.add("Aug");  months.add("Sept");
        months.add("Oct");  months.add("Nov");   months.add("Dec");

        Calendar calendar = Calendar.getInstance();
        int monthIndex = calendar.get(Calendar.MONTH); // 0 based
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 1 based
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 based
        int firstdayOfWeek;


        if (dayOfWeek==2) { // Monday
            txtCurrentWeek.setText("Week of: " + dayOfMonth + " " + months.get(monthIndex));
            txtMondayAM.setText("AM: ");

        } else if (dayOfWeek==3) { // Tuesday
            firstdayOfWeek=dayOfMonth-1;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }
            txtCurrentWeek.setText("Week of: " + firstdayOfWeek + " " + months.get(monthIndex));

        } else if (dayOfWeek==4) { // Wednesday
            firstdayOfWeek=dayOfMonth-2;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }
            txtCurrentWeek.setText("Week of: " + firstdayOfWeek + " " + months.get(monthIndex));

        } else if (dayOfWeek==5) { // Thursday
            firstdayOfWeek=dayOfMonth-3;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }
            txtCurrentWeek.setText("Week of: " + firstdayOfWeek + " " + months.get(monthIndex));


        } else if (dayOfWeek==6) { // Friday
            firstdayOfWeek=dayOfMonth-4;
            if (firstdayOfWeek<=0) {
                monthIndex--;
                firstdayOfWeek= 31+firstdayOfWeek;
            }
            txtCurrentWeek.setText("Week of: " + firstdayOfWeek + " " + months.get(monthIndex));

        }

    }
}