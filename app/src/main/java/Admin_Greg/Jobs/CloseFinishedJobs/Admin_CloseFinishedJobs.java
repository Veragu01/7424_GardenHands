package Admin_Greg.Jobs.CloseFinishedJobs;



import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Admin_Greg.Jobs.JobDetails.Admin_Job;
import Admin_Greg.Jobs.JobDetails.Admin_JobAdapter;

public class Admin_CloseFinishedJobs extends AppCompatActivity {

    FirebaseFirestore db;

    RecyclerView recyclerClose;

    ArrayList<Admin_Job> jobList;

    Admin_JobAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_close_finished_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerClose = findViewById(R.id.recyclerClose);
        recyclerClose.setLayoutManager(new LinearLayoutManager(this));

        jobList = new ArrayList<>();

        adapter = new Admin_JobAdapter(jobList, "close");

        recyclerClose.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        jobList.clear();
        adapter.notifyDataSetChanged();

        db.collection("jobs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            jobList.clear();
                            for (int i = 0; i < task.getResult().size(); i++) {
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(i);
                                Admin_Job job = document.toObject(Admin_Job.class);
                                job.setDocumentId(document.getId());
                                String status = job.getJob_status();
                                if ("finished".equalsIgnoreCase(status)) {
                                    jobList.add(job);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
