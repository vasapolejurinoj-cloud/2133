package com.example.bogatyrev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseAdapter dbAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        dbAdapter = new DatabaseAdapter(this);
        sessionManager = new SessionManager(this);

        // Проверяем, не вошел ли уже пользователь
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        dbAdapter.open();

        // Теперь метод возвращает long (ID пользователя)
        long userId = dbAdapter.loginUser(email, password);

        dbAdapter.close();

        if (userId != -1) {  // Проверяем, что пользователь найден (ID не равен -1)
            // Сохраняем сессию
            sessionManager.setLogin(true, email);
            Toast.makeText(this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}