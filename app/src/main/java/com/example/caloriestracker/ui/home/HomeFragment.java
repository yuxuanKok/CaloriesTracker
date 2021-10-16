package com.example.caloriestracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriestracker.MainActivity;
import com.example.caloriestracker.UserProfile;
import com.example.caloriestracker.WorkoutPlan;
import com.example.caloriestracker.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

public class HomeFragment extends Fragment {

    //private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private ProgressBar progressBar;
    private TextView home_budget, home_cal_consumed,home_cal_burn,home_remaining,home_date,home_bmi, home_range;
    private Button home_workout;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    String time_array[]={};
    int weight = 0,height = 0, age = 0, consumed = 0, burn = 0, active = 0;
    double bmi, budget, bmr;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = binding.progressBar;
        home_budget = binding.homeBudget;
        home_date = binding.homeDate;
        home_cal_burn = binding.homeCalBurn;
        home_cal_consumed = binding.homeCalConsumed;
        home_remaining = binding.homeRemaining;
        recyclerView = binding.recyclerView;
        home_range=binding.homeRange;
        home_bmi=binding.homeBmi;
        home_workout=binding.homeWorkout;

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new RecyclerAdapter(time_array,getActivity());
        recyclerView.setAdapter(recyclerAdapter);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy.MM.dd");
        String currentDateandTime = sdf.format(new Date());
        home_date.setText(currentDateandTime);

        userID = fAuth.getCurrentUser().getUid();
        fStore.collection("users").document(userID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        weight = documentSnapshot.getLong("weight").intValue();
                        height = documentSnapshot.getLong("height").intValue();
                        age = documentSnapshot.getLong("age").intValue();
                        active = documentSnapshot.getLong("active").intValue();
                        //consumed = documentSnapshot.getLong("cal_consumed").intValue();
                        //burn = documentSnapshot.getLong("cal_burn").intValue();

                        home_cal_burn.setText(String.valueOf(burn));
                        home_cal_consumed.setText(String.valueOf(consumed));

                        //bmi
                        bmi = weight/(height*height/10000.0);
                        home_bmi.setText(String.format ("%.2f", bmi));

                        //range
                        if(bmi<18.5){
                            home_range.setText("Underweight");
                        }
                        else if(bmi>=18.5 && bmi<25){
                            home_range.setText("healthy weight");
                        }
                        else if(bmi>=25.0 && bmi<30){
                            home_range.setText("overweight ");
                        }
                        else if (bmi>=30.0){
                            home_range.setText("obesity ");
                        }

                        //budget
                        if(documentSnapshot.getLong("gender").intValue()==0){
                            bmr = 665.1+(9.563*weight)+(1.85*height)-(4.676*age);
                        }
                        else{
                            bmr = 66.47+(13.75*weight)+(5*height)-(6.75*age);
                        }
                        switch (active){
                            case 0:
                              budget = bmr * 1.2;
                              break;
                            case 1:
                                budget = bmr * 1.375;
                                break;
                            case 2:
                                budget = bmr * 1.55;
                                break;
                            case 3:
                                budget = bmr * 1.725;
                                break;
                            case 4:
                                budget = bmr * 1.9;
                                break;
                        }
                        home_budget.setText(String.valueOf(Math.round(budget)));

                        int remaining = (int) Math.round(((budget-consumed)+burn)/budget*100);
                        progressBar.setProgress(remaining);
                        home_remaining.setText(remaining+"%");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Fail to get data",Toast.LENGTH_SHORT).show();
                    }
                });

//        DocumentReference documentReference = fStore.collection("users").document(userID);
//        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                Double budget;
//                weight = value.getLong("weight").intValue();
//                height = value.getLong("height").intValue();
//                age = value.getLong("age").intValue();
//                consumed = value.getLong("cal_consumed").intValue();
//                burn = value.getLong("cal_burn").intValue();
//
//                home_cal_burn.setText(String.valueOf(burn));
//                home_cal_consumed.setText(String.valueOf(consumed));
//                //budget
//                if(value.getLong("gender").intValue()==0){
//                    budget = 665.1+(9.563*weight)+(1.85*height)-(4.676*age);
//                }
//                else{
//                    budget = 66.47+(13.75*weight)+(5*height)-(6.75*age);
//                }
//                home_budget.setText(String.valueOf(Math.round(budget)));
//
//                int remaining = (int) Math.round(((budget-consumed)+burn)/budget*100);
//                progressBar.setProgress(remaining);
//                home_remaining.setText(remaining+"%");
//            }
//        });


//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        home_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkoutPlan.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}