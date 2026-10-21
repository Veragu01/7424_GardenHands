package Customer_Aidan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Customer_Create_Job extends AppCompatActivity {

    private TextView txtFileName;
    private EditText txtJobDateTime, txtJobFee, txtJobTitle, txtJobDescription, txtJobType, txtJobLocation, txtJobSize;
    private Button btnPickFile, btnCreateJob, btnCcancelNewJob;

    private Uri selectedFileUri = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_create_job);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtFileName = findViewById(R.id.txtFileName);
        txtJobDateTime = findViewById(R.id.txtJobDateTime);
        txtJobFee = findViewById(R.id.txtJobFee);
        txtJobTitle = findViewById(R.id.txtJobTitle);
        txtJobDescription = findViewById(R.id.txtJobDescription);
        txtJobType = findViewById(R.id.txtJobType);
        txtJobLocation = findViewById(R.id.txtJobLocation);
        txtJobSize = findViewById(R.id.txtJobSize);

        btnPickFile = findViewById(R.id.btnPickFile);
        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnCcancelNewJob = findViewById(R.id.btnCCancelNewJob);

        btnPickFile.setOnClickListener(v -> filePickerLauncher.launch("image/*"));

        txtJobDateTime.setOnClickListener(v -> showDateTimePicker(txtJobDateTime));

        btnCreateJob.setOnClickListener(v -> createNewJob());

        btnCcancelNewJob.setOnClickListener(v -> {
            Intent intent = new Intent(Customer_Create_Job.this, Customer_Dashboard.class);
            startActivity(intent);
            finish();
        });
    }

    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    txtFileName.setText(getFileName(uri));
                }
            }
    );

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

    private String getFileName(Uri uri) {
        if (uri == null) return "";
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
            if (result != null) {
                int lastSlash = result.lastIndexOf('/');
                if (lastSlash != -1) {
                    result = result.substring(lastSlash + 1);
                }
            } else {
                result = uri.toString();
            }
        }
        return result;
    }

    private void createNewJob() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String customerId = currentUser.getUid();
        String dateTime = txtJobDateTime.getText().toString().trim();
        String fee = txtJobFee.getText().toString().trim();
        String title = txtJobTitle.getText().toString().trim();
        String description = txtJobDescription.getText().toString().trim();
        String type = txtJobType.getText().toString().trim();
        String location = txtJobLocation.getText().toString().trim();
        String size = txtJobSize.getText().toString().trim();
        String beforePic = selectedFileUri != null ? getFileName(selectedFileUri) : "";

        if (dateTime.isEmpty() || fee.isEmpty() || title.isEmpty() || description.isEmpty() || type.isEmpty() || location.isEmpty() || size.isEmpty()) {
            Toast.makeText(this, "Please fill in all job details", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> newJob = new LinkedHashMap<>();
        newJob.put("before_pic", beforePic);
        newJob.put("customer", customerId);
        newJob.put("date_time", dateTime);
        newJob.put("fee", fee);
        newJob.put("gardener", "");
        newJob.put("job_description", description);
        newJob.put("job_status", "Open");
        newJob.put("job_title", title);
        newJob.put("job_type", type);
        newJob.put("location", location);
        newJob.put("size", size);

        db.collection("jobs")
                .add(newJob)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Customer_Create_Job.this, "Success: Job created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Customer_Create_Job.this, Customer_Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Customer_Create_Job.this, "Error: Failed to create job", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}