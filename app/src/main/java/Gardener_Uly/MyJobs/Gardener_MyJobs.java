package Gardener_Uly.MyJobs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Gardener_Uly.Jobs;

public class Gardener_MyJobs extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    RecyclerView rvMyJobs;
    ProgressBar pbMyJobs;

    List<Jobs> jobsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardener_my_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        jobsArrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        pbMyJobs = findViewById(R.id.pbGMJLoading);
        Gardener_MyJobsCVAdapter adapter = new Gardener_MyJobsCVAdapter(jobsArrayList);


        CollectionReference colRef = db.collection("jobs");
        colRef.whereEqualTo("job_status", "in_progress").whereEqualTo("gardener", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("", "test");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Jobs j = document.toObject(Jobs.class);
                        j.setDocumentId(document.getId());
                        jobsArrayList.add(j);
                    }
                    adapter.notifyDataSetChanged();
                    pbMyJobs.setVisibility(View.GONE);
                    rvMyJobs.setVisibility(View.VISIBLE);
                } else {
                    Log.d("", "Error getting documents: ", task.getException());
                }
            }
        });


        rvMyJobs = findViewById(R.id.rvGMJMyJobs);
        rvMyJobs.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvMyJobs.setAdapter(adapter);

    }
}