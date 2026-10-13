package Gardener_Uly.OpenJobs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.AGU.gardenhands.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Gardener_Uly.Jobs;


public class Gardener_OpenJobs extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    RecyclerView rvOpenJobs;
    ProgressBar pbOpenJobs;

    List<Jobs> jobsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardener_open_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        jobsArrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        pbOpenJobs = findViewById(R.id.pbGOJLoading);
        OpenJobsCVAdapter adapter = new OpenJobsCVAdapter(jobsArrayList);


        CollectionReference colRef = db.collection("jobs");
        colRef.whereEqualTo("job_status", "verified").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Jobs j = document.toObject(Jobs.class);
                        j.setDocumentId(document.getId());
                        jobsArrayList.add(j);
                    }
                    adapter.notifyDataSetChanged();
                    pbOpenJobs.setVisibility(View.GONE);
                } else {
                    Log.d("", "Error getting documents: ", task.getException());
                }
            }
        });



        rvOpenJobs = findViewById(R.id.rvGOJOpenJobs);
        rvOpenJobs.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvOpenJobs.setAdapter(adapter);




    }
}