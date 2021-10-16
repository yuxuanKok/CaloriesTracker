package com.example.caloriestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText register_email,register_pswd, register_confirm_pswd;
    Button registerBtn;
    TextView loginLink;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_email=findViewById(R.id.register_email);
        register_pswd=findViewById(R.id.register_pswd);
        register_confirm_pswd=findViewById(R.id.register_confirm_pswd);
        registerBtn=findViewById(R.id.register_button);
        loginLink=findViewById(R.id.register_login);

        fAuth=FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        registerBtn.setOnClickListener(this);
        loginLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button:
                String email = register_email.getText().toString().trim();
                String password= register_pswd.getText().toString().trim();
                String confirmPassword= register_confirm_pswd.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    register_email.setError("Email is Required");
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    register_email.setError("Email not valid");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    register_pswd.setError("Password is Required");
                    return;
                }
                if(password.length()<8){
                    register_pswd.setError("Password must be at least 8 characters");
                    return;
                }
                if(!confirmPassword.equals(password)){
                    Log.d(TAG,confirmPassword + "/password:"+password);
                    register_confirm_pswd.setError("Password not same");
                    return;
                }

                //register user
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"User Created",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),UserProfile.class));
                        }
                        else{
                            Toast.makeText(Register.this,"Error, "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.register_login:
                startActivity(new Intent(getApplicationContext(),Login.class));
                break;

            default:
                break;
        }
    }
}