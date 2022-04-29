package com.example.smartprescription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class check_patient_exists extends AppCompatActivity {

    EditText patientPhoneNumber;
    EditText otp;
    Button verifyBtn;
    private boolean otpSent = false;
    private final String countryCode = "+91";
    private String id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_check_patient_exists);

        patientPhoneNumber = findViewById(R.id.PatientPhoneNumber_check_db);
        otp = findViewById(R.id.patient_otp);
        verifyBtn = findViewById(R.id.Check_Patient_Phone_Number);

        FirebaseApp.initializeApp(this);
        FirebaseAuth firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = patientPhoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    patientPhoneNumber.setError("Provide mobile number");
                    Toast.makeText(check_patient_exists.this, "Provide mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    userExist();
                }
            }
        });


    }

    public void userExist() {

        String getPatientNumber = patientPhoneNumber.getText().toString();
        FirebaseAuth firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        FirebaseFirestore.getInstance()
                .collection("User data")
                .document(getPatientNumber)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    if (otpSent) {
                        final String getOTP = otp.getText().toString();
//                        System.out.println("1");

                        if (id.isEmpty()) {
                            Toast.makeText(check_patient_exists.this, "Unable to verify OTP", Toast.LENGTH_LONG).show();
                        } else {
                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, getOTP);
//                            System.out.println("2");

                            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = task.getResult().getUser();
                                        Toast.makeText(check_patient_exists.this, "Verified", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(check_patient_exists.this, patient_homePage.class));
                                        finish();
                                    } else {
                                        Toast.makeText(check_patient_exists.this, "Something went wrong.!!!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    } else {
                        final String getMobile = patientPhoneNumber.getText().toString();
                        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                                .setPhoneNumber(countryCode + "" + getMobile)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(check_patient_exists.this)
                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        Toast.makeText(check_patient_exists.this, "OTP sent successfully", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        Toast.makeText(check_patient_exists.this, "Something went wrong " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        super.onCodeSent(s, forceResendingToken);
                                        otp.setVisibility(View.VISIBLE);
                                        verifyBtn.setText("Verify OTP");
                                        id = s;
                                        otpSent = true;
                                    }
                                }).build();

                        PhoneAuthProvider.verifyPhoneNumber(options);
                    }
                } else {
                    Toast.makeText(check_patient_exists.this, "New user, please sign in first", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(check_patient_exists.this, sign_in_patient.class));
                    finish();
                }
            }
        });
    }
}