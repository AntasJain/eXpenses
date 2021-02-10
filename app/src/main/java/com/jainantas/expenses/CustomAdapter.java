package com.jainantas.expenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private List<MoneyDetails> moneyDetails;

    public CustomAdapter(List<MoneyDetails> moneyDetails) {
        this.moneyDetails=moneyDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display,parent,false);
       return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MoneyDetails money= moneyDetails.get(position);
        holder.mMessage.setText(money.getDetail());
        holder.mDate.setText(money.getDate());
        holder.mAmount.setText(money.getPrice());
        holder.mName.setText(money.getName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mMessage, mAmount, mDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mAmount = (TextView) itemView.findViewById(R.id.amount);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mMessage = (TextView) itemView.findViewById(R.id.msg);
            mName = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
