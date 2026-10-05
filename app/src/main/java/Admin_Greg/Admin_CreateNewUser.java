package Admin_Greg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Admin_CreateNewUser extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    RadioButton rbCustomer, rbGardener;
    EditText txtFullName, txtPassword, txtConfirmPassword, txtEmail, txtPhone;
    Button btnCreateNewAccount;

    String user_group;
    String authorised;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_create_new_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rbCustomer = findViewById(R.id.rdoAdminCreateNewCustomer);
        rbGardener = findViewById(R.id.rdoAdminCreateNewGardener);
        txtFullName = findViewById(R.id.txtAdminRegisterFullName);
        txtPassword = findViewById(R.id.txtAdminCreateNewPassword);
        txtConfirmPassword = findViewById(R.id.txtAdminConfirmNewPassword);
        txtEmail = findViewById(R.id.txtAdminCreateNewEmail);
        txtPhone = findViewById(R.id.txtAdminCreateNewPhone);
        btnCreateNewAccount = findViewById(R.id.btnAdminCreateNewAccount);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();




        // Changes selected account type to customer and unchecks gardener radio button
        rbCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbGardener.setChecked(false);
                rbCustomer.setChecked(true);
                Toast.makeText(getApplicationContext(), "test 1", Toast.LENGTH_SHORT).show();
                user_group = "Customers";
                authorised = "True";
            }
        });

        // Changes selected account type to gardener and unchecks customer radio button
        rbGardener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbGardener.setChecked(true);
                rbCustomer.setChecked(false);
                Toast.makeText(getApplicationContext(), "test 2", Toast.LENGTH_SHORT).show();
                user_group = "Gardeners";
                authorised = "True";
            }
        });



        btnCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = txtPassword.getText().toString().trim();
                String confirmPassword = txtConfirmPassword.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String fullName = txtFullName.getText().toString().trim();
                String phone = txtPhone.getText().toString().trim();

                if (!fieldsEmpty()){
                    if (password.equals(confirmPassword)) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    String uID = mAuth.getCurrentUser().getUid();
                                    Map<String, Object> newUser = new HashMap<>();
                                    newUser.put("name", fullName);
                                    newUser.put("phone", phone);
                                    newUser.put("user_group", user_group);
                                    newUser.put("authorised", authorised);
                                    db.collection("users").document(uID).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), "Success: New profile created", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unsuccessful: Cannot create new account", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Error: Passwords do not match", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Error: One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public boolean fieldsEmpty(){
        String fullName = txtFullName.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();

        if (fullName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty() || user_group.isEmpty() ){
            return true;
        }
        return false;
    }
}