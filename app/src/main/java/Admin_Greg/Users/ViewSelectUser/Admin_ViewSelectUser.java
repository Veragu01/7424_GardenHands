package Admin_Greg.Users.ViewSelectUser;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Admin_ViewSelectUser extends AppCompatActivity {

    EditText edit_usercard_name, edit_usercard_usergroup, edit_usercard_email, edit_usercard_phone, edit_usercard_authorised;
    Button btn_usercard_edit, btn_usercard_save, btn_usercard_deactivate, btn_usercard_deactivate_confirm, btn_usercard_deactivate_cancel, btn_usercard_activate;
    FirebaseFirestore db;

    String userID;

    String originalName, originalUsergroup, originalEmail, originalPhone, originalAuthorised;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_view_select_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edit_usercard_name = findViewById(R.id.edit_usercard_name);
        edit_usercard_usergroup = findViewById(R.id.edit_usercard_usergroup);
        edit_usercard_email = findViewById(R.id.edit_usercard_email);
        edit_usercard_phone = findViewById(R.id.edit_usercard_phone);
        edit_usercard_authorised = findViewById(R.id.edit_usercard_authorised);
        btn_usercard_edit = findViewById(R.id.btn_usercard_edit);
        btn_usercard_save = findViewById(R.id.btn_usercard_save);
        btn_usercard_deactivate = findViewById(R.id.btn_usercard_deactivate);
        btn_usercard_deactivate_confirm = findViewById(R.id.btn_usercard_deactivate_confirm);
        btn_usercard_deactivate_cancel = findViewById(R.id.btn_usercard_deactivate_cancel);
        btn_usercard_activate = findViewById(R.id.btn_usercard_activate);

        db = FirebaseFirestore.getInstance();
        userID = getIntent().getStringExtra("userId");

        originalName = getIntent().getStringExtra("name");
        edit_usercard_name.setText(originalName);
        originalUsergroup = getIntent().getStringExtra("user_group");
        edit_usercard_usergroup.setText(originalUsergroup);
        originalEmail = getIntent().getStringExtra("email");
        edit_usercard_email.setText(originalEmail);
        originalPhone = getIntent().getStringExtra("phone");
        edit_usercard_phone.setText(originalPhone);
        originalAuthorised = getIntent().getStringExtra("authorised");
        edit_usercard_authorised.setText(originalAuthorised);

        setUnabled();
        btn_usercard_save.setVisibility(View.GONE);

        btn_usercard_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_usercard_authorised.setEnabled(true);
                edit_usercard_authorised.setText("true");
                edit_usercard_authorised.setEnabled(false);
                Toast.makeText(Admin_ViewSelectUser.this, "User account activated successfully.", Toast.LENGTH_SHORT).show();
                btn_usercard_activate.setVisibility(View.GONE);
            }
        });
        btn_usercard_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_usercard_edit.getText().equals("Edit Details")) {
                    btn_usercard_edit.setText("Cancel");
                    btn_usercard_save.setVisibility(View.VISIBLE);
                    setEnabled();
                }else if(btn_usercard_edit.getText().equals("Cancel")){
                    edit_usercard_name.setText(originalName);
                    edit_usercard_usergroup.setText(originalUsergroup);
                    edit_usercard_email.setText(originalEmail);
                    edit_usercard_phone.setText(originalPhone);
                    edit_usercard_authorised.setText(originalAuthorised);

                    btn_usercard_edit.setText("Edit Details");
                    btn_usercard_save.setVisibility(View.GONE);
                    setUnabled();
                }
            }
        });
        btn_usercard_deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_usercard_deactivate_cancel.setVisibility(View.VISIBLE);
                btn_usercard_deactivate_confirm.setVisibility(View.VISIBLE);
                btn_usercard_deactivate.setVisibility((View.GONE));
            }
        });
        btn_usercard_deactivate_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_usercard_deactivate_confirm.setVisibility(View.GONE);
                btn_usercard_deactivate_cancel.setVisibility(View.GONE);
                btn_usercard_deactivate.setVisibility(View.VISIBLE);
                btn_usercard_activate.setVisibility(View.GONE);
                btn_usercard_deactivate.setVisibility(View.GONE);
            }
        });
        btn_usercard_deactivate_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userID == null || userID.isEmpty()) {
                    Toast.makeText(Admin_ViewSelectUser.this, "User ID doesn't exist", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("jobs").get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean activeJobFound = false;
                            for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                                String customer = document.getString("customer");
                                String gardener = document.getString("gardener");
                                String jobStatus = document.getString("job_status");
                                if (customer != null) {
                                    customer = customer.trim();
                                }
                                if (gardener != null) {
                                    gardener = gardener.trim();
                                }
                                if (jobStatus != null) {
                                    jobStatus = jobStatus.trim().toLowerCase();
                                }
                                if ((userID.equals(customer) || userID.equals(gardener)) && ("in_progress".equals(jobStatus) || "in progress".equals(jobStatus) || "work_completed".equals(jobStatus) || "work completed".equals(jobStatus) || "finished".equals(jobStatus) || "closed".equals(jobStatus))) {
                                    activeJobFound = true;
                                    break;
                                }
                            }
                            if (activeJobFound) {
                                Toast.makeText(Admin_ViewSelectUser.this, "Unable to deactivate user with active or finished jobs.", Toast.LENGTH_LONG).show();
                                btn_usercard_activate.setVisibility(View.GONE);
                                btn_usercard_deactivate.setVisibility(View.GONE);
                            } else {
                                edit_usercard_authorised.setEnabled(true);
                                edit_usercard_authorised.setText("false");
                                edit_usercard_authorised.setEnabled(false);
                                btn_usercard_deactivate_cancel.setVisibility(View.GONE);
                                btn_usercard_deactivate_confirm.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        });

        btn_usercard_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_usercard_name.getText().toString().trim().equals("") || edit_usercard_usergroup.getText().toString().trim().equals("") || edit_usercard_email.getText().toString().trim().equals("") || edit_usercard_phone.getText().toString().trim().equals("") || edit_usercard_authorised.getText().toString().trim().equals(""))
                {
                    Toast.makeText(Admin_ViewSelectUser.this, "Please fill in all details correctly.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String authorised = edit_usercard_authorised.getText().toString().trim();
                if(!authorised.equalsIgnoreCase("true") && !authorised.equalsIgnoreCase("false"))
                {
                    Toast.makeText(Admin_ViewSelectUser.this, "Authorisation must be 'true' or 'false'", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!edit_usercard_phone.getText().toString().trim().matches("\\d+"))
                {
                    Toast.makeText(Admin_ViewSelectUser.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(edit_usercard_email.getText().toString().trim()).matches())
                {
                    Toast.makeText(Admin_ViewSelectUser.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = edit_usercard_name.getText().toString().trim();
                String usergroup = edit_usercard_usergroup.getText().toString().trim();
                String email = edit_usercard_email.getText().toString().trim();
                String phone = edit_usercard_phone.getText().toString().trim();

                if (userID == null || userID.isEmpty()) {
                    Toast.makeText(Admin_ViewSelectUser.this, "User ID doesn't exist", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference docRef = db.collection("users").document(userID);

                docRef.update("name", name,"user_group", usergroup, "email", email, "phone", phone, "authorised", authorised).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Admin_ViewSelectUser.this, "User details saved", Toast.LENGTH_SHORT).show();

                            originalName = name;
                            originalUsergroup = usergroup;
                            originalEmail = email;
                            originalPhone = phone;
                            originalAuthorised = authorised;

                            btn_usercard_save.setVisibility(View.GONE);
                            btn_usercard_edit.setText("Edit Details");
                            setUnabled();
                        } else {
                            Toast.makeText(Admin_ViewSelectUser.this, "Failed to update user details.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void setEnabled(){
        edit_usercard_name.setEnabled(true);
        edit_usercard_usergroup.setEnabled(true);
        edit_usercard_email.setEnabled(true);
        edit_usercard_phone.setEnabled(true);
        checkAuthorised();
    }

    public void setUnabled(){
        edit_usercard_name.setEnabled(false);
        edit_usercard_usergroup.setEnabled(false);
        edit_usercard_email.setEnabled(false);
        edit_usercard_phone.setEnabled(false);
        btn_usercard_activate.setVisibility(View.GONE);
        btn_usercard_deactivate.setVisibility(View.GONE);
        btn_usercard_deactivate_cancel.setVisibility(View.GONE);
        btn_usercard_deactivate_confirm.setVisibility(View.GONE);
    }

    public void checkAuthorised(){
        if (originalAuthorised.equals("false")){
            btn_usercard_activate.setVisibility(View.VISIBLE);
            btn_usercard_deactivate.setVisibility(View.GONE);
        }else{
            btn_usercard_activate.setVisibility(View.GONE);
            btn_usercard_deactivate.setVisibility(View.VISIBLE);
        }
    }
}
