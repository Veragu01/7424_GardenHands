package Gardener_Uly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Gardener_Uly.OpenJobs.Gardener_OpenJobs;

public class Gardener_Dashboard extends AppCompatActivity {

//    FirebaseAuth mAuth;
//    FirebaseUser user;
//    FirebaseFirestore db;
//    TextView tvGardenerName, tvGardenerPhone;

//    DocumentSnapshot gardenerDoc;

Button btnViewJobs, btnMyJobs, btnMyWeek, btnMyProfile, btnSetAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardener_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnViewJobs = findViewById(R.id.btnGDashViewJobs);
        btnMyJobs = findViewById(R.id.btnGDashMyJobs);
        btnMyWeek = findViewById(R.id.btnGDashMyWeek);
        btnMyProfile = findViewById(R.id.btnGDashMyProfile);
        btnSetAvailability = findViewById(R.id.btnGDashSetAvailability);


        btnViewJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Opening View Jobs", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Gardener_OpenJobs.class);
                startActivity(intent);
            }
        });

        btnMyJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Opening My Jobs", Toast.LENGTH_SHORT).show();

            }
        });

        btnMyWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Opening My Weekly Calendar", Toast.LENGTH_SHORT).show();


            }
        });

        btnMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Opening My Profile", Toast.LENGTH_SHORT).show();


            }
        });

        btnSetAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Opening Set Availability", Toast.LENGTH_SHORT).show();
            }
        });




//        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//        tvGardenerName = findViewById(R.id.tvGDashGardenersName);
//        tvGardenerPhone = findViewById(R.id.tvGDashGardenersPhone);


//        db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("users").document(user.getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    gardenerDoc = task.getResult();
//                }

//                tvGardenerName.setText(gardenerDoc.get("name").toString());
//                tvGardenerPhone.setText(gardenerDoc.get("phone").toString());
//            }
//        });



    }
}