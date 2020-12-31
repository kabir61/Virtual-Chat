package com.example.virtualchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.virtualchat.adapter.MessageAdapter;
import com.example.virtualchat.fragment.ApiService;
import com.example.virtualchat.model.Chat;
import com.example.virtualchat.model.User;
import com.example.virtualchat.notification.ClientRetrofit;
import com.example.virtualchat.notification.Data;
import com.example.virtualchat.notification.MyResponse;
import com.example.virtualchat.notification.Sender;
import com.example.virtualchat.notification.Token;
import com.google.android.gms.common.api.Api;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private CircleImageView profile_image;
    private TextView username;
    private EditText send_text;
    private ImageButton btn_send;

    FirebaseUser fUser;
    DatabaseReference reference;

    MessageAdapter messageAdapter;

    List<Chat> mChat;

    RecyclerView send_recycler;

    String userid;
    Intent intent;
    ValueEventListener seenListener;

    ApiService apiService;
    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService= ClientRetrofit.getClient("https://fcm.googleapis.com").create(ApiService.class);
        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        send_text=findViewById(R.id.send_text);
        btn_send=findViewById(R.id.btn_send);
        send_recycler=findViewById(R.id.send_recycler);

        send_recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        send_recycler.setLayoutManager(layoutManager);

        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        fUser= FirebaseAuth.getInstance().getCurrentUser();

        intent=getIntent();
        userid=intent.getStringExtra("userid");


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String message = send_text.getText().toString();
                if (!message.equals("")) {
                    sendMessage(fUser.getUid(),userid,message);
                      } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                           }

                send_text.setText("");
            }
        });

         fUser=FirebaseAuth.getInstance().getCurrentUser();
         reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
         reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("Default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessage(fUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         seenMessage(userid);
    }

    public void seenMessage(final String userid){
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fUser.getUid())&& chat.getSender().equals(userid)){
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("isseen",true);
                        dataSnapshot.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


  /*  public void sendMessage(String message){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
       HashMap<String,Object> hashMap=new HashMap<>();
       hashMap.put("sender",myUid);
       hashMap.put("receiver",userid);
       hashMap.put("message",message);
       hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);

        send_text.setText("");

        //add user to chat fragment
        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("ChatList")
                .child(myUid)
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String msg=message;
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (notify){
                    sendNotification(userid,user.getUsername(),msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/
    public void sendMessage( String sender,final String userid, String message){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",userid);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);

        //add user to chat fragment
        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("ChatList")
                .child(fUser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String msg=message;
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (notify){
                    sendNotification(userid,user.getUsername(),msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver, final String username,final String message) {
        DatabaseReference token=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=token.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token1=dataSnapshot.getValue(Token.class);
                    Data data=new Data(fUser.getUid(),username+": "+message,"New Message",userid,R.mipmap.ic_launcher);

                    Sender sender=new Sender(data,token1.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code()==200){
                                        if (response.body().success !=1){
                                            Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readMessage( String myid, String userid, String imageUrl){
        mChat=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Chat chat=snapshot1.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }

                }
                messageAdapter=new MessageAdapter(MessageActivity.this,mChat,imageUrl);
                send_recycler.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public  void status(String status){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String,Object> map=new HashMap<>();
        map.put("status",status);
        reference.updateChildren(map);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener) ;
        status("offline");
    }
}