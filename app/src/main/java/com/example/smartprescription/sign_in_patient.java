package com.example.smartprescription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class sign_in_patient extends AppCompatActivity {

    EditText patientLastName;
    EditText patientFirstName;
    EditText patientPhone;
    EditText OTP;
    RadioGroup GenderGroup;
    RadioButton Gender;
    Button register;

    private boolean otpSent = false;
    private final String countryCode = "+91";
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_sign_in_patient);

        patientLastName = findViewById(R.id.idPatientLastName);
        patientFirstName = findViewById(R.id.idPatientFirstName);
        GenderGroup = findViewById(R.id.genderGroup);
        OTP = findViewById(R.id.idOTP);
        patientPhone = findViewById(R.id.idPatientPhone);
        register = findViewById(R.id.idPatientRegitserbtn);

        FirebaseApp.initializeApp(this);
        FirebaseAuth firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmptyFields()) {
                    if (otpSent) {
                        final String getOTP = OTP.getText().toString();

                        if (id.isEmpty()) {
                            Toast.makeText(sign_in_patient.this, "Unable to verify OTP", Toast.LENGTH_LONG).show();
                        } else {
                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, getOTP);

                            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = task.getResult().getUser();
                                        Toast.makeText(sign_in_patient.this, "Verified", Toast.LENGTH_LONG).show();
                                        storeUserDataToFireStore();
                                        startActivity(new Intent(sign_in_patient.this, patient_homePage.class));
                                        finish();
                                    } else {
                                        Toast.makeText(sign_in_patient.this, "Something went wrong.!!!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    } else {
                        final String getMobile = patientPhone.getText().toString();
                        // To check if number already exists or not:
                        FirebaseFirestore.getInstance()
                                .collection("User data")
                                .document(getMobile)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Toast.makeText(sign_in_patient.this, "Number already in use", Toast.LENGTH_SHORT).show();
                                } else {
                                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                                            .setPhoneNumber(countryCode + "" + getMobile)
                                            .setTimeout(60L, TimeUnit.SECONDS)
                                            .setActivity(sign_in_patient.this)
                                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                @Override
                                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                    Toast.makeText(sign_in_patient.this, "Verification complete", Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                                    Toast.makeText(sign_in_patient.this, "Something went wrong " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                    super.onCodeSent(s, forceResendingToken);
                                                    OTP.setVisibility(View.VISIBLE);
                                                    Toast.makeText(sign_in_patient.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                                                    register.setText("Verify OTP");
                                                    id = s;
                                                    otpSent = true;
                                                }
                                            }).build();

                                    PhoneAuthProvider.verifyPhoneNumber(options);
                                }
                            }
                        });
//                        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
//                                .setPhoneNumber(countryCode + "" + getMobile)
//                                .setTimeout(60L, TimeUnit.SECONDS)
//                                .setActivity(sign_in_patient.this)
//                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                                    @Override
//                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                        Toast.makeText(sign_in_patient.this, "OTP sent successfully", Toast.LENGTH_LONG).show();
//                                    }
//
//                                    @Override
//                                    public void onVerificationFailed(@NonNull FirebaseException e) {
//                                        Toast.makeText(sign_in_patient.this, "Something went wrong " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//
//                                    @Override
//                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                        super.onCodeSent(s, forceResendingToken);
//                                        OTP.setVisibility(View.VISIBLE);
//                                        register.setText("Verify OTP");
//                                        id = s;
//                                        otpSent = true;
//                                    }
//                                }).build();
//
//                        PhoneAuthProvider.verifyPhoneNumber(options);
                    }
                }
            }
        });


    }

    public boolean checkEmptyFields() {
        String fName = patientFirstName.getText().toString().trim();
        String lName = patientLastName.getText().toString().trim();
        String number = patientPhone.getText().toString().trim();


        if (TextUtils.isEmpty(fName)) {
            patientFirstName.setError("Provide your first name");
            Toast.makeText(this, "Fill your first name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(lName)) {
            patientLastName.setError("Provide your last name");
            Toast.makeText(this, "Fill your last name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (GenderGroup.getCheckedRadioButtonId() == -1) {
//            Gender.setError("Select your gender");
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(number)) {
            patientPhone.setError("Provide yur number");
            Toast.makeText(this, "Fill your number", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void storeUserDataToFireStore() {
        String getPatientFirstName = patientFirstName.getText().toString();
        String getPatientLastName = patientLastName.getText().toString();
        String getPatientPhoneNumber = patientPhone.getText().toString();
        int selectedGenderId = GenderGroup.getCheckedRadioButtonId();
        Gender = findViewById(selectedGenderId);
        String getPatientGender = Gender.getText().toString();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        HashMap<String, Object> map = new HashMap<>();
        map.put("First name", getPatientFirstName);
        map.put("Last name", getPatientLastName);
        map.put("Full name", getPatientFirstName + " " + getPatientLastName);
        map.put("Gender", getPatientGender);
        map.put("UID", uid);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User data")
                .document(getPatientPhoneNumber)
                .set(map);
    }

    public void checkUserExist() {
        String getPatientPhoneNumber = patientPhone.getText().toString();
        FirebaseFirestore.getInstance()
                .collection("User data")
                .document(getPatientPhoneNumber)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Toast.makeText(sign_in_patient.this, "Number already in use", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(sign_in_patient.this, "Verified", Toast.LENGTH_LONG).show();
//                    storeUserDataToFireStore();
//                    startActivity(new Intent(sign_in_patient.this, patient_homePage.class));
//                    finish();

                }
            }
        });
    }

}