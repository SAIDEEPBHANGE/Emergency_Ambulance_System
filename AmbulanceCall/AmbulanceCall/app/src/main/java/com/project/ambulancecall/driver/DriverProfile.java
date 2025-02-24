package com.project.ambulancecall.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.project.ambulancecall.user.Profile;
import com.project.ambulancecall.user.UserData;

import java.util.HashMap;

public class DriverProfile extends AppCompatActivity {

    EditText fullname,number,carnumber,aadharnumber;
    MaterialTextView email;
    FirebaseAuth auth;
    DatabaseReference reference;
    DriverData userData;
    MaterialButton update;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        carnumber = findViewById(R.id.carnumber);
        aadharnumber = findViewById(R.id.aadharnumber);
        update = findViewById(R.id.update);

        userData = new DriverData();

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Driver").orderByChild("FullName").equalTo(auth.getCurrentUser().getDisplayName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    userData = ds.getValue(DriverData.class);
                }
                fullname.setText(userData.getFullName());
                email.setText(userData.getEmail());
                number.setText(userData.getMobileNumber());
                carnumber.setText(userData.getAmbulanceNumber());
                aadharnumber.setText(userData.getAadharNumber());
                id = userData.getKey();
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
                    Toast.makeText(DriverProfile.this, "Fields Cannot be Empty..!", Toast.LENGTH_SHORT).show();
                }
                else{
                    String carnumberformat = "^[A-Z]{2}\\s[0-9]{2}\\s[A-Z]{2}\\s[0-9]{4}$";
                    String abnumber =  carnumber.getText().toString().trim();
                    if(number.getText().toString().length()!=10 || aadharnumber.getText().toString().length()!=12 || !abnumber.matches(carnumberformat)){
                        Toast.makeText(DriverProfile.this, "Phone Number Must be of 10 Digit or Aadhar Number must be 12 Digit or Check the Car Number..!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("FullName",fullname.getText().toString());
                        map.put("MobileNumber",number.getText().toString());
                        map.put("AadharNumber",aadharnumber.getText().toString());
                        map.put("AmbulanceNumber",carnumber.getText().toString());
                        reference.child("Driver").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(DriverProfile.this, "Data Updated..!", Toast.LENGTH_SHORT).show();
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