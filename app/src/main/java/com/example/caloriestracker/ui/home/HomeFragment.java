package com.example.caloriestracker.ui.home;

import android.content.Intent;
import android.icu.util.Calendar;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriestracker.Food;
import com.example.caloriestracker.FoodDetails;
import com.example.caloriestracker.MainActivity;
import com.example.caloriestracker.Quantity;
import com.example.caloriestracker.R;
import com.example.caloriestracker.WorkoutPlan;
import com.example.caloriestracker.databinding.FragmentHomeBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProgressBar progressBar;
    private TextView home_budget, home_cal_consumed,home_cal_burn,home_remaining,home_date,home_bmi, home_range;
    private Button home_workout;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    RecyclerAdapter recyclerAdapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    int weight = 0,height = 0, age = 0, consumed = 0, burn = 0, active = 0;
    double bmi, budget, bmr;
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


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


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy.MM.dd");
        String currentDateandTime = sdf.format(new Date());
        home_date.setText(currentDateandTime);

        userID = fAuth.getCurrentUser().getUid();

        //Recycler View
//        ArrayList<Food> foodArrayList = new ArrayList<>();
//        fStore.collection("users").document(userID)
//                .collection("food").document(date)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            DocumentSnapshot document = task.getResult();
//                            if(document.exists()){
//                                Map<String,Object> foodMap = document.getData();
//                                for(Map.Entry<String, Object> entry : foodMap.entrySet()){
//                                    Map<String,Object>details = (Map<String, Object>) entry.getValue();
//                                    Food food = new Food();
//                                    for(Map.Entry<String,Object> entryset: details.entrySet()){
//                                        if(entryset.getKey().equals("foodName")){
//                                            food.setFoodName(entryset.getValue().toString());
//                                        }
//                                        if(entryset.getKey().equals("dateTime")){
//                                            food.setDateTime(entryset.getValue().toString());
//                                        }
//                                        if(entryset.getKey().equals("totalCal")){
//                                            food.setTotalCal(Integer.parseInt(entryset.getValue().toString()));
//                                            consumed+=Integer.parseInt(entryset.getValue().toString());
//                                        }
//                                        if(entryset.getKey().equals("qty")){
//                                            food.setQty(Integer.parseInt(entryset.getValue().toString()));
//                                        }
//                                        if(entryset.getKey().equals("healthy")){
//                                            food.setHealthy(Boolean.parseBoolean(entryset.getValue().toString()));
//                                        }
//                                    }
//                                    foodArrayList.add(food);
//                                    home_cal_consumed.setText(String.valueOf(consumed));
//                                }
//                            }
//                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                            recyclerAdapter = new RecyclerAdapter(foodArrayList,getContext());
//                            recyclerView.setAdapter(recyclerAdapter);
//                        }
//                    }
//                });

        //Dashboard
        fStore.collection("users").document(userID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        weight = documentSnapshot.getLong("weight").intValue();
                        height = documentSnapshot.getLong("height").intValue();
                        age = documentSnapshot.getLong("age").intValue();
                        active = documentSnapshot.getLong("active").intValue();

                        //burn
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        DocumentReference burnRef = fStore
                                .collection("users").document(userID)
                                .collection("workout").document(date);

                        burnRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if(documentSnapshot.exists()){
                                        burn = (int)Math.round(documentSnapshot.getDouble("burn"));
                                        home_cal_burn.setText(String.valueOf(burn));
                                    }
                                }
                            }
                        });

                        Calendar day = Calendar.getInstance();
                        day.set(Calendar.MILLISECOND, 0);
                        day.set(Calendar.SECOND, 0);
                        day.set(Calendar.MINUTE, 0);
                        day.set(Calendar.HOUR_OF_DAY, 0);

                        fStore.collection("users").document(userID)
                                .collection("food").whereGreaterThan("dateTime",day.getTime())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                consumed += document.getLong("totalCal");
                                            }
                                            home_cal_consumed.setText(String.valueOf(consumed));
                                        }
                                    }
                                });

                        //bmi
                        bmi = weight/(height*height/10000.0);
                        home_bmi.setText(String.format ("%.2f", bmi));

                        //range
                        if(bmi<18.5){
                            home_range.setText("Underweight");
                        }
                        else if(bmi>=18.5 && bmi<25){
                            home_range.setText("Healthy");
                        }
                        else if(bmi>=25.0 && bmi<30){
                            home_range.setText("Overweight ");
                        }
                        else if (bmi>=30.0){
                            home_range.setText("Obesity ");
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


        home_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkoutPlan.class);
                startActivity(intent);
            }
        });


//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Map<String,Object> foodMap = documentSnapshot.getData();
//
//
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });


       //Query
        Query query = fStore.collection("users").document(userID)
                .collection("food").orderBy("dateTime", Query.Direction.DESCENDING);
        //Recycler Option
        FirestoreRecyclerOptions<Food> option = new FirestoreRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();

         adapter = new FirestoreRecyclerAdapter<Food, FoodViewHolder>(option) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycler_view,parent,false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull HomeFragment.FoodViewHolder holder, int position, @NonNull Food model) {
                Date date = model.getDateTime();
                DateFormat dateFormat = new SimpleDateFormat("EEEE, yyyy.mm.dd ");
                String strDate = dateFormat.format(date);
                DateFormat timeFormat = new SimpleDateFormat("hh:mm");
                String strTime = timeFormat.format(date);
                holder.time.setText(strTime);
                holder.date.setText(strDate);
                holder.cal.setText(model.getTotalCal()+" cal");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), FoodDetails.class));
                    }
                });
            }
        };

         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
         recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.startListening();
    }

    private class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private TextView cal;
        private TextView date;
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.home_list_time);
            cal = itemView.findViewById(R.id.home_list_cal);
            date = itemView.findViewById(R.id.home_list_date);
        }
    }
}