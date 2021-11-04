package com.example.caloriestracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class FoodDetails extends AppCompatActivity {

    private RecyclerView details_recycler_view;
    private Button details_submit;
    DetailsRecyclerAdapter detailsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        details_submit = findViewById(R.id.details_submit);
        details_recycler_view = findViewById(R.id.details_recycler_view);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<String> object = (ArrayList<String>) args.getSerializable("ARRAYLIST");

//        details_recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        detailsRecyclerAdapter = new DetailsRecyclerAdapter(object, FoodDetails.this);
//        details_recycler_view.setAdapter(detailsRecyclerAdapter);

        details_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}