package Gardener_Uly.MyJobs;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

import Gardener_Uly.Gardener_Dashboard;
import Gardener_Uly.JobDetails.Gardener_JobDetails;

public class Gardener_MyJobsCVHolder extends RecyclerView.ViewHolder {
    CardView cvJob;
    TextView txtTitle, txtJobType, txtJobSize, txtJobDate, txtJobID;

    public Gardener_MyJobsCVHolder(@NonNull View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.GCVMyJob_Title);
        txtJobType = itemView.findViewById(R.id.GCVMyJob_Type);
        cvJob = itemView.findViewById(R.id.GCV_MyJob_Card);
        txtJobSize = itemView.findViewById(R.id.GCVMyJob_Size);
        txtJobDate = itemView.findViewById(R.id.GCVMyJob_Date);
        txtJobID = itemView.findViewById(R.id.GCVMyJob_ID);



        cvJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cvJob.getContext(), Gardener_JobDetails.class);
                intent.putExtra("docID", txtJobID.getText().toString());
                intent.putExtra("source", "MyJobs");
                cvJob.getContext().startActivity(intent);
            }
        });
    }
}
