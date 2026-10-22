package Admin_Greg.Users.UserDetails;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AGU.gardenhands.R;

import java.util.ArrayList;

import Admin_Greg.Users.ViewSelectUser.Admin_ViewSelectUser;

public class Admin_UserAdapter extends RecyclerView.Adapter<Admin_UserViewHolder> {

    ArrayList<Admin_User> allUsers;

    public Admin_UserAdapter(ArrayList<Admin_User> allUsers) {
        this.allUsers = allUsers;
    }

    @NonNull
    @Override
    public Admin_UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_user_card,parent,false);
        return new Admin_UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull Admin_UserViewHolder holder,int position) {

        Admin_User user = allUsers.get(position);

        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvUserGroup.setText(user.getUser_group());
        holder.tvPhone.setText(user.getPhone());
        holder.tvAuthorised.setText(user.getAuthorised());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Admin_ViewSelectUser.class);
                intent.putExtra("userId", user.getUserId());
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("user_group", user.getUser_group());
                intent.putExtra("phone", user.getPhone());
                intent.putExtra("authorised", user.getAuthorised());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return allUsers.size();
    }

}