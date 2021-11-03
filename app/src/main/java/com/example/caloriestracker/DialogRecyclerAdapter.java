package com.example.caloriestracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder> {
    ArrayList<String> list;
    Context context;

    public DialogRecyclerAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.dialog_recycler_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DialogRecyclerAdapter.ViewHolder holder, int position) {
        holder.dialog_food_name.setText(list.get(position));
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dialog_food_name;
        CheckBox dialog_check_box;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dialog_check_box = itemView.findViewById(R.id.dialog_check_box);
            dialog_food_name = itemView.findViewById(R.id.dialog_food_name);
        }
    }
}
