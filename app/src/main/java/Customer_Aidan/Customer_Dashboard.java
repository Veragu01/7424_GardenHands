package Customer_Aidan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.AGU.gardenhands.R;

public class Customer_Dashboard extends AppCompatActivity {

    Button btnCCreateJob, btnCMyJobs, btnCMyProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dahsboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCCreateJob = findViewById(R.id.btnCDashCreateJob);
        btnCMyJobs = findViewById(R.id.btnCDashMyJobs);
        btnCMyProfile = findViewById(R.id.btnCDashMyProfile);

        btnCCreateJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Customer_Dashboard.this, Customer_Create_Job.class);
                startActivity(intent);
            }
        });

        btnCMyJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Customer_Dashboard.this, Customer_My_Jobs.class);
                startActivity(intent);
            }
        });

        btnCMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Customer_Dashboard.this, Customer_Profile.class);
                startActivity(intent);
            }
        });
    }
}