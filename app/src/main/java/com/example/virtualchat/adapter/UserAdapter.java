package com.example.virtualchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.virtualchat.MessageActivity;
import com.example.virtualchat.R;
import com.example.virtualchat.model.Chat;
import com.example.virtualchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewAdapter>{
    private Context context;
    private List<User> mUser;
    private boolean isChat;


    String theLastMessage;

    public UserAdapter(Context context, List<User> mUser,boolean isChat) {
        this.context = context;
        this.mUser = mUser;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public UserViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new UserViewAdapter(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewAdapter holder, int position) {
        User user=mUser.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
        }

        if (isChat){
            lastMessage(user.getId(),holder.last_mesg);
        }else {
            holder.last_mesg.setVisibility(View.GONE);
        }
        if (isChat){
            if (user.getStatus().equals("online")){
                holder.image_on.setVisibility(View.VISIBLE);
                holder.image_off.setVisibility(View.GONE);
            }else {
                holder.image_on.setVisibility(View.GONE);
                holder.image_off.setVisibility(View.VISIBLE);
            }
        }else {
            holder.image_on.setVisibility(View.GONE);
            holder.image_off.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }
    public void filteredList(ArrayList<User> filterList) {
        mUser = filterList;
        notifyDataSetChanged();

    }

    public class UserViewAdapter extends RecyclerView.ViewHolder {

        private TextView username,last_mesg;
        private CircleImageView profile_image,image_on,image_off;

        public UserViewAdapter(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);
            image_on=itemView.findViewById(R.id.image_on);
            image_off=itemView.findViewById(R.id.image_off);
            last_mesg=itemView.findViewById(R.id.last_mesg);


        }
    }

    public void lastMessage(String  userid,TextView last_mesg){
        theLastMessage="default";

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                    chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage=chat.getMessage();
                    }
                }
               switch (theLastMessage){
                   case "default":
                       last_mesg.setText("No message");
                       break;

                   default:
                       last_mesg.setText(theLastMessage);
                       break;
               }
                theLastMessage= "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}