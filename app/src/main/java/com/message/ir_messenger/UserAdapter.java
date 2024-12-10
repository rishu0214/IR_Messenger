package com.message.ir_messenger;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder>{
    MainActivity mainActivity;
    ArrayList<Users> usersArrayList;
    public UserAdapter(MainActivity mainActivity, ArrayList<Users> usersArrayList) {
    this.mainActivity = mainActivity;
    this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        Users users = usersArrayList.get(position);
        holder.uiN.setText(users.getUserName());
        holder.uiS.setText(users.getStatus());
        Picasso.get().load(users.getProfilepic()).into(holder.profilerg);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MCIntent = new Intent(mainActivity, ChatWin.class);
                MCIntent.putExtra("namee", users.getUserName());
                MCIntent.putExtra("receiverImg", users.getProfilepic());
                MCIntent.putExtra("uid", users.getUserId());
                mainActivity.startActivity(MCIntent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView profilerg;
        TextView uiN, uiS;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            profilerg = itemView.findViewById(R.id.profilerg);
            uiN = itemView.findViewById(R.id.uiN);
            uiS = itemView.findViewById(R.id.uiS);
        }
    }
}
