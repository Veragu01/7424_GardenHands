package Admin_Greg.Users.UserDetails;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

public class Admin_UserViewHolder extends RecyclerView.ViewHolder {

    TextView tvName;
    TextView tvUserGroup;
    TextView tvPhone;
    TextView tvAuthorised;
    TextView tvEmail;

    public Admin_UserViewHolder(@NonNull View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.txt_usercard_name);
        tvUserGroup = itemView.findViewById(R.id.txt_usercard_usergroup);
        tvEmail = itemView.findViewById(R.id.txt_usercard_email);
        tvPhone = itemView.findViewById(R.id.txt_usercard_phone);
        tvAuthorised = itemView.findViewById(R.id.txt_usercard_auth);
    }
}