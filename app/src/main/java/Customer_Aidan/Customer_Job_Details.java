package Customer_Aidan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Customer_Job_Details extends AppCompatActivity {

    private TextView txtFileName;
    private EditText txtJobDateTime, txtJobTitle, txtJobDescription, txtJobType, txtJobLocation, txtJobSize;
    private Button btnUpdateJob, btnViewQuotes, btnDeleteJob, btnCancelJob;

    private FirebaseFirestore db;
    private String jobId;
    private String jobStatus;
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

        txtFileName = findViewById(R.id.txtFileName);
        txtJobDateTime = findViewById(R.id.txtJobDateTime);
        txtJobTitle = findViewById(R.id.txtJobTitle);
        txtJobDescription = findViewById(R.id.txtJobDescription);
        txtJobType = findViewById(R.id.txtJobType);
        txtJobLocation = findViewById(R.id.txtJobLocation);
        txtJobSize = findViewById(R.id.txtJobSize);

        btnUpdateJob = findViewById(R.id.btnUpdateJob);
        btnViewQuotes = findViewById(R.id.btnViewQuotes);
        btnDeleteJob = findViewById(R.id.btnDeleteJob);
        btnCancelJob = findViewById(R.id.btnCancelJob);

        txtJobDateTime.setOnClickListener(v -> showDateTimePicker(txtJobDateTime));

        extractIntentDataAndPopulate();

        btnUpdateJob.setOnClickListener(v -> updateJobInFirestore());
        btnViewQuotes.setOnClickListener(v -> {
            if (isInProgress()) {
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
                jobStatus = getIntent().getStringExtra("job_status");
                String title = getIntent().getStringExtra("job_title");
                String description = getIntent().getStringExtra("job_description");
                String type = getIntent().getStringExtra("job_type");
                String location = getIntent().getStringExtra("job_location");
                String size = getIntent().getStringExtra("job_size");
                String dateTime = getIntent().getStringExtra("job_date_time");
                String beforePic = getIntent().getStringExtra("before_pic");

                if (title != null) txtJobTitle.setText(title);
                if (description != null) txtJobDescription.setText(description);
                if (type != null) txtJobType.setText(type);
                if (location != null) txtJobLocation.setText(location);
                if (size != null) txtJobSize.setText(size);
                if (dateTime != null) txtJobDateTime.setText(dateTime);
                if (beforePic != null && !beforePic.isEmpty()) txtFileName.setText(beforePic);
            }
        }

        updateViewQuotesButton();

        // If jobId is present but fields or status are missing/empty, load directly from Firestore
        if (jobId != null && !jobId.isEmpty() && (txtJobTitle.getText().toString().isEmpty() || jobStatus == null)) {
            fetchJobFromFirestore(jobId);
        }
    }

    private boolean isInProgress() {
        return jobStatus != null && (jobStatus.equalsIgnoreCase("in_progress") || jobStatus.equalsIgnoreCase("in progress"));
    }

    private void updateViewQuotesButton() {
        if (btnViewQuotes != null) {
            if (isInProgress()) {
                btnViewQuotes.setText("confirm job complete");
            } else {
                btnViewQuotes.setText("View Quotes");
            }
        }
    }

    private void populateFieldsFromJob(Job job) {
        if (job == null) return;

        if (job.getJobStatus() != null) {
            jobStatus = job.getJobStatus();
        }
        if (job.getJobTitle() != null) txtJobTitle.setText(job.getJobTitle());
        if (job.getJobDescription() != null) txtJobDescription.setText(job.getJobDescription());
        if (job.getJobType() != null) txtJobType.setText(job.getJobType());
        if (job.getLocation() != null) txtJobLocation.setText(job.getLocation());
        if (job.getSize() != null) txtJobSize.setText(job.getSize());
        if (job.getDateTime() != null) txtJobDateTime.setText(job.getDateTime());
        if (job.getBeforePic() != null && !job.getBeforePic().isEmpty()) {
            txtFileName.setText(job.getBeforePic());
        }

        updateViewQuotesButton();
    }

    private void fetchJobFromFirestore(String id) {
        db.collection("jobs").document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            jobObject = Job.fromDocument(doc);
                            populateFieldsFromJob(jobObject);
                        }
                    }
                });
    }

    private void markJobAsComplete() {
        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid Job ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("jobs").document(jobId)
                .update("job_status", "complete")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Customer_Job_Details.this, "Job marked as complete!", Toast.LENGTH_SHORT).show();
                    jobStatus = "complete";
                    if (jobObject != null) {
                        jobObject.setJobStatus("complete");
                    }
                    updateViewQuotesButton();
                })
                .addOnFailureListener(e -> Toast.makeText(Customer_Job_Details.this, "Error completing job: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateJobInFirestore() {
        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid Job ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = txtJobTitle.getText().toString().trim();
        String description = txtJobDescription.getText().toString().trim();
        String type = txtJobType.getText().toString().trim();
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
        }
        startActivity(intent);
    }

    private void showDateTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();

        String currentText = editText.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        if (!currentText.isEmpty()) {
            try {
                Date date = sdf.parse(currentText);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (Exception ignored) {
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                editText.setText(sdf.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
