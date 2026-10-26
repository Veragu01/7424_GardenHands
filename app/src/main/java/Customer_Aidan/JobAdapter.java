package Customer_Aidan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    private List<Job> jobList;
    private final OnJobClickListener listener;

    public JobAdapter(List<Job> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.listener = listener;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.bind(job, listener);
    }

    @Override
    public int getItemCount() {
        return jobList != null ? jobList.size() : 0;
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtJobTitle;
        private final TextView txtJobFee;
        private final TextView txtJobLocation;
        private final TextView txtJobDateTime;
        private final TextView txtJobStatus;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJobTitle = itemView.findViewById(R.id.txtJobTitle);
            txtJobFee = itemView.findViewById(R.id.txtJobFee);
            txtJobLocation = itemView.findViewById(R.id.txtJobLocation);
            txtJobDateTime = itemView.findViewById(R.id.txtJobDateTime);
            txtJobStatus = itemView.findViewById(R.id.txtJobStatus);
        }

        public void bind(final Job job, final OnJobClickListener listener) {
            txtJobTitle.setText(job.getJobTitle() != null && !job.getJobTitle().isEmpty() ? job.getJobTitle() : "Untitled Job");

            String status = job.getJobStatus();
            boolean isQuotePicked = status != null && !status.trim().isEmpty()
                    && !status.equalsIgnoreCase("pending")
                    && !status.equalsIgnoreCase("verified");

            if (isQuotePicked) {
                String feeStr = job.getFee();
                if (feeStr != null && !feeStr.trim().isEmpty() && !feeStr.equalsIgnoreCase("null")) {
                    if (!feeStr.startsWith("$")) {
                        feeStr = "$" + feeStr;
                    }
                    txtJobFee.setText(feeStr);
                } else {
                    txtJobFee.setText("N/A");
                }

                String dtStr = job.getDateTime();
                if (dtStr != null && !dtStr.trim().isEmpty() && !dtStr.equalsIgnoreCase("null")) {
                    txtJobDateTime.setText(dtStr);
                } else {
                    txtJobDateTime.setText("N/A");
                }
            } else {
                txtJobFee.setText("N/A");
                txtJobDateTime.setText("N/A");
            }

            txtJobLocation.setText(job.getLocation() != null && !job.getLocation().isEmpty() ? job.getLocation() : "No location specified");
            txtJobStatus.setText(status != null && !status.isEmpty() ? status : "pending");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJobClick(job);
                }
            });
        }
    }
}
