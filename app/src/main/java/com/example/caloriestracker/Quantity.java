package com.example.caloriestracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class Quantity extends AppCompatActivity {

    private RecyclerView quantity_recycler_view;
    private Button quantity_confirm;
    private QuantityRecyclerAdapter quantityRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quantity);

        quantity_confirm = findViewById(R.id.quantity_confirm);
        quantity_recycler_view = findViewById(R.id.quantity_recycler_view);

        Intent intent = getIntent();
        //Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Food> foodList =  (ArrayList<Food>) getIntent().getSerializableExtra("foodArray");

        quantity_recycler_view.setLayoutManager(new LinearLayoutManager(Quantity.this));
        quantityRecyclerAdapter = new QuantityRecyclerAdapter(foodList,Quantity.this);
        quantity_recycler_view.setAdapter(quantityRecyclerAdapter);

        quantity_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}