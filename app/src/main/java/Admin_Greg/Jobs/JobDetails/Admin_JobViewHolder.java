package Admin_Greg.Jobs.JobDetails;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

public class Admin_JobViewHolder extends RecyclerView.ViewHolder {

    TextView txtJobTitle;
    TextView txtJobType;
    TextView txtJobLocation;
    TextView txtJobDate;
    TextView txtJobFee;
    TextView txtJobStatus;
    TextView txtJobCustomer;
    TextView txtJobGardener;

    public Admin_JobViewHolder(@NonNull View itemView) {
        super(itemView);

        txtJobTitle = itemView.findViewById(R.id.txt_job_title);
        txtJobType = itemView.findViewById(R.id.txt_job_type);
        txtJobLocation = itemView.findViewById(R.id.txt_job_location);
        txtJobDate = itemView.findViewById(R.id.txt_job_date);
        txtJobFee = itemView.findViewById(R.id.txt_job_fee);
        txtJobStatus = itemView.findViewById(R.id.txt_job_status);
        txtJobCustomer = itemView.findViewById(R.id.txt_job_customer);
        txtJobGardener = itemView.findViewById(R.id.txt_job_gardener);
    }
}