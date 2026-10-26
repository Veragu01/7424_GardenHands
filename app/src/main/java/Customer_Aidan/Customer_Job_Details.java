package Customer_Aidan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Customer_Job_Details extends AppCompatActivity {

    private static final String[] JOB_TYPES = {"Gardening", "Mowing", "Planting", "Cleaning", "Other"};

    private LinearLayout layoutJobDateTime, layoutJobFee;
    private EditText txtJobDateTime, txtJobFee, txtJobTitle, txtJobDescription, txtJobLocation, txtJobSize;
    private Spinner spinnerJobType;
    private Button btnUpdateJob, btnViewQuotes, btnDeleteJob, btnCancelJob;

    private FirebaseFirestore db;
    private String jobId;
    private String jobStatus;
    private String jobFee;
    private Job jobObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_job_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        layoutJobDateTime = findViewById(R.id.layoutJobDateTime);
        layoutJobFee = findViewById(R.id.layoutJobFee);
        txtJobDateTime = findViewById(R.id.txtJobDateTime);
        txtJobFee = findViewById(R.id.txtJobFee);
        txtJobTitle = findViewById(R.id.txtJobTitle);
        txtJobDescription = findViewById(R.id.txtJobDescription);
        spinnerJobType = findViewById(R.id.spinnerJobType);
        txtJobLocation = findViewById(R.id.txtJobLocation);
        txtJobSize = findViewById(R.id.txtJobSize);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, JOB_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobType.setAdapter(adapter);

        btnUpdateJob = findViewById(R.id.btnUpdateJob);
        btnViewQuotes = findViewById(R.id.btnViewQuotes);
        btnDeleteJob = findViewById(R.id.btnDeleteJob);
        btnCancelJob = findViewById(R.id.btnCancelJob);

        extractIntentDataAndPopulate();

        btnUpdateJob.setOnClickListener(v -> updateJobInFirestore());
        btnViewQuotes.setOnClickListener(v -> {
            if (isWorkCompleted()) {
                markJobAsComplete();
            } else {
                openQuotesActivity();
            }
        });
        btnDeleteJob.setOnClickListener(v -> deleteJobFromFirestore());
        btnCancelJob.setOnClickListener(v -> finish());
    }

    private void extractIntentDataAndPopulate() {
        if (getIntent() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                jobObject = getIntent().getSerializableExtra("job", Job.class);
            } else {
                jobObject = (Job) getIntent().getSerializableExtra("job");
            }

            if (jobObject != null) {
                jobId = jobObject.getJobId();
                jobStatus = jobObject.getJobStatus();
                populateFieldsFromJob(jobObject);
            } else {
                jobId = getIntent().getStringExtra("job_id");
                if (jobId == null || jobId.isEmpty()) {
                    jobId = getIntent().getStringExtra("jobId");
                }
                jobStatus = getIntent().getStringExtra("job_status");
                jobFee = getIntent().getStringExtra("job_fee");
                String title = getIntent().getStringExtra("job_title");
                String description = getIntent().getStringExtra("job_description");
                String type = getIntent().getStringExtra("job_type");
                String location = getIntent().getStringExtra("job_location");
                String size = getIntent().getStringExtra("job_size");
                String dateTime = getIntent().getStringExtra("job_date_time");
                String beforePic = getIntent().getStringExtra("before_pic");

                if (title != null) txtJobTitle.setText(title);
                if (description != null) txtJobDescription.setText(description);
                if (type != null) setJobTypeSelection(type);
                if (location != null) txtJobLocation.setText(location);
                if (size != null) txtJobSize.setText(size);
                if (dateTime != null) txtJobDateTime.setText(dateTime);
                if (jobFee != null && !jobFee.isEmpty()) {
                    String f = jobFee.startsWith("$") ? jobFee : "$" + jobFee;
                    txtJobFee.setText(f);
                }
            }
        }

        updateUIState();

        if (jobId != null && !jobId.isEmpty()) {
            if (txtJobTitle.getText().toString().isEmpty() || jobStatus == null) {
                fetchJobFromFirestore(jobId);
            } else if (isQuotePicked()) {
                fetchQuoteDetailsForJob(jobId);
            }
        }
    }

    private boolean isVerified() {
        return jobStatus != null && jobStatus.trim().equalsIgnoreCase("verified");
    }

    private boolean isWorkCompleted() {
        return jobStatus != null && jobStatus.trim().equalsIgnoreCase("work_completed");
    }

    private boolean isFinishedOrClosed() {
        if (jobStatus == null) return false;
        String status = jobStatus.trim();
        return status.equalsIgnoreCase("finished")
                || status.equalsIgnoreCase("closed");
    }

    private boolean isQuotePicked() {
        if (jobStatus == null || jobStatus.trim().isEmpty()) {
            return false;
        }
        String s = jobStatus.trim();
        if (s.equalsIgnoreCase("pending") || s.equalsIgnoreCase("verified")) {
            return false;
        }
        return true;
    }

    private void updateUIState() {
        boolean finishedOrClosed = isFinishedOrClosed();
        boolean quotePicked = isQuotePicked();

        if (layoutJobDateTime != null) {
            layoutJobDateTime.setVisibility(quotePicked ? View.VISIBLE : View.GONE);
        }
        if (layoutJobFee != null) {
            layoutJobFee.setVisibility(quotePicked ? View.VISIBLE : View.GONE);
        }

        if (btnViewQuotes != null) {
            if (isVerified()) {
                btnViewQuotes.setVisibility(View.VISIBLE);
                btnViewQuotes.setText("View Quotes");
            } else if (isWorkCompleted()) {
                btnViewQuotes.setVisibility(View.VISIBLE);
                btnViewQuotes.setText("Confirm Job Completed");
            } else {
                btnViewQuotes.setVisibility(View.GONE);
            }
        }

        if (finishedOrClosed) {
            if (btnCancelJob != null) btnCancelJob.setVisibility(View.GONE);
            if (btnDeleteJob != null) btnDeleteJob.setVisibility(View.GONE);
            if (btnUpdateJob != null) btnUpdateJob.setVisibility(View.GONE);
        }

        boolean enabled = !finishedOrClosed;
        setEditTextReadOnly(txtJobDateTime, enabled);
        setEditTextReadOnly(txtJobFee, false); // Fee is display only once quote is picked
        setEditTextReadOnly(txtJobTitle, enabled);
        setEditTextReadOnly(txtJobDescription, enabled);
        spinnerJobType.setEnabled(enabled);
        setEditTextReadOnly(txtJobLocation, enabled);
        setEditTextReadOnly(txtJobSize, enabled);
    }

    private void setEditTextReadOnly(EditText editText, boolean enabled) {
        if (editText != null) {
            editText.setEnabled(enabled);
            editText.setFocusable(enabled);
            editText.setFocusableInTouchMode(enabled);
            editText.setClickable(enabled);
        }
    }

    private void setJobTypeSelection(String type) {
        if (type == null || spinnerJobType == null) return;
        String trimmed = type.trim();
        for (int i = 0; i < JOB_TYPES.length; i++) {
            if (JOB_TYPES[i].equalsIgnoreCase(trimmed)) {
                spinnerJobType.setSelection(i);
                return;
            }
        }
        spinnerJobType.setSelection(4);
    }

    private void populateFieldsFromJob(Job job) {
        if (job == null) return;

        if (job.getJobStatus() != null) {
            jobStatus = job.getJobStatus();
        }
        boolean quotePicked = isQuotePicked();
        if (job.getFee() != null && quotePicked) {
            jobFee = job.getFee();
            String f = jobFee.startsWith("$") ? jobFee : "$" + jobFee;
            txtJobFee.setText(f);
        } else {
            jobFee = null;
            txtJobFee.setText("");
        }
        if (job.getJobTitle() != null) txtJobTitle.setText(job.getJobTitle());
        if (job.getJobDescription() != null) txtJobDescription.setText(job.getJobDescription());
        if (job.getJobType() != null) setJobTypeSelection(job.getJobType());
        if (job.getLocation() != null) txtJobLocation.setText(job.getLocation());
        if (job.getSize() != null) txtJobSize.setText(job.getSize());
        if (job.getDateTime() != null && quotePicked) {
            txtJobDateTime.setText(job.getDateTime());
        } else {
            txtJobDateTime.setText("");
        }

        updateUIState();
    }

    // fetch all jobs from the firestore database
    private void fetchJobFromFirestore(String id) {
        db.collection("jobs").document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            jobObject = Job.fromDocument(doc);
                            populateFieldsFromJob(jobObject);
                            if (isQuotePicked()) {
                                fetchQuoteDetailsForJob(id);
                            }
                        }
                    }
                });
    }

    private void fetchQuoteDetailsForJob(String targetJobId) {
        if (targetJobId == null || targetJobId.trim().isEmpty()) return;
        if (!isQuotePicked()) return;

        db.collection("quotes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Quote quote = Quote.fromDocument(doc);
                            if (isQuoteForJob(targetJobId, quote, doc)) {
                                String qStatus = quote.getStatus();
                                boolean isAccepted = qStatus != null && qStatus.trim().equalsIgnoreCase("accepted");
                                if (isAccepted) {
                                    if (quote.getFee() != null && !quote.getFee().isEmpty()) {
                                        jobFee = quote.getFee();
                                        String f = jobFee.startsWith("$") ? jobFee : "$" + jobFee;
                                        txtJobFee.setText(f);
                                    }
                                    fetchTimeslotAndSetDateTime(quote);
                                    updateUIState();
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void fetchTimeslotAndSetDateTime(Quote quote) {
        if (quote == null) return;

        if (quote.getDate() != null && !quote.getDate().trim().isEmpty()) {
            txtJobDateTime.setText(quote.getFormattedTimeslot());
            return;
        }

        String tsId = quote.getTimeslotId();
        if (tsId == null || tsId.trim().isEmpty() || "N/A".equalsIgnoreCase(tsId)) {
            tsId = quote.getTimeslot();
        }

        if (tsId != null && !tsId.trim().isEmpty() && !"N/A".equalsIgnoreCase(tsId)) {
            String finalTsId = tsId;
            db.collection("timeslots").document(finalTsId).get()
                    .addOnCompleteListener(task -> {
                        boolean found = false;
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            applyTimeslotDocToQuote(quote, task.getResult());
                            found = true;
                        }
                        if (!found) {
                            db.collection("time_slots").document(finalTsId).get()
                                    .addOnCompleteListener(fallbackTask -> {
                                        if (fallbackTask.isSuccessful() && fallbackTask.getResult() != null && fallbackTask.getResult().exists()) {
                                            applyTimeslotDocToQuote(quote, fallbackTask.getResult());
                                        } else {
                                            txtJobDateTime.setText(quote.getFormattedTimeslot());
                                        }
                                    });
                        }
                    });
        } else {
            txtJobDateTime.setText(quote.getFormattedTimeslot());
        }
    }

    private void applyTimeslotDocToQuote(Quote quote, DocumentSnapshot tsDoc) {
        Object dateObj = tsDoc.get("date");
        Object sessionObj = tsDoc.get("session");

        if (dateObj != null) {
            quote.setDate(String.valueOf(dateObj));
        }
        if (sessionObj != null) {
            quote.setSession(String.valueOf(sessionObj));
        }
        txtJobDateTime.setText(quote.getFormattedTimeslot());
    }

    // Check if a job belongs to the current customer
    private boolean isQuoteForJob(String targetJobId, Quote quote, DocumentSnapshot doc) {
        if (targetJobId == null || targetJobId.trim().isEmpty()) return false;

        if (quote.getJobId() != null && isJobIdMatch(targetJobId, quote.getJobId())) {
            return true;
        }

        if (doc != null && doc.getId() != null) {
            String docId = doc.getId();
            if (isJobIdMatch(targetJobId, docId) || docId.startsWith(targetJobId.trim() + "_") || docId.startsWith(targetJobId.trim() + "-")) {
                return true;
            }
        }

        return false;
    }

    private boolean isJobIdMatch(String target, String actual) {
        if (target == null || actual == null) return false;
        String t = target.trim();
        String a = actual.trim();
        if (t.equalsIgnoreCase(a)) return true;

        if (t.contains("/")) {
            t = t.substring(t.lastIndexOf('/') + 1);
        }
        if (a.contains("/")) {
            a = a.substring(a.lastIndexOf('/') + 1);
        }
        return t.equalsIgnoreCase(a);
    }

    // Mark the job as complete once the work has been completed
    private void markJobAsComplete() {
        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid Job ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("jobs").document(jobId)
                .update("job_status", "finished")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Customer_Job_Details.this, "Job marked as complete!", Toast.LENGTH_SHORT).show();
                    jobStatus = "finished";
                    if (jobObject != null) {
                        jobObject.setJobStatus("finished");
                    }
                    updateUIState();
                })
                .addOnFailureListener(e -> Toast.makeText(Customer_Job_Details.this, "Error completing job: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Update the job details in the firestore database
    private void updateJobInFirestore() {
        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid Job ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = txtJobTitle.getText().toString().trim();
        String description = txtJobDescription.getText().toString().trim();
        String type = spinnerJobType.getSelectedItem() != null ? spinnerJobType.getSelectedItem().toString() : "";
        String location = txtJobLocation.getText().toString().trim();
        String size = txtJobSize.getText().toString().trim();
        String dateTime = txtJobDateTime.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || type.isEmpty() || location.isEmpty() || size.isEmpty() || dateTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("job_title", title);
        updates.put("job_description", description);
        updates.put("job_type", type);
        updates.put("location", location);
        updates.put("size", size);
        updates.put("date_time", dateTime);

        db.collection("jobs").document(jobId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(Customer_Job_Details.this, "Job updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Customer_Job_Details.this, "Error updating job: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteJobFromFirestore() {
        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid Job ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("jobs").document(jobId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Customer_Job_Details.this, "Job deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(Customer_Job_Details.this, "Error deleting job: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openQuotesActivity() {
        Intent intent = new Intent(Customer_Job_Details.this, Customer_View_Quotes.class);
        if (jobId != null) {
            intent.putExtra("job_id", jobId);
            intent.putExtra("jobId", jobId);
        }
        if (jobObject != null) {
            intent.putExtra("job", jobObject);
        }
        startActivity(intent);
    }
}
