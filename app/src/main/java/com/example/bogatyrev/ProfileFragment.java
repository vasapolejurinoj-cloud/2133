package com.example.bogatyrev;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private TextView emailText;
    private Button addItemButton, logoutButton;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();
        loadUserInfo();
    }

    private void initViews(View view) {
        emailText = view.findViewById(R.id.emailText);
        addItemButton = view.findViewById(R.id.addItemButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        sessionManager = new SessionManager(requireContext());
    }

    private void setupListeners() {
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditItemActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadUserInfo() {
        String email = sessionManager.getUserEmail();
        emailText.setText(email);
    }
}