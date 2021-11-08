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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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

                for(Food item : array){
//                    long cc = System.currentTimeMillis();
                    //item.setDateTime(cc);
                    DocumentReference nutrition = fStore.collection("nutrition").document(item.getFoodName());
                    nutrition.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            item.setHealthy(documentSnapshot.getBoolean("healthy"));
//                            item.setTotalCal(documentSnapshot.getLong("cal").intValue()*item.getQty());
                            DocumentReference foodRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                                    .collection("food").document();
                            Map<String,Object> docData = new HashMap<>();
                            docData.put("dateTime", FieldValue.serverTimestamp());
                            docData.put("foodName", item.getFoodName());
                            docData.put("totalCal",documentSnapshot.getLong("cal").intValue()*item.getQty());
                            docData.put("qty",item.getQty());
                            docData.put("healthy",documentSnapshot.getBoolean("healthy"));

                            Log.d("foodname",item.getFoodName());
                            foodRef.set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Quantity.this,"Successful upload food",Toast.LENGTH_SHORT).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Quantity.this,"Unsuccessful upload food",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Quantity.this, "Fail to get data: "+e,Toast.LENGTH_SHORT).show();
                                }
                            });

                }



//                for(Food i: array){
//
//                }

//                DocumentReference foodRef = fStore
//                        .collection("users").document(fAuth.getCurrentUser().getUid())
//                        .collection("food").document(date);
//
//                Map<String, Food> docData = new HashMap<>();
//                for(Food i : array){
//                    docData.put(i.getDateTime(), i);
//                }
//
//                foodRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            DocumentSnapshot document = task.getResult();
//                            if(document.exists()){
//                                foodRef.set(docData,SetOptions.merge())
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Toast.makeText(Quantity.this,"Successful upload food",Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(Quantity.this,"Unsuccessful upload food",Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                Log.d("qqqq","update");
//                            }
//                            else{
//                                foodRef.set(docData)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Toast.makeText(Quantity.this,"Successful upload food",Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(Quantity.this,"Unsuccessful upload food",Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                Log.d("qqqq","set");
//                            }
//                        }
//                    }
//                });

                startActivity(new Intent(Quantity.this,MainActivity.class));
            }
        });
    }
}