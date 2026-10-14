package Admin_Greg.Jobs.ViewSelectJob;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.AGU.gardenhands.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Admin_ViewSelectJob extends AppCompatActivity {

    EditText edit_jobcard_title, edit_jobcard_description, edit_jobcard_location, edit_jobcard_customer, edit_jobcard_gardener, edit_jobcard_status;

    Button btn_jobcard_verify, btn_jobcard_deny, btn_jobcard_close, btn_jobcard_delete;

    FirebaseFirestore db;

    String documentId;
    String screenMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_view_select_job);

        edit_jobcard_title = findViewById(R.id.edit_jobcard_title);
        edit_jobcard_description = findViewById(R.id.edit_jobcard_description);
        edit_jobcard_location = findViewById(R.id.edit_jobcard_location);
        edit_jobcard_customer = findViewById(R.id.edit_jobcard_customer);
        edit_jobcard_gardener = findViewById(R.id.edit_jobcard_gardener);
        edit_jobcard_status = findViewById(R.id.edit_jobcard_status);

        btn_jobcard_verify = findViewById(R.id.btn_jobcard_verify);
        btn_jobcard_deny = findViewById(R.id.btn_jobcard_deny);
        btn_jobcard_close = findViewById(R.id.btn_jobcard_close);
        btn_jobcard_delete = findViewById(R.id.btn_jobcard_delete);

        db = FirebaseFirestore.getInstance();

        documentId = getIntent().getStringExtra("documentId");
        screenMode = getIntent().getStringExtra("screenMode");

        edit_jobcard_title.setText(getIntent().getStringExtra("job_title"));
        edit_jobcard_description.setText(getIntent().getStringExtra("job_description"));
        edit_jobcard_location.setText(getIntent().getStringExtra("location"));

        final String customerId = getIntent().getStringExtra("customer");
        final String gardenerId = getIntent().getStringExtra("gardener");

        loadUserNames(customerId, gardenerId);

        edit_jobcard_status.setText(getIntent().getStringExtra("job_status"));

        edit_jobcard_title.setEnabled(false);
        edit_jobcard_description.setEnabled(false);
        edit_jobcard_location.setEnabled(false);
        edit_jobcard_customer.setEnabled(false);
        edit_jobcard_gardener.setEnabled(false);
        edit_jobcard_status.setEnabled(false);

        btn_jobcard_verify.setVisibility(View.GONE);
        btn_jobcard_deny.setVisibility(View.GONE);
        btn_jobcard_close.setVisibility(View.GONE);
        btn_jobcard_delete.setVisibility(View.GONE);

        if ("verify".equals(screenMode)) {
            btn_jobcard_verify.setVisibility(View.VISIBLE);
            btn_jobcard_deny.setVisibility(View.VISIBLE);
        } else if ("close".equals(screenMode)) {
            btn_jobcard_close.setVisibility(View.VISIBLE);
        } else if ("delete".equals(screenMode)) {
            btn_jobcard_delete.setVisibility(View.VISIBLE);
        }

        btn_jobcard_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateJobStatus("verified");
            }
        });

        btn_jobcard_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDenyJob();
            }
        });

        btn_jobcard_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateJobStatus("closed");
            }
        });

        btn_jobcard_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteJob();
            }
        });
    }

    private void loadUserNames(final String customerId, final String gardenerId) {
        final String trimmedCustId = customerId != null ? customerId.trim() : "";
        final String trimmedGardId = gardenerId != null ? gardenerId.trim() : "";


        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String customerName = null;
                        String gardenerName = null;

                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docId = document.getId().trim();
                                String name = document.getString("name");
                                String email = document.getString("email");

                                if ((docId.equals(trimmedCustId) || trimmedCustId.equalsIgnoreCase(email) || trimmedCustId.equalsIgnoreCase(name))) {
                                    customerName = name;
                                }
                                if ((docId.equals(trimmedGardId) || trimmedGardId.equalsIgnoreCase(email) || trimmedGardId.equalsIgnoreCase(name))) {
                                    gardenerName = name;
                                }
                            }
                        }

                        if (!trimmedCustId.isEmpty()) {
                            edit_jobcard_customer.setText(customerName != null && !customerName.trim().isEmpty() ? customerName : trimmedCustId);
                        } else {
                            edit_jobcard_customer.setText("No customer");
                        }

                        if (trimmedGardId.isEmpty()) {
                            edit_jobcard_gardener.setText("No gardener yet");
                        } else {
                            edit_jobcard_gardener.setText(gardenerName != null && !gardenerName.trim().isEmpty() ? gardenerName : trimmedGardId);
                        }
                    }
                });
    }

    private void updateJobStatus(final String status) {
        if (documentId == null || documentId.trim().isEmpty()) {
            Toast.makeText(this, "Job document ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        setButtonsEnabled(false);

        db.collection("jobs")
                .document(documentId)
                .update("job_status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Admin_ViewSelectJob.this, "Job " + status + " successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setButtonsEnabled(true);
                        Toast.makeText(Admin_ViewSelectJob.this, "Failed to update job status.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void confirmDenyJob() {

        new AlertDialog.Builder(this)
                .setTitle("Deny Job")
                .setMessage("Are you sure you want to deny this job?\nThis will also delete the job.")
                .setPositiveButton("Deny & Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        denyJob();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void denyJob() {
        setButtonsEnabled(false);

        db.collection("jobs")
                .document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Admin_ViewSelectJob.this, "Job denied and deleted successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setButtonsEnabled(true);
                        Toast.makeText(Admin_ViewSelectJob.this, "Failed to deny and delete job.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void confirmDeleteJob() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteJob();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deleteJob() {
        setButtonsEnabled(false);

        db.collection("jobs")
                .document(documentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document == null || !document.exists()) {
                            setButtonsEnabled(true);
                            Toast.makeText(Admin_ViewSelectJob.this, "Job no longer exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String status = document.getString("job_status");
                        if (status != null) {
                            status = status.trim().toLowerCase();
                        }

                        if (!"in_progress".equals(status) && !"in progress".equals(status) && !"verified".equals(status)) {
                            setButtonsEnabled(true);
                            Toast.makeText(Admin_ViewSelectJob.this, "Only jobs that are in_progress or verified can be deleted.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        db.collection("jobs")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Admin_ViewSelectJob.this, "Job deleted successfully.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        setButtonsEnabled(true);
                                        Toast.makeText(Admin_ViewSelectJob.this, "Failed to delete job.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setButtonsEnabled(true);
                        Toast.makeText(Admin_ViewSelectJob.this, "Failed to check job status.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setButtonsEnabled(boolean enabled) {
        btn_jobcard_verify.setEnabled(enabled);
        btn_jobcard_deny.setEnabled(enabled);
        btn_jobcard_close.setEnabled(enabled);
        btn_jobcard_delete.setEnabled(enabled);
    }
}
