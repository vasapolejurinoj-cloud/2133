package com.example.bogatyrev;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText emailInput, passwordInput, confirmPasswordInput;
    private MaterialButton registerButton;
    private TextView loginLink, errorText;
    private DatabaseAdapter dbAdapter;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        errorText = findViewById(R.id.errorText);
        dbAdapter = new DatabaseAdapter(this);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> register());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void register() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showError("Введите email");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Введите корректный email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError("Введите пароль");
            return;
        }
        if (password.length() < 6) {
            showError("Пароль должен быть не менее 6 символов");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Пароли не совпадают");
            return;
        }

        dbAdapter.open();
        if (dbAdapter.userExists(email)) {
            showError("Пользователь с таким email уже существует");
            dbAdapter.close();
            return;
        }

        long result = dbAdapter.registerUser(email, password);
        dbAdapter.close();

        if (result != -1) {
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            showError("Ошибка регистрации");
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }
}