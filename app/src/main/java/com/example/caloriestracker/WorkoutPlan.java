package com.example.caloriestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class WorkoutPlan extends AppCompatActivity {

    private Button workout_intensity, workout_type, workout_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan);

        workout_intensity=findViewById(R.id.workout_intensity);
        workout_type=findViewById(R.id.workout_type);
        workout_confirm=findViewById(R.id.workout_confirm);

        workout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean notMissingValue = true;
                if(TextUtils.isEmpty(workout_intensity.getText().toString())){
                    workout_intensity.setError("Intensity is required");
                    notMissingValue=false;
                }
                else{
                    workout_intensity.setError(null);
                }
                if(TextUtils.isEmpty(workout_type.getText().toString())){
                    workout_type.setError("Type is required");
                    notMissingValue=false;
                }
                else {
                    workout_type.setError(null);
                }
                if(notMissingValue){
                    int intensity = Integer.parseInt(workout_intensity.getTag().toString());
                    int type = Integer.parseInt(workout_type.getTag().toString());
                    Intent intent = new Intent(WorkoutPlan.this, WorkoutStep.class);
                    intent.putExtra("intensity",intensity);
                    intent.putExtra("type",type);
                    startActivity(intent);
                }

            }
        });
    }

    public void openIntensityDialog(View view) {
        String [] intensityArray = new String[]{"Light","Moderate","Vigorous"};
        dialog("Intensity",2,0,intensityArray,workout_intensity);
    }

    public void openTypeDialog(View view) {
        String [] typeArray = new String[]{"Strength","Aerobic","Flexibility","Balance"};
        dialog("Type",3,0,typeArray,workout_type);
    }

    public void dialog(String title,int max,int min,String [] string,Button btn){
        final Dialog d = new Dialog(WorkoutPlan.this);
        d.setTitle(title);
        d.setContentView(R.layout.dialog);
        Button saveBtn = (Button) d.findViewById(R.id.saveBtn);
        Button cancelBtn = (Button) d.findViewById(R.id.cancelBtn);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
        np.setMaxValue(max); // max value 100
        np.setMinValue(min);   // min value 0
        np.setDisplayedValues(string);
        np.setWrapSelectorWheel(true);

        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int value = np.getValue();
                btn.setText(string[value]);//set the value to textview
                btn.setTag(value);
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