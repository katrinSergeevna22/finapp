package com.example.finapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AuthViewModel extends ViewModel {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final MutableLiveData<String> exceptionForRegister = new MutableLiveData<>();
    private final MutableLiveData<String> exceptionForLogIn = new MutableLiveData<>();

    public LiveData<String> register(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {
            exceptionForRegister.setValue("Введите логин и пароль");
        } else if (email.isEmpty()) {
            exceptionForRegister.setValue("Введите логин");
        } else if (password.isEmpty()) {
            exceptionForRegister.setValue("Введите пароль");
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Регистрация прошла успешно
                            exceptionForRegister.setValue("Регистрация прошла успешна!");
                        } else {
                            // Ошибка при регистрации
                            exceptionForRegister.setValue("Ошибка при регистрации");
                        }
                    });
        }
        return exceptionForRegister;
    }

    public LiveData<String> logIn(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {
            exceptionForLogIn.setValue("Введите логин и пароль");
        } else if (email.isEmpty()) {
            exceptionForLogIn.setValue("Введите логин");
        } else if (password.isEmpty()) {
            exceptionForLogIn.setValue("Введите пароль");
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Вход выполнен успешно
                            exceptionForLogIn.setValue("Добро пожаловать!");
                        } else {
                            // Ошибка при входе
                            exceptionForLogIn.setValue("Неверный логин или пароль");
                        }
                    });
        }
        return exceptionForLogIn;
    }

    public void exit() {
        auth.signOut();
    }
}
