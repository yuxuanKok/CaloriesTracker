package com.example.caloriestracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuantityRecyclerAdapter extends RecyclerView.Adapter<QuantityRecyclerAdapter.ViewHolder> {
    ArrayList<Food> list;
    Context context;

    public QuantityRecyclerAdapter(ArrayList<Food> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.quantity_recycler_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuantityRecyclerAdapter.ViewHolder holder, int position) {
        holder.quantity_food_name.setText(list.get(position).getFoodName());
        List<Integer> spinnerItems = new ArrayList<>();
        for(int i = 1 ; i<31;i++){
            spinnerItems.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, spinnerItems);
        holder.quantity_qty.setAdapter(adapter);
        holder.quantity_qty.setSelection(list.get(position).getQty()-1);

    }

    @Override
    public int getItemCount() {
        if (list == null){
            return 0;
        }
        else{
            return  list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView quantity_food_name;
        Spinner quantity_qty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity_food_name = itemView.findViewById(R.id.quantity_food_name);
            quantity_qty = itemView.findViewById(R.id.quantity_qty);
        }
    }
}
