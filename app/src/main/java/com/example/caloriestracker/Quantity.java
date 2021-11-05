package com.example.caloriestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Quantity extends AppCompatActivity {

    private RecyclerView quantity_recycler_view;
    private Button quantity_confirm;
    private QuantityRecyclerAdapter quantityRecyclerAdapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quantity);

        quantity_confirm = findViewById(R.id.quantity_confirm);
        quantity_recycler_view = findViewById(R.id.quantity_recycler_view);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Food> foodList = (ArrayList<Food>) args.getSerializable("ARRAYLIST");

        quantity_recycler_view.setLayoutManager(new LinearLayoutManager(Quantity.this));
        quantityRecyclerAdapter = new QuantityRecyclerAdapter(foodList,Quantity.this);
        quantity_recycler_view.setAdapter(quantityRecyclerAdapter);

        quantity_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Food> array = quantityRecyclerAdapter.getList();

                DocumentReference foodRef = fStore
                        .collection("users").document(fAuth.getCurrentUser().getUid())
                        .collection("food").document(date);

                for(Food item: array){
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("users").document(fAuth.getCurrentUser().getUid())
                            .collection("food").document(date)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            long c = System.currentTimeMillis();
                                            Map<String, Object> docData = new HashMap<>();
                                            docData.put(Long.toString(c), Arrays.asList(item.getFoodName(),item.getQty()));

                                            foodRef.update(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(Quantity.this,"Successful upload food",Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Quantity.this,"Unsuccessful upload food "+e,Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            Log.d("qqqq","update");
                                        } else {
                                            long c = System.currentTimeMillis();
                                            Map<String, Object> docData = new HashMap<>();
                                            docData.put(Long.toString(c), Arrays.asList(item.getFoodName(),item.getQty()));

                                            foodRef.set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(Quantity.this,"Successful upload food",Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Quantity.this,"Unsuccessful upload food "+e,Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            Log.d("qqqq","set");
                                        }
                                    } else {
                                        Toast.makeText(Quantity.this, "Failed with: "+ task.getException(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

//                for(Food item : array){
//                    fStore.collection("nutrition").document(item.getFoodName())
//                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(MainActivity.this,"Fail to get nutrition",Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                    }
            }
        });
    }
}