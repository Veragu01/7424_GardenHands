package Customer_Aidan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.AGU.gardenhands.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Customer_Quote_Details extends AppCompatActivity {

    private TextView txtDetailGardenerName, txtDetailFee, txtDetailTimeslot, txtDetailNotes;
    private Button btnPickQuote, btnBack;

    private FirebaseFirestore db;

    private String quoteId;
    private String gardenerId;
    private String gardenerName;
    private Double fee;
    private String timeslotId;
    private String date;
    private String session;
    private String timeslot;
    private String notes;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_quote_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        txtDetailGardenerName = findViewById(R.id.txtDetailGardenerName);
        txtDetailFee = findViewById(R.id.txtDetailFee);
        txtDetailTimeslot = findViewById(R.id.txtDetailTimeslot);
        txtDetailNotes = findViewById(R.id.txtDetailNotes);
        btnPickQuote = findViewById(R.id.btnPickQuote);
        btnBack = findViewById(R.id.btnBack);

        // Retrieve data from Intent
        if (getIntent() != null) {
            Quote quote = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                quote = getIntent().getSerializableExtra("quote", Quote.class);
            } else {
                quote = (Quote) getIntent().getSerializableExtra("quote");
            }

            if (quote != null) {
                quoteId = quote.getQuoteId();
                gardenerId = quote.getGardenerId();
                gardenerName = quote.getGardenerName();
                fee = parseDoubleSafely(quote.getFee());
                timeslotId = quote.getTimeslotId();
                date = quote.getDate();
                session = quote.getSession();
                timeslot = quote.getTimeslot();
                notes = quote.getNotes();
                jobId = quote.getJobId();
            } else {
                quoteId = getIntent().getStringExtra("quote_id");
                gardenerId = getIntent().getStringExtra("gardener_id");
                gardenerName = getIntent().getStringExtra("gardener_name");
                fee = parseDoubleSafely(getIntent().getStringExtra("fee"));
                timeslotId = getIntent().getStringExtra("timeslot_id");
                date = getIntent().getStringExtra("date");
                session = getIntent().getStringExtra("session");
                timeslot = getIntent().getStringExtra("timeslot");
                notes = getIntent().getStringExtra("notes");
                jobId = getIntent().getStringExtra("job_id");
            }
        }

        displayQuoteDetails();
        fetchTimeslotDetailsIfNeeded();

        btnPickQuote.setOnClickListener(v -> pickQuote());
        btnBack.setOnClickListener(v -> finish());
    }

    private Double parseDoubleSafely(String value) {
        if (value == null) return 0.0;
        try {
            String cleaned = value.replaceAll("[^0-9.]", "");
            if (cleaned.isEmpty()) return 0.0;
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void displayQuoteDetails() {
        String displayName = gardenerName;
        if (displayName == null || displayName.isEmpty()) {
            displayName = "Gardener " + (gardenerId != null ? gardenerId : "");
        }
        txtDetailGardenerName.setText(displayName);

        if (fee != null) {
            txtDetailFee.setText(String.format(Locale.getDefault(), "$%.2f", fee));
        } else {
            txtDetailFee.setText("$0.00");
        }

        updateTimeslotDisplay();

        txtDetailNotes.setText(notes != null && !notes.trim().isEmpty() ? notes : "No additional notes provided.");
    }

    private void updateTimeslotDisplay() {
        StringBuilder sb = new StringBuilder();
        if (date != null && !date.trim().isEmpty() && !"N/A".equalsIgnoreCase(date)) {
            sb.append("Date: ").append(date);
        }
        if (session != null && !session.trim().isEmpty() && !"N/A".equalsIgnoreCase(session)) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("Session: ").append(session);
        }
        if (sb.length() == 0) {
            if (timeslot != null && !timeslot.trim().isEmpty()) {
                sb.append(timeslot);
            } else {
                sb.append("N/A");
            }
        }
        txtDetailTimeslot.setText(sb.toString());
    }

    private void fetchTimeslotDetailsIfNeeded() {
        if (date != null && !date.trim().isEmpty() && session != null && !session.trim().isEmpty()) {
            return;
        }

        String tsId = timeslotId;
        if (tsId == null || tsId.trim().isEmpty() || "N/A".equalsIgnoreCase(tsId)) {
            tsId = timeslot;
        }
        if (tsId == null || tsId.trim().isEmpty() || "N/A".equalsIgnoreCase(tsId)) {
            return;
        }

        String finalTsId = tsId;
        db.collection("timeslots").document(finalTsId).get()
                .addOnCompleteListener(task -> {
                    boolean found = false;
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        applyTimeslotDoc(task.getResult());
                        found = true;
                    }
                    if (!found) {
                        db.collection("time_slots").document(finalTsId).get()
                                .addOnCompleteListener(fallbackTask -> {
                                    if (fallbackTask.isSuccessful() && fallbackTask.getResult() != null && fallbackTask.getResult().exists()) {
                                        applyTimeslotDoc(fallbackTask.getResult());
                                    }
                                });
                    }
                });
    }

    private void applyTimeslotDoc(DocumentSnapshot tsDoc) {
        Object dateObj = tsDoc.get("date");
        if (dateObj == null) dateObj = tsDoc.get("date_time");
        if (dateObj == null) dateObj = tsDoc.get("dateTime");
        if (dateObj == null) dateObj = tsDoc.get("day");

        Object sessionObj = tsDoc.get("session");
        if (sessionObj == null) sessionObj = tsDoc.get("session_time");
        if (sessionObj == null) sessionObj = tsDoc.get("slot");
        if (sessionObj == null) sessionObj = tsDoc.get("time");

        if (dateObj != null) {
            this.date = String.valueOf(dateObj);
        }
        if (sessionObj != null) {
            this.session = String.valueOf(sessionObj);
        }
        updateTimeslotDisplay();
    }

    private void pickQuote() {
        if (quoteId == null || quoteId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid quote ID", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPickQuote.setEnabled(false);

        Map<String, Object> quoteUpdates = new HashMap<>();

        db.collection("quotes").document(quoteId)
                .update(quoteUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Update job if jobId is available
                    if (jobId != null && !jobId.isEmpty()) {
                        Map<String, Object> jobUpdates = new HashMap<>();
                        jobUpdates.put("job_status", "in_progress");
                        if (gardenerId != null) {
                            jobUpdates.put("fee", fee);
                            String timeValue = (date != null && session != null) ? (date + " (" + session + ")") : (timeslot != null ? timeslot : "");
                            jobUpdates.put("date_time", date);
                            jobUpdates.put("gardener", gardenerId);
                        }
                        if (fee != null) {
                            jobUpdates.put("fee", fee);
                        }
                        db.collection("jobs").document(jobId).update(jobUpdates);
                    }

                    Toast.makeText(Customer_Quote_Details.this, "Quote picked successfully!", Toast.LENGTH_SHORT).show();
                    btnPickQuote.setText("Quote Accepted");
                    btnPickQuote.setAlpha(0.6f);
                })
                .addOnFailureListener(e -> {
                    btnPickQuote.setEnabled(true);
                    Toast.makeText(Customer_Quote_Details.this, "Failed to pick quote: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        Intent intent = new Intent(Customer_Quote_Details.this, Customer_Dashboard.class);
        startActivity(intent);
        finish();
    }
}
