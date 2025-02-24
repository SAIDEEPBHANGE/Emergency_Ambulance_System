package com.project.ambulancecall.driver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.ambulancecall.R;

import java.util.HashMap;

public class RequestAdapter extends FirebaseRecyclerAdapter<RequestData, RequestAdapter.RequestAdapterViewHolder>{

    private Context context;
    String dname,dnumber,anumber;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    DriverData userData;

    public RequestAdapter(@NonNull FirebaseRecyclerOptions<RequestData> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestAdapterViewHolder holder, int position, @NonNull RequestData model) {
        holder.username.setText(model.getUserName().toString());
        holder.usernumber.setText(model.getUserNumber().toString());
        holder.destination.setText(model.getDestination().toString());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(model.getKey());
                Toast.makeText(context, "Accept Clicked..!"+dname, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public RequestAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_requestcard, parent, false);

        context = parent.getContext();

        return new RequestAdapterViewHolder(view);
    }

    class RequestAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView username,usernumber,destination;
        ImageButton accept,reject;
        public RequestAdapterViewHolder(View itemview){
            super(itemview);

            username = itemview.findViewById(R.id.username);
            usernumber = itemview.findViewById(R.id.usernumber);
            destination = itemview.findViewById(R.id.destination);
            accept = itemview.findViewById(R.id.accept);
            reject = itemview.findViewById(R.id.reject);

        }
    }

    void getDriverInformation(){
        mAuth = FirebaseAuth.getInstance();
        userData = new DriverData();
        Query query2 = FirebaseDatabase.getInstance().getReference().child("Driver").orderByChild("FullName").equalTo(mAuth.getCurrentUser().getDisplayName());
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    userData = ds.getValue(DriverData.class);
                }
                dname = userData.getFullName();
                dnumber = userData.getMobileNumber();
                anumber = userData.getAmbulanceNumber();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void showCustomDialog(String key) {
        getDriverInformation();
        final Dialog dialog = new Dialog(context);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.custom_dialog_addamount);

        //Initializing the views of the dialog.
        final EditText amount = dialog.findViewById(R.id.addamount);
        Button submitButton = dialog.findViewById(R.id.update);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fairamount = amount.getText().toString();
                dbRef = FirebaseDatabase.getInstance().getReference().child("Requests");
                dbRef.child(key).child("Ammount").setValue(fairamount);
                dbRef.child(key).child("DriverName").setValue(dname);
                dbRef.child(key).child("DriverNumber").setValue(dnumber);
                dbRef.child(key).child("AmbulanceNumber").setValue(anumber);
                dbRef.child(key).child("Status").setValue("Accepted");
                dialog.dismiss();
                Intent intent = new Intent(context,DriverRide.class);
                intent.putExtra("Key",key);
                context.startActivity(intent);
            }
        });

        dialog.show();
    }
}
