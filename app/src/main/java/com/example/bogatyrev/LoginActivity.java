package com.example.bogatyrev;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginButton;
    private TextView registerLink, errorText;
    private DatabaseAdapter dbAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startMainActivity();
            return;
        }

        initViews();
        setupListeners();
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        errorText = findViewById(R.id.errorText);
        dbAdapter = new DatabaseAdapter(this);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> login());
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showError("Введите email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError("Введите пароль");
            return;
        }

        dbAdapter.open();
        boolean success = dbAdapter.loginUser(email, password);
        dbAdapter.close();

        if (success) {
            sessionManager.setLogin(true, email);
            startMainActivity();
        } else {
            showError("Неверный email или пароль");
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}