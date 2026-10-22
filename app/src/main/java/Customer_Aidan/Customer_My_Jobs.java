package Customer_Aidan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Customer_My_Jobs extends AppCompatActivity implements JobAdapter.OnJobClickListener {

    private RecyclerView rvCustomerJobs;
    private ProgressBar pbLoadingJobs;
    private TextView tvEmptyJobs;
    private Button btnBackDashboard;

    private JobAdapter jobAdapter;
    private List<Job> jobList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_my_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rvCustomerJobs = findViewById(R.id.rvCustomerJobs);
        pbLoadingJobs = findViewById(R.id.pbLoadingJobs);
        tvEmptyJobs = findViewById(R.id.tvEmptyJobs);
        btnBackDashboard = findViewById(R.id.btnBackDashboard);

        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList, this);

        rvCustomerJobs.setLayoutManager(new LinearLayoutManager(this));
        rvCustomerJobs.setAdapter(jobAdapter);

        btnBackDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(Customer_My_Jobs.this, Customer_Dashboard.class);
            startActivity(intent);
            finish();
        });

        loadCustomerJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomerJobs();
    }

    private void loadCustomerJobs() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            pbLoadingJobs.setVisibility(View.GONE);
            tvEmptyJobs.setText("Please log in to view your jobs.");
            tvEmptyJobs.setVisibility(View.VISIBLE);
            return;
        }

        String currentUserId = currentUser.getUid();
        pbLoadingJobs.setVisibility(View.VISIBLE);
        tvEmptyJobs.setVisibility(View.GONE);

        db.collection("jobs")
                .whereEqualTo("customer", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    pbLoadingJobs.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        jobList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Job job = Job.fromDocument(document);
                            String status = job.getJobStatus();
                            boolean isFinishedOrClosed = false;
                            if (status != null) {
                                String s = status.trim();
                                if ("finished".equalsIgnoreCase(s) || "closed".equalsIgnoreCase(s)) {
                                    isFinishedOrClosed = true;
                                }
                            }
                            if (!isFinishedOrClosed) {
                                jobList.add(job);
                            }
                        }

                        if (jobList.isEmpty()) {
                            tvEmptyJobs.setText("No active jobs found.");
                            tvEmptyJobs.setVisibility(View.VISIBLE);
                        } else {
                            tvEmptyJobs.setVisibility(View.GONE);
                        }

                        jobAdapter.notifyDataSetChanged();
                    } else {
                        tvEmptyJobs.setText("Failed to load jobs.");
                        tvEmptyJobs.setVisibility(View.VISIBLE);
                        Toast.makeText(Customer_My_Jobs.this, "Error fetching jobs", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onJobClick(Job job) {
        Intent intent = new Intent(Customer_My_Jobs.this, Customer_Job_Details.class);
        intent.putExtra("job", job);
        intent.putExtra("job_id", job.getJobId());
        intent.putExtra("job_title", job.getJobTitle());
        intent.putExtra("job_description", job.getJobDescription());
        intent.putExtra("job_type", job.getJobType());
        intent.putExtra("job_location", job.getLocation());
        intent.putExtra("job_size", job.getSize());
        intent.putExtra("job_date_time", job.getDateTime());
        intent.putExtra("before_pic", job.getBeforePic());
        intent.putExtra("job_fee", job.getFee());
        intent.putExtra("job_status", job.getJobStatus());
        intent.putExtra("gardener", job.getGardener());
        startActivity(intent);
    }
}
