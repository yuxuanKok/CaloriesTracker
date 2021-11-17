package com.example.caloriestracker.ui.me;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.caloriestracker.Login;
import com.example.caloriestracker.MainActivity;
import com.example.caloriestracker.UserProfile;
import com.example.caloriestracker.databinding.FragmentMeBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;
    private Button logoutBtn, settingBtn;
    FirebaseAuth fAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        logoutBtn= binding.logoutBtn;
        settingBtn = binding.meSetting;

        fAuth = FirebaseAuth.getInstance();

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfile.class);
                intent.putExtra("uid",fAuth.getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder logoutDialog = new AlertDialog.Builder(getContext());
                logoutDialog.setTitle("Logout? ")
                        .setMessage("Do you want to logout this account")
                        .setNegativeButton("No",null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getActivity(), Login.class));
                                getActivity().finish();
                            }
                        })
                        .show();
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