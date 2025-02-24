package com.project.ambulancecall.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project.ambulancecall.HistoryAdapter;
import com.project.ambulancecall.R;

public class DriverHistory extends AppCompatActivity {
    FirebaseAuth mAuth;
    RecyclerView recyclerview;
    DatabaseReference databaseReference;
    private HistoryAdapter requestAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_history);

        mAuth = FirebaseAuth.getInstance();
        recyclerview = findViewById(R.id.recyclerview);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        Query query = databaseReference.child("Requests").orderByChild("DriverName").equalTo(mAuth.getCurrentUser().getDisplayName().toString());
        FirebaseRecyclerOptions<RequestData> options =
                new FirebaseRecyclerOptions.Builder<RequestData>()
                        .setQuery(query, RequestData.class)
                        .build();

        requestAdapter = new HistoryAdapter(options);
        recyclerview.setAdapter(requestAdapter);
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
}