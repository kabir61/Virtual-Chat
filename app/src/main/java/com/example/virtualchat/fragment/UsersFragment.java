package com.example.virtualchat.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.virtualchat.R;
import com.example.virtualchat.adapter.UserAdapter;
import com.example.virtualchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> mUser;
    EditText user_search;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView=view.findViewById(R.id.user_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUser=new ArrayList<>();
        readUser();
        user_search=view.findViewById(R.id.user_search);
        user_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });

        return view;
    }

    private void filter(String text) {

            ArrayList<User> filterList = new ArrayList<>();
            for (User item: mUser){
                if (item.getUsername().toLowerCase().contains(text.toLowerCase())){
                    filterList.add(item);
                }
            }

            adapter.filteredList(filterList);
    }

  /*  private void searchUser(String s) {
        final  FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        Query query =FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (user_search.getText().toString().equals("")){

                    mUser.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        User user=dataSnapshot.getValue(User.class);

                        if (!user.getId().equals(fuser.getUid())){
                            mUser.add(user);
                        }

                    }

                    adapter=new UserAdapter(getContext(),mUser,true);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void readUser(){

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())){
                        mUser.add(user);
                    }
                }
                adapter=new UserAdapter(getContext(),mUser,false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}