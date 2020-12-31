package com.example.virtualchat.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.ETC1;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.virtualchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPassword extends AppCompatActivity {
    MaterialToolbar toolbar;
    private EditText send_email;
    private MaterialButton reset_btn;

    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reset_btn=findViewById(R.id.reset_btn);
        send_email=findViewById(R.id.send_email);

        fauth= FirebaseAuth.getInstance();

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=send_email.getText().toString();
                if (email.equals("")){
                    Toast.makeText(ResetPassword.this, "All filled are required", Toast.LENGTH_SHORT).show();
                }else {
                   fauth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPassword.this, "Please check your email", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ResetPassword.this,LogIn.class));
                                    } else {
                                       String error=task.getException().getMessage();
                                        Toast.makeText(ResetPassword.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });



    }
}