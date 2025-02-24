package com.project.ambulancecall.driver;

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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.ambulancecall.R;
import com.project.ambulancecall.user.MainActivity;
import com.project.ambulancecall.user.RegisterActivity;

import java.util.HashMap;

public class RegisterDriver extends AppCompatActivity {
    MaterialButton btnRegister;
    TextInputEditText fullname,email,number,password,confirmpassword,ambulancenumber,aadharnumber;
    TextView txtSignIn;
    FirebaseAuth auth;
    DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);
        btnRegister = findViewById(R.id.btnRegister);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        txtSignIn = findViewById(R.id.txtSignIn);
        ambulancenumber = findViewById(R.id.ambulancenumber);
        aadharnumber = findViewById(R.id.aadharnumber);

        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Driver");

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterDriver.this, LoginDriver.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String f_email = email.getText().toString();
                String f_password = password.getText().toString();
                //Toast.makeText(RegisterActivity.this, "name:"+f_email+"pass:"+f_password, Toast.LENGTH_SHORT).show();
                if(TextUtils.isEmpty(fullname.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(ambulancenumber.getText().toString()) || TextUtils.isEmpty(number.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(confirmpassword.getText().toString())){
                    Toast.makeText(RegisterDriver.this, "Please Enter All Details..!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!confirmpassword.getText().toString().equals(password.getText().toString())){
                        Toast.makeText(RegisterDriver.this, "Password and Confirm Password Must be Same", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String carnumberformat = "^[A-Z]{2}\\s[0-9]{2}\\s[A-Z]{2}\\s[0-9]{4}$";
                        String carnumber =  ambulancenumber.getText().toString().trim();
                        if(number.getText().toString().length()!=10 || aadharnumber.getText().toString().length()!=12 || !carnumber.matches(carnumberformat)){
                            Toast.makeText(RegisterDriver.this, "Phone Number Must be of 10 Digit or Aadhar Number must be 12 Digit or Check the Car Number..!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            auth.createUserWithEmailAndPassword(f_email,f_password).addOnCompleteListener(RegisterDriver.this,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        String key = dbRef.push().getKey();
                                        HashMap<String,String> map = new HashMap<>();
                                        map.put("FullName",fullname.getText().toString());
                                        map.put("Email",email.getText().toString());
                                        map.put("MobileNumber",number.getText().toString());
                                        map.put("AmbulanceNumber",ambulancenumber.getText().toString());
                                        map.put("AadharNumber",aadharnumber.getText().toString());
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
                                                    Toast.makeText(RegisterDriver.this, "Driver Registered..!", Toast.LENGTH_SHORT).show();
                                                    fullname.setText("");
                                                    email.setText("");
                                                    number.setText("");
                                                    password.setText("");
                                                    ambulancenumber.setText("");
                                                    aadharnumber.setText("");
                                                    confirmpassword.setText("");
                                                }
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterDriver.this, "Error:"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}