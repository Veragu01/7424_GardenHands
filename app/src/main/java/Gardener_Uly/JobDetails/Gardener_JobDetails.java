package Gardener_Uly.JobDetails;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Gardener_Uly.Gardener_Dashboard;
import Gardener_Uly.MyJobs.Gardener_MyJobs;
import Gardener_Uly.ProvideQuote.Gardener_ProvideQuote;

public class Gardener_JobDetails extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    DocumentSnapshot jobDocument, custDoc;

    LinearLayout LoadingSection, DetailsSection, MyJobButtons, GJD_DateLayout, GJD_FeeLayout;
    TextView txtTitle, txtDescription, txtJobType, txtJobLocation, txtJobSize, txtJobDate, txtGJD_JobFee;
    Button btnProvideQuote, btnMarkAsComplete, btnCallCustomer, btnEmailCustomer;


    ProgressBar pbMyJobs;
    String jobID;

    private DialogInterface.OnClickListener dialogClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gardener_job_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        txtTitle = findViewById(R.id.txtGJD_Title);
        txtDescription = findViewById(R.id.txtGJD_Description);
        txtJobType = findViewById(R.id.txtGJD_JobType);
        txtJobSize = findViewById(R.id.txtGJD_JobSize);
        txtJobLocation = findViewById(R.id.txtGJD_JobLocation);
        txtJobDate = findViewById(R.id.txtGJD_JobDate);
        txtGJD_JobFee = findViewById(R.id.txtGJD_JobFee);
        btnMarkAsComplete = findViewById(R.id.btnGJD_MarkAsCompleted);
        btnEmailCustomer = findViewById(R.id.btnGJD_Email);
        btnCallCustomer = findViewById(R.id.btnGJD_Call);


        LoadingSection = findViewById(R.id.GJD_JobLoading);
        DetailsSection = findViewById(R.id.GJD_JobLayout);
        MyJobButtons = findViewById(R.id.GJD_MyJobLayout);
        btnProvideQuote = findViewById(R.id.btnGJD_ProvideQuote);
        GJD_DateLayout = findViewById(R.id.GJD_DateLayout);
        GJD_FeeLayout = findViewById(R.id.GJD_FeeLayout);

        LoadingSection.setVisibility(View.VISIBLE);
        DetailsSection.setVisibility(View.GONE);
        MyJobButtons.setVisibility(View.GONE);
        btnProvideQuote.setVisibility(View.GONE);
        GJD_DateLayout.setVisibility(View.GONE);
        GJD_FeeLayout.setVisibility(View.GONE);



        Intent intent = getIntent();
        jobID = intent.getStringExtra("docID");
        String source = intent.getStringExtra("source");









        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        DocumentReference docRef = db.collection("jobs").document(jobID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    jobDocument = task.getResult();
                    txtTitle.setText(jobDocument.get("job_title").toString());
                    txtDescription.setText(jobDocument.get("job_description").toString());
                    txtJobType.setText(jobDocument.get("job_type").toString());
                    txtJobSize.setText(jobDocument.get("size").toString());
                    String location = jobDocument.get("location").toString();
                    txtJobDate.setText("");
                    txtGJD_JobFee.setText("");

                    if (source.equals("MyJobs")) {
                        txtJobDate.setText(jobDocument.get("date_time").toString());
                        txtGJD_JobFee.setText(jobDocument.get("fee").toString());
                        txtJobLocation.setText(location);
                        DetailsSection.setVisibility(View.VISIBLE);
                        GJD_DateLayout.setVisibility(View.VISIBLE);
                        MyJobButtons.setVisibility(View.VISIBLE);
                        GJD_FeeLayout.setVisibility(View.VISIBLE);
                        LoadingSection.setVisibility(View.GONE);

                    } else if (source.equals("OpenJobs")) {
                        String[] locationSplit = location.split(",");
                        location = "";
                        for (int i = 1; i<locationSplit.length;i++) { // adds all of address except street number and name
                            location+=locationSplit[i];
                        }
                        txtJobLocation.setText(location.trim()); // sets altered location
                        DetailsSection.setVisibility(View.VISIBLE);
                        btnProvideQuote.setVisibility(View.VISIBLE);
                        LoadingSection.setVisibility(View.GONE);
                    }
                }
            }
        });


        btnProvideQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Gardener_ProvideQuote.class);
                intent.putExtra("docID", jobID);
                startActivity(intent);
            }
        });

        btnMarkAsComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) { // switch to determine outcome based on what dialog button is clicked
                            case DialogInterface.BUTTON_POSITIVE:
                                docRef.update("job_status", "work_completed");
                                Toast.makeText(Gardener_JobDetails.this, "Job marked as Completed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Gardener_JobDetails.this, Gardener_Dashboard.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();

                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Gardener_JobDetails.this);
                builder.setMessage("Are you sure you want to mark this Job as Complete?\n\rYou will no longer be able to see this job.") // creates alert confirmation message
                        .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });


        btnEmailCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerID = jobDocument.get("customer").toString();
                DocumentReference customerRef = db.collection("users").document(customerID);
                customerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // gets customer phone number
                        if (task.isSuccessful()) {
                            custDoc = task.getResult();
                            String emailAddress = custDoc.get("email").toString();  // giving null error??
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+emailAddress));
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        btnCallCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerID = jobDocument.get("customer").toString();
                DocumentReference customerRef = db.collection("users").document(customerID);
                customerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // gets customer phone number
                        if (task.isSuccessful()) {
                            custDoc = task.getResult();
                            String phoneNumber = custDoc.get("phone").toString();  // giving null error??
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
                            startActivity(intent);
                        }
                    }
                });

            }
        });



    }
}