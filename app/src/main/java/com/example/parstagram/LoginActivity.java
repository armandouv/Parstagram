package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parstagram.databinding.ActivityLoginBinding;
import com.parse.ParseUser;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    public final static String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            goTimelineActivity();
            finish();
            return;
        }

        binding.loginButton.setOnClickListener((view) -> {
            Editable username = binding.username.getText();
            Editable password = binding.password.getText();
            loginUser(username.toString(), password.toString());

            username.clear();
            password.clear();
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Toast.makeText(this, "Couldn't log in", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            goTimelineActivity();
            finish();
        });
    }

    private void goTimelineActivity() {
        Intent intent = new Intent(this, TimelineActivity.class);
        startActivity(intent);
    }
}