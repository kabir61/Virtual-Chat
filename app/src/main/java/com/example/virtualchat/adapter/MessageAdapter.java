package com.example.virtualchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.virtualchat.R;
import com.example.virtualchat.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewAdapter> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    private Context context;
    private List<Chat> mChat;
    private String imageUrl;

    public MessageAdapter(Context context, List<Chat> mChat, String imageUrl) {
        this.context = context;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
    }

    FirebaseUser fUser;
    @NonNull
    @Override
    public MessageViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageViewAdapter(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageViewAdapter(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewAdapter holder, int position) {

        Chat chat=mChat.get(position);
        holder.show_message.setText(chat.getMessage());

        if (imageUrl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(imageUrl).into(holder.profile_image);
        }

        if (position==mChat.size()-1){
            if (chat.isIsseen()){
                holder.seen_text.setText("seen");
            }else {
                holder.seen_text.setText("Delivered");
            }
        }
        else {
            holder.seen_text.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class MessageViewAdapter extends RecyclerView.ViewHolder {
        private TextView show_message;
        private ImageView profile_image;
        private TextView seen_text;
        public MessageViewAdapter(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            seen_text=itemView.findViewById(R.id.seen_text);

        }
    }
    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
