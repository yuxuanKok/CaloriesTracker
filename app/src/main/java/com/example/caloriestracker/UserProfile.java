package com.example.caloriestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    private Button profile_gender, profile_age, profile_height, profile_weight, profile_save,profile_active;
    private EditText profile_username;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profile_age=findViewById(R.id.profile_age);
        profile_gender=findViewById(R.id.profile_gender);
        profile_height=findViewById(R.id.profile_height);
        profile_weight=findViewById(R.id.profile_weight);
        profile_save=findViewById(R.id.profile_save);
        profile_username=findViewById(R.id.profile_username);
        profile_active=findViewById(R.id.profile_active);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        String uid;

        if (extras != null) {
            uid = extras.getString("uid");
            fStore.collection("users").document(uid)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    profile_username.setText(documentSnapshot.getString("username"));
                    if(documentSnapshot.getLong("gender").intValue()==0){
                        profile_gender.setText("FEMALE");
                    }
                    else{
                        profile_gender.setText("MALE");
                    }
                    profile_gender.setTag(documentSnapshot.getLong("gender").intValue());
                    profile_age.setText(String.valueOf(documentSnapshot.getLong("age").intValue()));
                    profile_height.setText(String.valueOf(documentSnapshot.getLong("height").intValue()));
                    profile_weight.setText(String.valueOf(documentSnapshot.getLong("weight").intValue()));
                    String activeText=null;
                    switch (documentSnapshot.getLong("active").intValue()){
                        case 0:
                            activeText="Sedentary (little or no exercise)";
                            break;
                        case 1:
                            activeText="Lightly Active (exercise 1–3 days/week)";
                            break;
                        case 2:
                            activeText="Moderately Active (exercise 3–5 days/week)";
                            break;
                        case 3:
                            activeText="Active (exercise 6–7 days/week)";
                            break;
                        case 4:
                            activeText="Very Active (hard exercise 6–7 days/week)";
                            break;
                        default:
                            break;
                    }

                    profile_active.setText(activeText);
                    profile_active.setTag(documentSnapshot.getLong("active").intValue());
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfile.this,"Fail to get data",Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        profile_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean notMissingValue = true;
                String username = profile_username.getText().toString().trim();
                String gender = profile_gender.getText().toString();
                String age = profile_age.getText().toString();
                String height = profile_height.getText().toString();
                String weight = profile_weight.getText().toString();
                String active = profile_active.getText().toString();
                if(TextUtils.isEmpty(username)){
                    profile_username.setError("Username is required");
                    notMissingValue=false;
                }
                else{
                    profile_username.setError(null);
                }
                if(TextUtils.isEmpty(gender)){
                    profile_gender.setError("Gender is required");
                    notMissingValue=false;
                }
                else{
                    profile_gender.setError(null);
                }
                if(TextUtils.isEmpty(age)){
                    profile_age.setError("Age is required");
                    notMissingValue=false;
                }
                else{
                    profile_age.setError(null);
                }
                if(TextUtils.isEmpty(height)){
                    profile_height.setError("Height is required");
                    notMissingValue=false;
                }
                else{
                    profile_height.setError(null);
                }
                if(TextUtils.isEmpty(weight)){
                    profile_weight.setError("Weight is required");
                    notMissingValue=false;
                }
                else{
                    profile_weight.setError(null);
                }
                if(TextUtils.isEmpty(active)){
                    profile_active.setError("Activeness is required");
                    notMissingValue=false;
                }
                else{
                    profile_active.setError(null);
                }

                if (notMissingValue){
                    int gender_int = Integer.parseInt(profile_gender.getTag().toString());
                    int age_int = Integer.parseInt(age);
                    int height_int = Integer.parseInt(height);
                    int weight_int = Integer.parseInt(weight);
                    int active_int = Integer.parseInt(profile_active.getTag().toString());

                    userID=fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference=fStore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("username",username);
                    user.put("gender", gender_int);
                    user.put("age",age_int);
                    user.put("height",height_int);
                    user.put("weight",weight_int);
                    user.put("active",active_int);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UserProfile.this,"User "+userID+" Profile Updated",Toast.LENGTH_SHORT).show();
                        }
                    });
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            }
        });
    }

    public void openGenderPicker(View view) {
        String [] genderArray = new String[]{"Female","Male"};
        dialog("Gender",1,0,0,genderArray,profile_gender);
    }

    public void openAgePicker(View view) {
        dialog("Age",100,1,18,null,profile_age);
    }

    public void openHeightPicker(View view) {
        dialog("Height",220,40,150,null,profile_height);
    }

    public void openWeightPicker(View view) {
        dialog("Weight",200,2,50,null,profile_weight);
    }

    public void openActivePicker(View view) {
        String [] activeArray = new String[]{"Sedentary (little or no exercise)","Lightly Active (exercise 1–3 days/week)",
                "Moderately Active (exercise 3–5 days/week)","Active (exercise 6–7 days/week)","Very Active (hard exercise 6–7 days/week)"};
        dialog("Activeness",4,0,0,activeArray,profile_active);
    }

    public void dialog(String title,int max,int min,int defaultValue,String [] string,Button btn){
        final Dialog d = new Dialog(UserProfile.this);
        d.setTitle(title);
        d.setContentView(R.layout.dialog);
        Button saveBtn = (Button) d.findViewById(R.id.saveBtn);
        Button cancelBtn = (Button) d.findViewById(R.id.cancelBtn);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
        np.setMaxValue(max); // max value 100
        np.setMinValue(min);   // min value 0
        np.setDisplayedValues(string);
        np.setWrapSelectorWheel(true);
        np.setValue(defaultValue);

        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int value = np.getValue();
                if(string!=null){ //check whether there are string display value
                    btn.setText(string[value]);//set the value to textview
                    btn.setTag(value);
                }
                else{
                    btn.setText(Integer.toString(value)); //set the value to textview
                }
                d.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });
        d.show();
    }
}