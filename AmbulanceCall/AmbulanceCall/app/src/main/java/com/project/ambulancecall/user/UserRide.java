package com.project.ambulancecall.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulancecall.MapsActivity;
import com.project.ambulancecall.R;
import com.project.ambulancecall.SelectionScreen;
import com.project.ambulancecall.driver.DriverData;
import com.project.ambulancecall.driver.RequestData;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRide extends AppCompatActivity implements PaymentResultListener {
    MaterialTextView dname,dnumber,anumber,amount;
    DatabaseReference reference;
    String key;
    RequestData requestData;
    ProgressDialog dialog;
    MaterialButton cancel,payment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ride);

        requestData = new RequestData();
        Intent intent = getIntent();
        key = intent.getStringExtra("Key");
        dname = findViewById(R.id.dname);
        dnumber = findViewById(R.id.dnumber);
        anumber = findViewById(R.id.anumber);
        amount = findViewById(R.id.amount);
        cancel = findViewById(R.id.cancelride);
        payment = findViewById(R.id.payment);

        reference = FirebaseDatabase.getInstance().getReference().child("Requests");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).child("Status").setValue("Cancled");
                Intent intent1 = new Intent(UserRide.this,MapsActivity.class);
                startActivity(intent1);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checkout checkout =new Checkout();
                checkout.setKeyID("rzp_live_2a38270hZI8hk3");
                checkout.setImage(R.drawable.ambulance);

                JSONObject object = new JSONObject();
                try {
                    // to put name
                    object.put("name", dname.getText().toString());

                    // put description
                    object.put("description", "Payment for Your Fair");

                    // to set theme color
                    object.put("theme.color", "");

                    // put the currency
                    object.put("currency", "INR");

                    // put amount
                    object.put("amount", Math.round(Float.parseFloat(amount.getText().toString()) * 100));

                    // put mobile number
                    object.put("prefill.contact", dnumber.getText().toString());

                    // put email
                    //object.put("prefill.email", "chaitanyamunje@gmail.com");

                    // open razorpay to checkout activity
                    checkout.open(UserRide.this, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        dnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+dnumber.getText()));//change the number
                startActivity(callIntent);
            }
        });


        dialog = new ProgressDialog(UserRide.this);
        dialog.setMessage("Waiting for Driver...!");



        //Toast.makeText(this, "Key:"+key, Toast.LENGTH_SHORT).show();
        dialog.show();
        Query query = reference.child(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    requestData = snapshot.getValue(RequestData.class);
                    if(requestData.getStatus().equals("Complete")){
                        Toast.makeText(UserRide.this, "Your Ride is Completed..!", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(UserRide.this, SelectionScreen.class);
                        startActivity(intent1);
                    }
                if(requestData.getDriverName().equals(" ")){
                    dialog.show();
                }
                else{
                    dialog.dismiss();
                }
                   // Toast.makeText(UserRide.this, "Name:"+requestData.getDriverName().toString(), Toast.LENGTH_SHORT).show();
                dname.setText(requestData.getDriverName());
                dnumber.setText(requestData.getDriverNumber());
                anumber.setText(requestData.getAmbulanceNumber());
                amount.setText(requestData.getAmmount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        // this method is called on payment success.
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        // on payment failed.
        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }
}