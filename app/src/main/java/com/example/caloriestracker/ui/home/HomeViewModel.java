package com.example.caloriestracker.ui.home;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.caloriestracker.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.Executor;

public class HomeViewModel extends ViewModel {

//    private MutableLiveData<String> mText;


    public HomeViewModel() {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");


    }

//    public LiveData<String> getText() {
//        return mText;
//    }
}