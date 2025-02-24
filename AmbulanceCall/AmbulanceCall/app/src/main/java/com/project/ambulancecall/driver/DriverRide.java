package com.project.ambulancecall.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulancecall.R;

import java.util.Locale;

public class DriverRide extends AppCompatActivity {

    MaterialTextView cname,cnumber,destination;
    MaterialButton sourcedirection,completeride;
    String key;
    DatabaseReference reference;
    RequestData requestData;
    String lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride);

        Intent intent  = getIntent();
        key = intent.getStringExtra("Key");
        cname = findViewById(R.id.cname);
        cnumber = findViewById(R.id.cnumber);
        destination = findViewById(R.id.destination);
        sourcedirection = findViewById(R.id.sourcedirection);
        completeride = findViewById(R.id.completeride);

        requestData = new RequestData();

        reference = FirebaseDatabase.getInstance().getReference().child("Requests").child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestData = snapshot.getValue(RequestData.class);
                if(requestData.getStatus().equals("Cancled")){
                    Toast.makeText(DriverRide.this, "User Canceled the Ride Request..!", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(DriverRide.this,DriverHomePage.class);
                    startActivity(intent1);
                }
                cname.setText(requestData.getUserName());
                cnumber.setText(requestData.getUserNumber());
                destination.setText(requestData.getDestination());
                lat = requestData.getSource_Lat();
                lon = requestData.getSource_Lon();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+cnumber.getText()));//change the number
                startActivity(callIntent);
            }
        });

        sourcedirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", Float.parseFloat(lat), Float.parseFloat(lon), "User is Here");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        completeride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Status").setValue("Complete");
                Intent intent1 = new Intent(DriverRide.this,DriverHomePage.class);
                startActivity(intent1);
            }
        });
    }
}