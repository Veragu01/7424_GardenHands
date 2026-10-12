package Gardener_Uly.MyJobs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

import java.util.List;

import Gardener_Uly.Jobs;


public class Gardener_MyJobsCVAdapter extends RecyclerView.Adapter<Gardener_MyJobsCVHolder> {
    private List<Jobs> jobsList;

    public Gardener_MyJobsCVAdapter(List<Jobs> jobsList) {
        this.jobsList=jobsList;
    }

    @NonNull
    @Override
    public Gardener_MyJobsCVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_gardener_my_jobs_card, parent, false);
        return new Gardener_MyJobsCVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Gardener_MyJobsCVHolder holder, int position) {
        Jobs jobs = jobsList.get(position);

        holder.txtTitle.setText(jobs.getJob_title());
        holder.txtJobType.setText(jobs.getJob_type());
        holder.txtJobSize.setText(jobs.getSize());
        holder.txtJobDate.setText(jobs.getDate_time());
        holder.txtJobID.setText(jobs.getDocumentId());
    }

    @Override
    public int getItemCount() {
        return jobsList.size();
    }

}
