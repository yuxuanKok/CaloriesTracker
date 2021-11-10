package com.example.caloriestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WorkoutStep extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID,collection, doc;
    private int intensity, type, remaining, time;
    private double BMR,totalBurn;
    private TextView step_title;
    private RecyclerView workoutRecyclerView;
    private WorkoutRecyclerAdapter workoutRecyclerAdapter;
    private FloatingActionButton workout_done;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<Workout> workoutList = new ArrayList<>();
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_step);

        //step_description=findViewById(R.id.step_description);
        step_title=findViewById(R.id.step_title);
        workoutRecyclerView=findViewById(R.id.workout_recycler);
        workout_done = findViewById(R.id.workout_done);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            intensity = extras.getInt("intensity");
            type = extras.getInt("type");
            remaining=extras.getInt("remainingCal");
        }

        //to get user bmi and calories burn and calories consumed
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        fStore.collection("users").document(userID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int weight = documentSnapshot.getLong("weight").intValue();
                int height = documentSnapshot.getLong("height").intValue();
                int age = documentSnapshot.getLong("age").intValue();
                BMR = 665.1+(9.563*weight)+(1.85*height)-(4.676*age);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WorkoutStep.this,"Fail to get data",Toast.LENGTH_SHORT).show();
            }
        });

        //get workout plan
        switch (intensity){
            case 0:
                collection="light";
                break;
            case 1:
                collection="moderate";
                break;
            case 2:
                collection="vigorous";
                break;
            default:
                break;
        }
        switch (type){
            case 0:
                doc="strength";
                break;
            case 1:
                doc="aerobic";
                break;
            case 2:
                doc="flexibility";
                break;
            case 3:
                doc="balance";
                break;
            default:
                break;
        }

        step_title.setText(collection +" "+doc);
        fStore.collection(collection)
                .document(doc).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            Map<String, Object> map = document.getData();
                            if (map != null) {
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    list.add(entry.getValue().toString());
                                }
                            }

                            //based on the calories remaining decides the time
                            if(remaining>=75){//75-100
                                time = 15;
                            }
                            else if(remaining>=50 && remaining<75){//50-74
                                time = 30;
                            }
                            else if(remaining>=25 && remaining<50){
                                time = 45;
                            }
                            else if(remaining<24){
                                time = 60;
                            }

                            for(String item: list){
                                String[] elements = item.split(",");
                                String name = elements[0].replaceAll("[^a-zA-Z0-9]", " ");
                                double met = Double.parseDouble(elements[1]);
                                String description =  elements[2].replace("\\n","\n");
                                Workout workout = new Workout(met,name,description.replace("]", ""),time);
                                workoutList.add(workout);
                            }
                            workoutRecyclerView.setLayoutManager(new LinearLayoutManager(WorkoutStep.this));
                            workoutRecyclerAdapter = new WorkoutRecyclerAdapter(workoutList,WorkoutStep.this);
                            workoutRecyclerView.setAdapter(workoutRecyclerAdapter);
                        }

                       }
                   });


        workout_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalBurn= 0;
                for(Workout x: workoutList){
                    totalBurn += BMR * x.getMET()/24.0 * (x.getTime()/60.0);
                }
                DocumentReference workoutRef = fStore
                        .collection("users").document(fAuth.getCurrentUser().getUid())
                        .collection("workout").document(date);

                workoutRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                totalBurn+=documentSnapshot.getDouble("burn");
                                Map<String,Object> workout = new HashMap<>();
                                workout.put("burn",totalBurn);
                                workoutRef.set(workout).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(WorkoutStep.this,"Workout updated",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                Map<String,Object> workout = new HashMap<>();
                                workout.put("burn",totalBurn);
                                workoutRef.set(workout).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(WorkoutStep.this,"Workout updated",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
                startActivity(new Intent(WorkoutStep.this,MainActivity.class));

            }
        });
    }

}