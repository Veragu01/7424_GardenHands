package com.AGU.gardenhands;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextView txtCreateNewAccount;
    Button btnLogin;
    EditText txtLoginEmail, txtLoginPassword;
    FirebaseAuth mAuth;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCreateNewAccount = findViewById(R.id.txtCreateNewAccount);
        btnLogin = findViewById(R.id.btnLogin);
        txtLoginEmail = findViewById(R.id.txtLoginEmail);
        txtLoginPassword = findViewById(R.id.txtLoginPassword);
        mAuth = FirebaseAuth.getInstance();

        txtCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = txtLoginEmail.getText().toString();
                password = txtLoginPassword.getText().toString();
                if (!email.isEmpty() || !password.isEmpty()) { // if either are blank display error, otherwise attempt login
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                                db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        String TAG = "";
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                                if (document.get("user_group").equals("Gardeners")){ // opens gardeners dash on login success
                                                    Toast.makeText(getApplicationContext(), "Opening Gardeners Dash", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                                                    startActivity(intent);
                                                } else if (document.get("user_group").equals("Customers")) { // opens customers dash on login success
                                                    Toast.makeText(getApplicationContext(), "Opening Customer Dash", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                                                    startActivity(intent);
                                                } else if (document.get("user_group").equals("Administrators")) { // opens administrators dash on login success
                                                    Toast.makeText(getApplicationContext(), "Opening Administrators Dash", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                                                    startActivity(intent);
                                                }

                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(getApplicationContext(), "Error: Email or Password incorrect", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error: Email or Password blank", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}