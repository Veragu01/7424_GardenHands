package Gardener_Uly.OpenJobs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

import java.util.List;

import Gardener_Uly.Jobs;

public class OpenJobsCVAdapter extends RecyclerView.Adapter<OpenJobsCVHolder>{

    private List<Jobs> jobsList;

    public OpenJobsCVAdapter(List<Jobs> jobsList) {
        this.jobsList=jobsList;
    }


    @NonNull
    @Override
    public OpenJobsCVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_gardener_open_jobs_card, parent, false);
        return new OpenJobsCVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenJobsCVHolder holder, int position) {
        Jobs jobs = jobsList.get(position);

        holder.txtTitle.setText(jobs.getJob_title());
        holder.txtJobType.setText(jobs.getJob_type());

    }

    @Override
    public int getItemCount() {
        return jobsList.size();
    }
}
