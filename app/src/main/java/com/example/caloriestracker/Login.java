package com.example.caloriestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText login_email,login_password;
    private TextView login_register,login_forget_password;
    private Button loginBtn;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_pswd);
        login_register=findViewById(R.id.login_register);
        loginBtn=findViewById(R.id.login_button);
        login_forget_password=findViewById(R.id.login_forget_password);

        fAuth=FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(this);
        login_register.setOnClickListener(this);
        login_forget_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                String email = login_email.getText().toString().trim();
                String password= login_password.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    login_email.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    login_password.setError("Password is Required");
                    return;
                }
                if(password.length()<8){
                    login_password.setError("Password must be at least 8 characters");
                    return;
                }
                //authenticate user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("login","yes");
                            Toast.makeText(Login.this,"Login Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{
                            Log.d("login","no");
                            Toast.makeText(Login.this,"Error, "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;

            case R.id.login_register:
                startActivity(new Intent(getApplicationContext(),Register.class));
                break;

            case R.id.login_forget_password:
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder resetPswdDialog = new AlertDialog.Builder(v.getContext());
                resetPswdDialog.setTitle("Reset Password");
                resetPswdDialog.setMessage("Enter Your Email To Receive Reset Link");
                resetPswdDialog.setView(resetMail);

                resetPswdDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the mail and send reset link
                        String mail=resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Login.this,"Reset Link Sent To Your Mail",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Error! Reset Link Is Not Send"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                resetPswdDialog.setNegativeButton("No", null);

                resetPswdDialog.create().show();

            default:
                break;
        }
    }
}