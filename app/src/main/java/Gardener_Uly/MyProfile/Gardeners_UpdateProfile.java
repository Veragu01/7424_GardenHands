package Gardener_Uly.MyProfile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Gardeners_UpdateProfile extends AppCompatActivity {

    LinearLayout profileFieldsLayout;
    ProgressBar pbProfileLoading;
    EditText txtName, txtEmail, txtPhone, txtPassword, txtConfirmPassword;
    Button btnUpdateDetails, btnCancel;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    DocumentSnapshot gardenerDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardeners_update_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        profileFieldsLayout = findViewById(R.id.GardenerUpdateProfileFieldsLayout);
        pbProfileLoading = findViewById(R.id.pbGUpdateProfileLoading);
        txtName = findViewById(R.id.txtGUPName);
        txtEmail = findViewById(R.id.txtGUPEmail);
        txtPhone = findViewById(R.id.txtGUPPhone);
        txtPassword = findViewById(R.id.txtGUP_Password);
        txtConfirmPassword = findViewById(R.id.txtGUP_ConfirmPassword);
        btnUpdateDetails = findViewById(R.id.btnGUP_UpdateDetails);
        btnCancel = findViewById(R.id.btnGUPCancel);

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
                // fills fields with data and flips loading visibility
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
                if (CheckFields()) { // if any name, email or phone are empty or passwords dont match, returns false
                    if (!txtPassword.getText().toString().isEmpty()) { //determines whether to update the password (no if empty)
                        if (txtPassword.getText().toString().trim().length()<8) { // checks password length
                            Toast.makeText(getApplicationContext(), "Error: Password too short!", Toast.LENGTH_LONG).show();
                        } else { // updates password
                            user.updatePassword(txtPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Updates non password fields
                                        docRef.update("name", txtName.getText().toString().trim());
                                        docRef.update("email", txtEmail.getText().toString().trim());
                                        docRef.update("phone", txtPhone.getText().toString().trim());
                                        Toast.makeText(getApplicationContext(), "Success: Profile Updated", Toast.LENGTH_LONG).show();

                                        // returns to profile page
                                        finish();                                    }
                                }
                            });
                        }
                    } else {
                        // Updates non password fields
                        docRef.update("name", txtName.getText().toString().trim());
                        docRef.update("email", txtEmail.getText().toString().trim());
                        docRef.update("phone", txtPhone.getText().toString().trim());
                        Toast.makeText(getApplicationContext(), "Success: Profile Updated", Toast.LENGTH_LONG).show();

                        // returns to profile page
                        finish();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public Boolean CheckFields() {
        if (txtName.getText().toString().isEmpty() || txtEmail.getText().toString().isEmpty() || txtPhone.getText().toString().isEmpty() ) {
            Toast.makeText(getApplicationContext(), "Error: One or more fields are empty!", Toast.LENGTH_LONG).show();
            return false;
        } else if ( !txtPassword.getText().toString().equals( txtConfirmPassword.getText().toString() ) ) {
            Toast.makeText(getApplicationContext(), "Error: Passwords do not match!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}