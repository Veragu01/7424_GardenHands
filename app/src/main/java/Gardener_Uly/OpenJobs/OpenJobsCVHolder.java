package Gardener_Uly.OpenJobs;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

import java.util.List;

import Gardener_Uly.Gardener_Dashboard;
import Gardener_Uly.Jobs;

public class OpenJobsCVHolder extends RecyclerView.ViewHolder{

    CardView cvJob;
    TextView txtTitle, txtJobType;
    ImageView imgJob;

//    List<Jobs> jobsList;

    public OpenJobsCVHolder(@NonNull View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.CVOpenJob_Title);
        txtJobType = itemView.findViewById(R.id.CVOpenJob_Type);
        cvJob = itemView.findViewById(R.id.CV_OpenJob_Card);

        cvJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cvJob.getContext(), Gardener_Dashboard.class);
                cvJob.getContext().startActivity(intent);
            }
        });

    }
}
