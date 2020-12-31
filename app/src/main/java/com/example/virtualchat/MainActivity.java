package com.example.virtualchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.virtualchat.adapter.ViewPageAdapter;
import com.example.virtualchat.fragment.ChatFragment;
import com.example.virtualchat.fragment.ProfileFragment;
import com.example.virtualchat.fragment.UsersFragment;
import com.example.virtualchat.model.Chat;
import com.example.virtualchat.model.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    MaterialToolbar toolbar;


    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
       final ViewPager viewpager=findViewById(R.id.viewpager);
       final TabLayout tabLayout=findViewById(R.id.tab_layout);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user=snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ViewPageAdapter pageAdapter=new ViewPageAdapter(getSupportFragmentManager());
                int userid=0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.isIsseen()){
                        userid++;
                    }
                }
                if (userid==0){
                    pageAdapter.addFragment(new ChatFragment(),"Chats");
                }else {
                    pageAdapter.addFragment(new ChatFragment(),"("+userid+")Chats");
                }
                pageAdapter.addFragment(new UsersFragment(),"Users");
                pageAdapter.addFragment(new ProfileFragment(),"Profile");

                viewpager.setAdapter(pageAdapter);
                tabLayout.setupWithViewPager(viewpager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,Start.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }
        return false;
    }

    public  void status(String status){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
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
        status("offline");
    }
}