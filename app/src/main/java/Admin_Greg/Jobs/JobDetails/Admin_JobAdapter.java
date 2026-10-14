package Admin_Greg.Jobs.JobDetails;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Admin_Greg.Jobs.ViewSelectJob.Admin_ViewSelectJob;

public class Admin_JobAdapter extends RecyclerView.Adapter<Admin_JobViewHolder> {

    ArrayList<Admin_Job> allJobs;
    String screenMode; //variable for setting the button type for admin job uses

    public Admin_JobAdapter(ArrayList<Admin_Job> allJobs) {
        this.allJobs = allJobs;
        this.screenMode = "";
    }

    public Admin_JobAdapter(ArrayList<Admin_Job> allJobs, String screenMode) {
        this.allJobs = allJobs;
        this.screenMode = screenMode;
    }

    @NonNull
    @Override
    public Admin_JobViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_job_card, parent, false);
        return new Admin_JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull final Admin_JobViewHolder holder, int position) {

        Admin_Job job = allJobs.get(position);

        holder.txtJobTitle.setText(job.getJob_title());
        holder.txtJobType.setText(job.getJob_type());
        holder.txtJobLocation.setText(job.getLocation());
        holder.txtJobDate.setText(job.getDate_time());

        if (job.getFee() != null) {
            holder.txtJobFee.setText(String.format("$%.2f", job.getFee()));
        } else {
            holder.txtJobFee.setText("Fee not set");
        }

        holder.txtJobStatus.setText(job.getJob_status());

        holder.txtJobCustomer.setText("Loading...");
        holder.txtJobGardener.setText(job.getGardenerID() == null || job.getGardenerID().trim().isEmpty() ? "No gardener yet" : "Loading...");

        // get customer name
        String customerId = job.getCustomerID();
        if (customerId != null && !customerId.trim().isEmpty()) {
            FirebaseFirestore.getInstance().collection("users").document(customerId.trim()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        holder.txtJobCustomer.setText(name);
                    }
                }
            });
        } else {
            holder.txtJobCustomer.setText("No customer");
        }

        // get gardener name
        String gardenerId = job.getGardenerID();
        if (gardenerId != null && !gardenerId.trim().isEmpty()) {
            FirebaseFirestore.getInstance().collection("users").document(gardenerId.trim()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        holder.txtJobGardener.setText(name);
                    }
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Admin_ViewSelectJob.class);

                intent.putExtra("documentId", job.getDocumentId());
                intent.putExtra("customer", job.getCustomerID());
                intent.putExtra("gardener", job.getGardenerID());
                intent.putExtra("job_title", job.getJob_title());
                intent.putExtra("job_type", job.getJob_type());
                intent.putExtra("job_description", job.getJob_description());
                intent.putExtra("location", job.getLocation());
                intent.putExtra("date_time", job.getDate_time());
                intent.putExtra("fee", job.getFee());
                intent.putExtra("job_status", job.getJob_status());
                intent.putExtra("repeating", job.getRepeating());
                intent.putExtra("size", job.getSize());
                intent.putExtra("before_pic", job.getBefore_pic());
                intent.putExtra("screenMode", screenMode);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allJobs.size();
    }
}
