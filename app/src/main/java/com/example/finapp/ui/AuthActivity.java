package com.example.finapp.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.databinding.ActivityAuthBinding;
import com.example.finapp.viewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding binding;
    private AuthViewModel viewModel;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        binding.ibRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etLogin.getText().toString();
                String password = binding.etPassword.getText().toString();

                if (!password.matches("^(?=.*[A-Za-z]+|[\\d]+)[A-Za-z\\d!@#\\$%^&*()-+=]{8,}$")) {
                    Toast.makeText(AuthActivity.this, "Ненадежный пароль", Toast.LENGTH_LONG).show();
                } else {
                    viewModel.register(email, password).observe(AuthActivity.this, new Observer<String>() {
                        @Override
                        public void onChanged(String text) {
                            if (!text.isEmpty()) {
                                Toast.makeText(AuthActivity.this, text, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        binding.ibLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etLogin.getText().toString();
                String password = binding.etPassword.getText().toString();

                viewModel.logIn(email, password).observe(AuthActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String text) {
                        if ("Добро пожаловать!".equals(text)) {
                            startActivity(new Intent(AuthActivity.this, MainActivity.class));
                            finish();
                        }

                        if (!text.isEmpty()) {
                            Toast.makeText(AuthActivity.this, text, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
