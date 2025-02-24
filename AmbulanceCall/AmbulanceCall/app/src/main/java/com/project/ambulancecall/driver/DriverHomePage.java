package com.project.ambulancecall.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.project.ambulancecall.MapsActivity;
import com.project.ambulancecall.R;
import com.project.ambulancecall.SelectionScreen;
import com.project.ambulancecall.user.MainActivity;
import com.project.ambulancecall.user.UserHistory;

public class DriverHomePage extends AppCompatActivity {
    FirebaseAuth mAuth;
    RecyclerView recyclerview;
    DatabaseReference databaseReference;
    private RequestAdapter requestAdapter;
    DriverData userData;
    String name,number,carnumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home_page);
        mAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().subscribeToTopic("messaging");
        recyclerview = findViewById(R.id.recyclerview);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        userData = new DriverData();

        Query query2 = databaseReference.child("Driver").orderByChild("FullName").equalTo(mAuth.getCurrentUser().getDisplayName());
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    userData = ds.getValue(DriverData.class);
                }
                if(!snapshot.exists()){
                    Toast.makeText(DriverHomePage.this, "Your not a Driver..!", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(DriverHomePage.this)
                            .setTitle("You are not a Driver")
                            .setMessage("Please Exit the App")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   finish();
                                   Intent intent = new Intent(DriverHomePage.this, SelectionScreen.class);
                                   startActivity(intent);
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                name = userData.getFullName();
                number = userData.getMobileNumber();
                carnumber = userData.getAmbulanceNumber();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
       requestAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        requestAdapter.stopListening();
    }

    void getData(){
        Query query = databaseReference.child("Requests").orderByChild("Status").equalTo("Pending");
        FirebaseRecyclerOptions<RequestData> options =
                new FirebaseRecyclerOptions.Builder<RequestData>()
                        .setQuery(query, RequestData.class)
                        .build();

        requestAdapter = new RequestAdapter(options,this);
        recyclerview.setAdapter(requestAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuitem,menu);
        final Switch sw = (Switch) menu.findItem(R.id.switch_action_bar).getActionView().findViewById(R.id.switch2);
        sw.setChecked(true);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(DriverHomePage.this, "You are now working", Toast.LENGTH_SHORT).show();
                    getData();
                    FirebaseMessaging.getInstance().subscribeToTopic("messaging");
                    requestAdapter.startListening();
                }
                else{
                    Toast.makeText(DriverHomePage.this, "You are Not Working", Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("messaging");
                    requestAdapter.stopListening();
                    recyclerview.setAdapter(null);
                }
            }
        });
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                FirebaseMessaging.getInstance().unsubscribeFromTopic("messaging");
                Intent intent = new Intent(this, SelectionScreen.class);
                startActivity(intent);
                break;

            case R.id.profile:
                Intent intentprofile = new Intent(this, DriverProfile.class);
                startActivity(intentprofile);
                break;

            case R.id.history:
                Intent intenthistory = new Intent(this, DriverHistory.class);
                startActivity(intenthistory);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SelectionScreen.class);
        startActivity(intent);
        super.onBackPressed();
    }
}