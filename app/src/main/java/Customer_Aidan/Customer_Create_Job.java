package Customer_Aidan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    private EditText txtJobTitle, txtJobDescription, txtJobLocation, txtJobSize;
    private Spinner spinnerJobType;
    private Button btnCreateJob, btnCcancelNewJob;

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

        txtJobTitle = findViewById(R.id.txtJobTitle);
        txtJobDescription = findViewById(R.id.txtJobDescription);
        spinnerJobType = findViewById(R.id.spinnerJobType);
        txtJobLocation = findViewById(R.id.txtJobLocation);
        txtJobSize = findViewById(R.id.txtJobSize);

        String[] jobTypes = {"Gardening", "Mowing", "Planting", "Cleaning", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobType.setAdapter(adapter);

        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnCcancelNewJob = findViewById(R.id.btnCCancelNewJob);

        btnCreateJob.setOnClickListener(v -> createNewJob());

        btnCcancelNewJob.setOnClickListener(v -> {
            Intent intent = new Intent(Customer_Create_Job.this, Customer_Dashboard.class);
            startActivity(intent);
            finish();
        });
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
        String title = txtJobTitle.getText().toString().trim();
        String description = txtJobDescription.getText().toString().trim();
        String type = spinnerJobType.getSelectedItem() != null ? spinnerJobType.getSelectedItem().toString() : "";
        String location = txtJobLocation.getText().toString().trim();
        String size = txtJobSize.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || type.isEmpty() || location.isEmpty() || size.isEmpty()) {
            Toast.makeText(this, "Please fill in all job details", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> newJob = new LinkedHashMap<>();
        newJob.put("customer", customerId);
        newJob.put("date_time", null);
        newJob.put("fee", null);
        newJob.put("gardener", "");
        newJob.put("job_description", description);
        newJob.put("job_status", "pending");
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