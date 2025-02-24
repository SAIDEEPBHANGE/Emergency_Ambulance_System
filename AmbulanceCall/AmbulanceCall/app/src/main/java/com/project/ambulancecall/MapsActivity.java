package com.project.ambulancecall;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationRequest;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.project.ambulancecall.databinding.ActivityMapsBinding;
import com.project.ambulancecall.driver.DriverHomePage;
import com.project.ambulancecall.user.MainActivity;
import com.project.ambulancecall.user.Profile;
import com.project.ambulancecall.user.UserData;
import com.project.ambulancecall.user.UserHistory;
import com.project.ambulancecall.user.UserRide;

import java.io.Console;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference reference,mRef;
    TextInputEditText destination,type;
    MaterialButton book;
    MaterialCheckBox checkBox;
    UserData userData;
    public boolean bool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("Requests");
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();

        destination = findViewById(R.id.destination);
        checkBox = findViewById(R.id.checkbox);
        book = findViewById(R.id.book);
        type = findViewById(R.id.type);

        bool = false;
        userData = new UserData();

        Query query = mRef.child("Users").orderByChild("FullName").equalTo(mAuth.getCurrentUser().getDisplayName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    userData = ds.getValue(UserData.class);
                }
                if(!snapshot.exists()){
                    Toast.makeText(MapsActivity.this, "Your not a User..!", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("You are not a User")
                            .setMessage("Please Exit the App")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    Intent intent = new Intent(MapsActivity.this, SelectionScreen.class);
                                    startActivity(intent);
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.checkbox:
                        if(checkBox.isChecked()){
                            destination.setVisibility(View.INVISIBLE);
                        }
                        else{
                            destination.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MapsActivity.this,type);
                popupMenu.getMenuInflater().inflate(R.menu.ambulancetype, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        type.setText(menuItem.getTitle());
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(type.getText().toString())){
                    Toast.makeText(MapsActivity.this, "Please Select Type of Ambulance..!", Toast.LENGTH_SHORT).show();
                }
                else{
                    String d;
                    if(!checkBox.isChecked()){
                        d = destination.getText().toString();
                        destination.setText("");
                    }
                    else{
                        d = "NearByHospital";
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy '||' HH:mm:ss z");

                    String currentDateAndTime = sdf.format(new Date());
                    String key = reference.push().getKey();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("UserName",userData.getFullName().toString());
                    map.put("UserNumber",userData.getMobileNumber().toString());
                    map.put("Source_Lon", String.valueOf(currentLocation.getLongitude()));
                    map.put("Source_Lat", String.valueOf(currentLocation.getLatitude()));
                    map.put("Destination",d);
                    map.put("AmbulanceType",type.getText().toString());
                    map.put("DateTime",currentDateAndTime);
                    map.put("Status","Pending");
                    map.put("DriverName"," ");
                    map.put("DriverNumber"," ");
                    map.put("AmbulanceNumber"," ");
                    map.put("Key",key);

                    reference.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendNotification();
                                Toast.makeText(MapsActivity.this, "Request Sent..Waiting for Driver...!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MapsActivity.this, UserRide.class);
                                intent.putExtra("Key",key);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendNotification() {
        new FCMSender().send(String.format(NotificationMessage.message,"messaging", "There is an Emergency Please check..!", "New Alert"), new Callback(){
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.code()==200){
                            Toast.makeText(MapsActivity.this, "Notification sent", Toast.LENGTH_SHORT).show();
                            //hideKeyboard(MainActivity.this);
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("IMP", "onFailure: "+e.getMessage());
            }
        });
    }

    public void isDriverAccepted(String key){
        reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dname = snapshot.child("DriverName").getValue(String.class);
                if(!dname.equals(" ")){

                    bool = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuitem,menu);
        final Switch sw = (Switch) menu.findItem(R.id.switch_action_bar).getActionView().findViewById(R.id.switch2);
        sw.setVisibility(View.INVISIBLE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(this, SelectionScreen.class);
                startActivity(intent);
                break;

            case R.id.profile:
                Intent intentprofile = new Intent(this, Profile.class);
                startActivity(intentprofile);
                break;

            case R.id.history:
                Intent intenthistory = new Intent(this, UserHistory.class);
                startActivity(intenthistory);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Current Location..!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        fetchLocation();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SelectionScreen.class);
        startActivity(intent);
        super.onBackPressed();
    }
}