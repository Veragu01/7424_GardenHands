package Customer_Aidan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Customer_View_Quotes extends AppCompatActivity implements QuoteAdapter.OnQuoteClickListener {

    private RecyclerView rvCustomerQuotes;
    private ProgressBar pbLoadingQuotes;
    private TextView tvEmptyQuotes;
    private Button btnBackToDashboard;

    private QuoteAdapter quoteAdapter;
    private List<Quote> quoteList;

    private FirebaseFirestore db;

    private String targetJobId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_view_quotes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        if (getIntent() != null) {
            targetJobId = getIntent().getStringExtra("job_id");
            if (targetJobId == null) {
                targetJobId = getIntent().getStringExtra("jobId");
            }
        }

        rvCustomerQuotes = findViewById(R.id.rvCustomerQuotes);
        pbLoadingQuotes = findViewById(R.id.pbLoadingQuotes);
        tvEmptyQuotes = findViewById(R.id.tvEmptyQuotes);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);

        quoteList = new ArrayList<>();
        quoteAdapter = new QuoteAdapter(quoteList, this);

        rvCustomerQuotes.setLayoutManager(new LinearLayoutManager(this));
        rvCustomerQuotes.setAdapter(quoteAdapter);

        btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(Customer_View_Quotes.this, Customer_Dashboard.class);
            startActivity(intent);
            finish();
        });

        loadCustomerQuotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh quotes list when returning to this screen
        loadCustomerQuotes();
    }

    private void loadCustomerQuotes() {
        if (targetJobId == null || targetJobId.trim().isEmpty()) {
            pbLoadingQuotes.setVisibility(View.GONE);
            tvEmptyQuotes.setText("No job specified to load quotes.");
            tvEmptyQuotes.setVisibility(View.VISIBLE);
            return;
        }

        pbLoadingQuotes.setVisibility(View.VISIBLE);
        tvEmptyQuotes.setVisibility(View.GONE);

        // Fetch quotes from "quotes" collection
        db.collection("quotes")
                .get()
                .addOnCompleteListener(task -> {
                    pbLoadingQuotes.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        quoteList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Quote quote = Quote.fromDocument(document);

                            // Check if this quote belongs to the target job
                            if (targetJobId.equals(quote.getJobId())) {
                                quoteList.add(quote);
                                fetchGardenerNameIfNeeded(quote);
                            }
                        }

                        if (quoteList.isEmpty()) {
                            tvEmptyQuotes.setText("No quotes found for this job.");
                            tvEmptyQuotes.setVisibility(View.VISIBLE);
                        } else {
                            tvEmptyQuotes.setVisibility(View.GONE);
                        }

                        quoteAdapter.notifyDataSetChanged();
                    } else {
                        tvEmptyQuotes.setText("Failed to load quotes.");
                        tvEmptyQuotes.setVisibility(View.VISIBLE);
                        Toast.makeText(Customer_View_Quotes.this, "Error fetching quotes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchGardenerNameIfNeeded(Quote quote) {
        if ((quote.getGardenerName() == null || quote.getGardenerName().isEmpty())
                && quote.getGardenerId() != null && !quote.getGardenerId().isEmpty()) {

            db.collection("users").document(quote.getGardenerId()).get()
                    .addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful() && userTask.getResult() != null) {
                            DocumentSnapshot userDoc = userTask.getResult();
                            if (userDoc.exists()) {
                                String name = userDoc.getString("name");
                                if (name != null && !name.isEmpty()) {
                                    quote.setGardenerName(name);
                                    quoteAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onQuoteClick(Quote quote) {
        Intent intent = new Intent(Customer_View_Quotes.this, Customer_Quote_Details.class);
        intent.putExtra("quote", quote);
        // Also put explicit strings in case
        intent.putExtra("quote_id", quote.getQuoteId());
        intent.putExtra("customer_id", quote.getCustomerId());
        intent.putExtra("gardener_id", quote.getGardenerId());
        intent.putExtra("gardener_name", quote.getGardenerName());
        intent.putExtra("fee", quote.getFee());
        intent.putExtra("timeslot", quote.getTimeslot());
        intent.putExtra("notes", quote.getNotes());
        intent.putExtra("status", quote.getStatus());
        intent.putExtra("job_id", quote.getJobId());
        startActivity(intent);
    }
}
