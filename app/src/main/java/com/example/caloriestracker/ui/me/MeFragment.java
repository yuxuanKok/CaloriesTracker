package com.example.caloriestracker.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.caloriestracker.Login;
import com.example.caloriestracker.R;
import com.example.caloriestracker.UserProfile;
import com.example.caloriestracker.databinding.FragmentMeBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MeFragment extends Fragment {

    private MeViewModel meViewModel;
    private FragmentMeBinding binding;
    private Button logoutBtn, settingBtn;
    FirebaseAuth fAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        meViewModel = new ViewModelProvider(this).get(MeViewModel.class);

        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textMe;
//        meViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
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
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
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