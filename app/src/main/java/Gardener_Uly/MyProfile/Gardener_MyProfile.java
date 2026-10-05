package Gardener_Uly.MyProfile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.AGU.gardenhands.LoginActivity;
import com.AGU.gardenhands.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;


public class Gardener_MyProfile extends AppCompatActivity {

    LinearLayout profileFieldsLayout;
    ProgressBar pbProfileLoading;
    TextView txtName, txtEmail, txtPhone;
    Button btnUpdateDetails, btnLogOut;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    DocumentSnapshot gardenerDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardener_my_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileFieldsLayout = findViewById(R.id.GardenerProfileFieldsLayout);
        pbProfileLoading = findViewById(R.id.pbGProfileLoading);
        txtName = findViewById(R.id.txtGardenerProfileName);
        txtEmail = findViewById(R.id.txtGardenerProfileEmail);
        txtPhone = findViewById(R.id.txtGardenerProfilePhone);
        btnUpdateDetails = findViewById(R.id.btnGardenerUpdateDetails);
        btnLogOut = findViewById(R.id.btnGardenerLogOut);

        pbProfileLoading.setVisibility(View.VISIBLE);
        profileFieldsLayout.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    gardenerDoc = task.getResult();
                }
                txtName.setText(gardenerDoc.get("name").toString());
                txtEmail.setText(user.getEmail());
                txtPhone.setText(gardenerDoc.get("phone").toString());

                pbProfileLoading.setVisibility(View.GONE);
                profileFieldsLayout.setVisibility(View.VISIBLE);
            }
        });

        btnUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Gardeners_UpdateProfile.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });





    }
}