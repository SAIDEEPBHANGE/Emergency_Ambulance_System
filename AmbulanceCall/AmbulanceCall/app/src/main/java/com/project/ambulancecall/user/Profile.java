package com.project.ambulancecall.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulancecall.R;
import com.project.ambulancecall.driver.DriverProfile;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    EditText fullname,number,aadhar;
    MaterialTextView email;
    FirebaseAuth auth;
    DatabaseReference reference;
    UserData userData;
    MaterialButton update;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        aadhar = findViewById(R.id.aadhar);
        update = findViewById(R.id.update);

        userData = new UserData();

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Users").orderByChild("FullName").equalTo(auth.getCurrentUser().getDisplayName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    userData = ds.getValue(UserData.class);
                }
                fullname.setText(userData.getFullName());
                email.setText(userData.getEmail());
                number.setText(userData.getMobileNumber());
                aadhar.setText(userData.getAadharNumber());
                id = userData.getKey();
                //Toast.makeText(Profile.this, "Id"+id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Profile.this, "Id:"+id, Toast.LENGTH_SHORT).show();
                if(TextUtils.isEmpty(fullname.getText().toString()) ||TextUtils.isEmpty(fullname.getText().toString()) ||TextUtils.isEmpty(fullname.getText().toString())){
                    Toast.makeText(Profile.this, "Fields Cannot be Empty..!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(number.getText().toString().length()!=10 || aadhar.getText().toString().length()!=12){
                        Toast.makeText(Profile.this, "Phone Number Must be of 10 Digit or Aadhar Number must be 12 Digit", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("FullName",fullname.getText().toString());
                        map.put("MobileNumber",number.getText().toString());
                        map.put("AadharNumber",aadhar.getText().toString());
                        reference.child("Users").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Profile.this, "Data Updated..!", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = auth.getCurrentUser();
                                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(fullname.getText().toString()).build();
                                user.updateProfile(profile);
                            }
                        });
                    }
                }
            }
        });
    }
}