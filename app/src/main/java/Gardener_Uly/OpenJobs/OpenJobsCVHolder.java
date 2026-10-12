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
import Gardener_Uly.JobDetails.Gardener_JobDetails;
import Gardener_Uly.Jobs;
import Gardener_Uly.MyJobs.Gardener_MyJobs;

public class OpenJobsCVHolder extends RecyclerView.ViewHolder{

    CardView cvJob;
    TextView txtTitle, txtJobType, txtJobID;
    ImageView imgJob;

//    List<Jobs> jobsList;

    public OpenJobsCVHolder(@NonNull View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.CVOpenJob_Title);
        txtJobType = itemView.findViewById(R.id.CVOpenJob_Type);
        cvJob = itemView.findViewById(R.id.CV_OpenJob_Card);
        txtJobID = itemView.findViewById(R.id.GCVOpenJob_ID);


        cvJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cvJob.getContext(), Gardener_JobDetails.class);
                intent.putExtra("docID", txtJobID.getText().toString());
                intent.putExtra("source", "OpenJobs");
                cvJob.getContext().startActivity(intent);
            }
        });

    }
}
