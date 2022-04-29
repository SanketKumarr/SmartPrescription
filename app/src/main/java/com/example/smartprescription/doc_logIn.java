package com.example.smartprescription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class doc_logIn extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    TextView register;
    TextView showHide;
    TextView forgotPassword;
    Button LogIn;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_doc_log_in);

        etEmail = findViewById(R.id.doc_logIn_email);
        etPassword = findViewById(R.id.doc_logIn_password);
        register = findViewById(R.id.doc_register_here);
        LogIn = findViewById(R.id.doc_logIn);
        showHide = findViewById(R.id.doc_login_showHide);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        forgotPassword = findViewById(R.id.forgot_password);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordReset();
            }
        });


        showHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showHide.getText().toString().equals("Show")) {
                    etPassword.setTransformationMethod(null);
                    showHide.setText("Hide");
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showHide.setText("Show");
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(doc_logIn.this, sign_in_doctor.class));
            }
        });

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    public void passwordReset() {
        String email = etEmail.getText().toString().trim();

        if(!TextUtils.isEmpty(email)) {

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(doc_logIn.this, "Password resetting email sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            etEmail.setError("Provide email address to sent password resetting mail");
        }
    }

    public void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password cannot be empty");
            etPassword.requestFocus();
        } else {

//            if (user.isEmailVerified()) {
//
//                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(doc_logIn.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(doc_logIn.this, doctor_homePage.class));
//                            finish();
//                        } else {
//                            Toast.makeText(doc_logIn.this, "Login error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            } else {
//                Toast.makeText(this, "Verify your email first", Toast.LENGTH_SHORT).show();
//                mAuth.signOut();
//            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(doc_logIn.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(doc_logIn.this, doctor_homePage.class));
                        finish();
                    } else {
                        Toast.makeText(doc_logIn.this, "Login error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}