package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parstagram.databinding.ActivitySignupBinding;
import com.parse.ParseUser;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(this, TimelineActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        mBinding.signupButton.setOnClickListener((view) -> {
            Editable username = mBinding.usernameSignup.getText();
            Editable password = mBinding.passwordSignup.getText();
            signupUser(username.toString(), password.toString());

            username.clear();
            password.clear();
        });
    }

    private void signupUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(e -> {
            if (e != null) {
                Toast.makeText(this, "Couldn't sign up", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, TimelineActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}