package com.project.ambulancecall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.project.ambulancecall.driver.RequestAdapter;
import com.project.ambulancecall.driver.RequestData;

public class HistoryAdapter extends FirebaseRecyclerAdapter<RequestData, HistoryAdapter.HistoryAdapterViewHolder> {

    public HistoryAdapter(@NonNull FirebaseRecyclerOptions<RequestData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HistoryAdapterViewHolder holder, int position, @NonNull RequestData model) {
        holder.username.setText(model.getUserName());
        holder.usernumber.setText(model.getUserNumber());
        holder.drivername.setText(model.getDriverName());
        holder.drivernumber.setText(model.getDriverNumber());
        holder.status.setText(model.getStatus());
        holder.ammount.setText(model.getAmmount());
        holder.datetime.setText(model.getDateTime());
    }

    @NonNull
    @Override
    public HistoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_history, parent, false);
        return new HistoryAdapterViewHolder(view);
    }

    class HistoryAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView username,usernumber,drivername,drivernumber,status,ammount,datetime;
        public HistoryAdapterViewHolder(View itemview){
            super(itemview);

            username = itemview.findViewById(R.id.username);
            usernumber = itemview.findViewById(R.id.usernumber);
            drivername = itemview.findViewById(R.id.drivername);
            drivernumber = itemview.findViewById(R.id.drivernumber);
            status = itemview.findViewById(R.id.status);
            ammount = itemview.findViewById(R.id.ammount);
            datetime = itemview.findViewById(R.id.datetime);

        }
    }
}
