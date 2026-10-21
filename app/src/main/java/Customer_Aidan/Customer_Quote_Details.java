package Customer_Aidan;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Customer_Quote_Details extends AppCompatActivity {

    private TextView txtDetailGardenerName, txtDetailFee, txtDetailTimeslot, txtDetailStatus, txtDetailNotes;
    private Button btnPickQuote, btnBack;

    private FirebaseFirestore db;

    private String quoteId;
    private String gardenerId;
    private String gardenerName;
    private String fee;
    private String timeslot;
    private String notes;
    private String status;
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
        txtDetailStatus = findViewById(R.id.txtDetailStatus);
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
                fee = quote.getFee();
                timeslot = quote.getTimeslot();
                notes = quote.getNotes();
                status = quote.getStatus();
                jobId = quote.getJobId();
            } else {
                quoteId = getIntent().getStringExtra("quote_id");
                gardenerId = getIntent().getStringExtra("gardener_id");
                gardenerName = getIntent().getStringExtra("gardener_name");
                fee = getIntent().getStringExtra("fee");
                timeslot = getIntent().getStringExtra("timeslot");
                notes = getIntent().getStringExtra("notes");
                status = getIntent().getStringExtra("status");
                jobId = getIntent().getStringExtra("job_id");
            }
        }

        displayQuoteDetails();

        btnPickQuote.setOnClickListener(v -> pickQuote());
        btnBack.setOnClickListener(v -> finish());
    }

    private void displayQuoteDetails() {
        String displayName = gardenerName;
        if (displayName == null || displayName.isEmpty()) {
            displayName = "Gardener " + (gardenerId != null ? gardenerId : "");
        }
        txtDetailGardenerName.setText(displayName);

        String feeStr = fee;
        if (feeStr != null && !feeStr.startsWith("$")) {
            feeStr = "$" + feeStr;
        }
        txtDetailFee.setText(feeStr != null ? feeStr : "$0.00");

        txtDetailTimeslot.setText(timeslot != null ? timeslot : "N/A");

        String displayStatus = status != null ? status : "Pending";
        txtDetailStatus.setText(displayStatus);

        txtDetailNotes.setText(notes != null && !notes.trim().isEmpty() ? notes : "No additional notes provided.");

        if ("Accepted".equalsIgnoreCase(displayStatus) || "Picked".equalsIgnoreCase(displayStatus)) {
            btnPickQuote.setText("Quote Accepted");
            btnPickQuote.setEnabled(false);
            btnPickQuote.setAlpha(0.6f);
        }
    }

    private void pickQuote() {
        if (quoteId == null || quoteId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid quote ID", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPickQuote.setEnabled(false);

        Map<String, Object> quoteUpdates = new HashMap<>();
        quoteUpdates.put("status", "Accepted");
        quoteUpdates.put("quote_status", "Accepted");

        db.collection("quotes").document(quoteId)
                .update(quoteUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Update job if jobId is available
                    if (jobId != null && !jobId.isEmpty()) {
                        Map<String, Object> jobUpdates = new HashMap<>();
                        jobUpdates.put("job_status", "Assigned");
                        if (gardenerId != null) {
                            jobUpdates.put("gardener", gardenerId);
                        }
                        if (fee != null) {
                            jobUpdates.put("fee", fee);
                        }
                        db.collection("jobs").document(jobId).update(jobUpdates);
                    }

                    Toast.makeText(Customer_Quote_Details.this, "Quote picked successfully!", Toast.LENGTH_SHORT).show();
                    txtDetailStatus.setText("Accepted");
                    btnPickQuote.setText("Quote Accepted");
                    btnPickQuote.setAlpha(0.6f);
                })
                .addOnFailureListener(e -> {
                    btnPickQuote.setEnabled(true);
                    Toast.makeText(Customer_Quote_Details.this, "Failed to pick quote: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
