package Admin_Greg;

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

import Admin_Greg.Jobs.Admin_CloseFinishedJobs;
import Admin_Greg.Jobs.Admin_DeleteJobs;
import Admin_Greg.Jobs.Admin_VerifyNewJobs;
import Admin_Greg.Users.CreateNewUser.Admin_CreateNewUser;
import Admin_Greg.Users.ViewAllUsers.Admin_ViewAllUsers;

public class Admin_Dashboard extends AppCompatActivity {

    Button btn_CreateNewUser, btn_CloseFinishedJobs, btn_DeleteJobs, btn_VerifyNewJobs, btn_ViewAllUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_CreateNewUser = findViewById(R.id.btn_AdminDashboardCreateUser);
        btn_CloseFinishedJobs = findViewById(R.id.btn_AdminDashboardCloseFinishedJobs);
        btn_DeleteJobs = findViewById(R.id.btn_Admin_DashboardDeleteJobs);
        btn_VerifyNewJobs = findViewById(R.id.btn_AdminDashboardVerifyNewJobs);
        btn_ViewAllUsers = findViewById(R.id.btn_AdminDashboardViewAllUsers);


        //Navigation
        btn_CreateNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Dashboard.this, Admin_CreateNewUser.class);
                startActivity(intent);
            }
        });

        btn_CloseFinishedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Dashboard.this, Admin_CloseFinishedJobs.class);
                startActivity(intent);
            }
        });

        btn_DeleteJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Dashboard.this, Admin_DeleteJobs.class);
                startActivity(intent);
            }
        });

        btn_VerifyNewJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Dashboard.this, Admin_VerifyNewJobs.class);
                startActivity(intent);
            }
        });

        btn_ViewAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Dashboard.this, Admin_ViewAllUsers.class);
                startActivity(intent);
            }
        });
    }
}