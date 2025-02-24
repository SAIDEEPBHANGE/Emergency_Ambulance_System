package com.project.ambulancecall.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.ambulancecall.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton btnRegister;
    TextInputEditText fullname,email,number,password,confirmpassword,aadhar;
    TextView txtSignIn;
    FirebaseAuth auth;
    DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        txtSignIn = findViewById(R.id.txtSignIn);
        aadhar = findViewById(R.id.aadharnumber);

        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String f_email = email.getText().toString();
                String f_password = password.getText().toString();
                //Toast.makeText(RegisterActivity.this, "name:"+f_email+"pass:"+f_password, Toast.LENGTH_SHORT).show();
                if(TextUtils.isEmpty(fullname.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(number.getText().toString()) || TextUtils.isEmpty(aadhar.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(confirmpassword.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Please Enter All Details..!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!confirmpassword.getText().toString().equals(password.getText().toString())){
                        Toast.makeText(RegisterActivity.this, "Password and Confirm Password Must be Same", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(number.getText().toString().length()!=10 || aadhar.getText().toString().length()!=12){
                            Toast.makeText(RegisterActivity.this, "Phone Number Must be of 10 Digit or Aadhar Number must be 12 Digit", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            auth.createUserWithEmailAndPassword(f_email,f_password).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        String key = dbRef.push().getKey();
                                        HashMap<String,String> map = new HashMap<>();
                                        map.put("FullName",fullname.getText().toString());
                                        map.put("Email",email.getText().toString());
                                        map.put("MobileNumber",number.getText().toString());
                                        map.put("AadharNumber",aadhar.getText().toString());
                                        map.put("Key",key);
                                        dbRef.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    FirebaseUser user = auth.getCurrentUser();
                                                    if(user!=null){
                                                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(fullname.getText().toString()).build();
                                                        user.updateProfile(profile);
                                                    }
                                                    Toast.makeText(RegisterActivity.this, "User Registered..!", Toast.LENGTH_SHORT).show();
                                                    fullname.setText("");
                                                    email.setText("");
                                                    number.setText("");
                                                    password.setText("");
                                                    aadhar.setText("");
                                                    confirmpassword.setText("");
                                                }
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "Error:"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}