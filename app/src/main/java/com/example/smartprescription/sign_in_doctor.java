package com.example.smartprescription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class sign_in_doctor extends AppCompatActivity {

    EditText name;
    EditText high_qualification;
    EditText hosp_name;
    EditText pNumber;
    EditText etEmail;
    EditText etPassword;
    EditText registration_number;
    TextView showHide;
    Button sign_in;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_sign_in_doctor);

        sign_in = findViewById(R.id.id_doc_signIn);
        name = findViewById(R.id.id_doc_Name);
        high_qualification = findViewById(R.id.idHighestQualification);
        hosp_name = findViewById(R.id.doc_hospitalName);
        pNumber = findViewById(R.id.doc_number);
        etEmail = findViewById(R.id.doc_email);
        etPassword = findViewById(R.id.doc_password);
        registration_number = findViewById(R.id.doc_RegistrationNumber);
        showHide = findViewById(R.id.showHide_text);
        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();

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


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEmptyFields()) {
                    createUser();
                } else {
                    Toast.makeText(sign_in_doctor.this, "Fill empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void createUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password cannot be empty");
            etPassword.requestFocus();
        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        verifyEmail();
                        Toast.makeText(sign_in_doctor.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        storeDataToDatabase();
                        startActivity(new Intent(sign_in_doctor.this, doc_logIn.class));
                        finish();
                    } else {
                        Toast.makeText(sign_in_doctor.this, "Registration error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            });

        }
    }

    public void verifyEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(sign_in_doctor.this, "Verification link has been sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EMAIL NOT SENT: ", "MAIL NOT SENT " + e.getMessage());
            }
        });


    }

    public void storeDataToDatabase() {
        String doc_name = name.getText().toString().trim();
        String hospital_name = hosp_name.getText().toString().trim();
        String qualification = high_qualification.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String reg_no = registration_number.getText().toString().trim();
        String number = pNumber.getText().toString().trim();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        HashMap<String, Object> map = new HashMap<>();
        map.put("Doctor name", doc_name);
        map.put("Mobile Number", number);
        map.put("Qualification", qualification);
        map.put("Email", email);
        map.put("Password", password);
        map.put("Hospital name", hospital_name);
        map.put("Registration number", reg_no);
        map.put("UID", uid);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Doctor's data").document(email).set(map);

        Toast.makeText(this, "Data Stored successfully", Toast.LENGTH_SHORT).show();
    }

    public boolean checkEmptyFields() {
        String doc_name = name.getText().toString().trim();
        String quali = high_qualification.getText().toString().trim();
        String hosp = hosp_name.getText().toString().trim();
        String phone = pNumber.getText().toString().trim();
        String reg_no = registration_number.getText().toString().trim();

        if (TextUtils.isEmpty(doc_name)) {
            name.setError("Provide full name");
            return false;

        } else if (TextUtils.isEmpty(quali)) {
            high_qualification.setError("Provide qualification");
            return false;

        } else if (TextUtils.isEmpty(hosp)) {
            hosp_name.setError("Provide hospital/clinic name");
            return false;

        } else if (TextUtils.isEmpty(phone)) {
            pNumber.setError("Provide mobile number");
            return false;
        } else if (TextUtils.isEmpty(reg_no)) {
            registration_number.setError("Provide registration number");
            return false;
        }else {
            return true;
        }
    }
}