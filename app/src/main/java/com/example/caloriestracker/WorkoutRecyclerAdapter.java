package com.example.caloriestracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutRecyclerAdapter extends RecyclerView.Adapter<WorkoutRecyclerAdapter.ViewHolder> {
    ArrayList<Workout> list;
    Context context;

    public WorkoutRecyclerAdapter(ArrayList<Workout> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.workout_recycler_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.step_desc.setText(list.get(position).getStepDesc());
        holder.step_name.setText(list.get(position).getStepName());
        holder.step_time.setText("-> " + list.get(position).getTime() + " min");
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
        TextView step_name,step_desc,step_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            step_name = itemView.findViewById(R.id.step_name);
            step_desc = itemView.findViewById(R.id.step_desc);
            step_time = itemView.findViewById(R.id.step_time);
        }
    }
}
