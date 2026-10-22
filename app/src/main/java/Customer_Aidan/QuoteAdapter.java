package Customer_Aidan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    public interface OnQuoteClickListener {
        void onQuoteClick(Quote quote);
    }

    private List<Quote> quoteList;
    private final OnQuoteClickListener listener;

    public QuoteAdapter(List<Quote> quoteList, OnQuoteClickListener listener) {
        this.quoteList = quoteList;
        this.listener = listener;
    }

    public void setQuoteList(List<Quote> quoteList) {
        this.quoteList = quoteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quoteList.get(position);
        holder.bind(quote, listener);
    }

    @Override
    public int getItemCount() {
        return quoteList != null ? quoteList.size() : 0;
    }

    static class QuoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtGardenerName;
        private final TextView txtQuoteFee;
        private final TextView txtQuoteTimeslot;
        private final TextView txtQuoteStatus;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGardenerName = itemView.findViewById(R.id.txtGardenerName);
            txtQuoteFee = itemView.findViewById(R.id.txtQuoteFee);
            txtQuoteTimeslot = itemView.findViewById(R.id.txtQuoteTimeslot);
            txtQuoteStatus = itemView.findViewById(R.id.txtQuoteStatus);
        }

        public void bind(final Quote quote, final OnQuoteClickListener listener) {
            String name = quote.getGardenerName();
            if (name == null || name.isEmpty()) {
                name = "Gardener (" + (quote.getGardenerId() != null ? quote.getGardenerId() : "Unknown") + ")";
            }
            txtGardenerName.setText(name);

            String feeStr = quote.getFee();
            if (feeStr != null && !feeStr.startsWith("$")) {
                feeStr = "$" + feeStr;
            }
            txtQuoteFee.setText(feeStr != null ? feeStr : "$0.00");

            txtQuoteTimeslot.setText(quote.getFormattedTimeslot());

            String status = quote.getStatus();
            txtQuoteStatus.setText(status != null ? status : "");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuoteClick(quote);
                }
            });
        }
    }
}
