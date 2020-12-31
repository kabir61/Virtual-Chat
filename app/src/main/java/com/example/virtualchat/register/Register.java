package com.example.virtualchat.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.virtualchat.MainActivity;
import com.example.virtualchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText userNameET,emailET,passwordET;
    private MaterialButton registerBtn;
    MaterialToolbar toolbar;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username=userNameET.getText().toString();
                String txt_email=emailET.getText().toString();
                String txt_pass=passwordET.getText().toString();

                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_username)){
                    Toast.makeText(Register.this, "All field are empty", Toast.LENGTH_SHORT).show();
                }else  if (txt_pass.length()>6){
                    Toast.makeText(Register.this, "Must be minimum 6 character ", Toast.LENGTH_SHORT).show();
                }else {
                    register(txt_username,txt_email,txt_pass);
                }
            }
        });
    }

    private void register(String username,String email,String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId=firebaseUser.getUid();

                            reference= FirebaseDatabase.getInstance().getReference("Users").child(userId);


                            HashMap<String, String> hashMap=new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("status","offline");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent=new Intent(Register.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(Register.this, "you can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void init() {
        toolbar=findViewById(R.id.toolbarRegister);
        userNameET=findViewById(R.id.userNameET);
        emailET=findViewById(R.id.emailET);
        passwordET=findViewById(R.id.passwordET);
        registerBtn=findViewById(R.id.registerBtn);
        auth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();

    }
}