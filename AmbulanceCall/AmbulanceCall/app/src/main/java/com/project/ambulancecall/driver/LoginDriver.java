package com.project.ambulancecall.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project.ambulancecall.R;
import com.project.ambulancecall.user.MainActivity;
import com.project.ambulancecall.user.RegisterActivity;

public class LoginDriver extends AppCompatActivity {
    TextView txtSignUp;
    TextInputEditText email,pass;
    MaterialButton btnSignIn;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_driver);

        txtSignUp = findViewById(R.id.txtSignUp);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.btnSignIn);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(this,DriverHomePage.class);
            startActivity(intent);
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginDriver.this, "Driver LogIn Success..!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginDriver.this,DriverHomePage.class);
                            startActivity(intent);
                            email.setText("");
                            pass.setText("");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginDriver.this, "Error:"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginDriver.this, RegisterDriver.class);
                startActivity(intent);
                finish();
            }
        });

    }
}