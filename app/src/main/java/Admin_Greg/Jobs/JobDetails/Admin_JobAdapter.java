package Admin_Greg.Jobs.JobDetails;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        String status = job.getJob_status();
        String resolvedStatus;
        if (status != null) {
            resolvedStatus = status;
        } else {
            resolvedStatus = "";
        }
        holder.txtJobStatus.setText(resolvedStatus);

        boolean isJobActiveOrBeyond = status != null && (
                status.equalsIgnoreCase("in_progress") || 
                status.equalsIgnoreCase("in progress") || 
                status.equalsIgnoreCase("work_completed") || 
                status.equalsIgnoreCase("finished") || 
                status.equalsIgnoreCase("closed")
        );

        if (isJobActiveOrBeyond) {
            holder.layoutDateRow.setVisibility(View.VISIBLE);
            holder.layoutFeeRow.setVisibility(View.VISIBLE);
            holder.layoutGardenerRow.setVisibility(View.VISIBLE);

            String jobDate;
            if (job.getDate_time() != null) {
                jobDate = job.getDate_time();
            } else {
                jobDate = "Not specified";
            }
            holder.txtJobDate.setText(jobDate);

            if (job.getFee() != null) {
                holder.txtJobFee.setText(String.format("$%.2f", job.getFee()));
            } else {
                holder.txtJobFee.setText("Fee not set");
            }
        } else {
            holder.layoutDateRow.setVisibility(View.GONE);
            holder.layoutFeeRow.setVisibility(View.GONE);
            holder.layoutGardenerRow.setVisibility(View.GONE);
        }

        holder.txtJobCustomer.setText("Loading...");

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
        } else {
            holder.txtJobGardener.setText("No gardener yet");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Admin_ViewSelectJob.class);

                intent.putExtra("documentId", job.getDocumentId());
                intent.putExtra("customer", job.getCustomerID());
                intent.putExtra("job_title", job.getJob_title());
                intent.putExtra("job_type", job.getJob_type());
                intent.putExtra("job_description", job.getJob_description());
                intent.putExtra("location", job.getLocation());
                intent.putExtra("job_status", job.getJob_status());
                intent.putExtra("size", job.getSize());
                intent.putExtra("screenMode", screenMode);

                String currentStatus = job.getJob_status();
                if (currentStatus != null && (currentStatus.equalsIgnoreCase("in_progress") || currentStatus.equalsIgnoreCase("in progress") || currentStatus.equalsIgnoreCase("work_completed") || currentStatus.equalsIgnoreCase("finished") || currentStatus.equalsIgnoreCase("closed"))) {
                    intent.putExtra("date_time", job.getDate_time());
                    intent.putExtra("fee", job.getFee());
                    intent.putExtra("gardener", job.getGardenerID());
                }

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allJobs.size();
    }
}
