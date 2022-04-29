package com.example.smartprescription;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        Button docSignin = findViewById(R.id.idSign_in_as_doctor);
        docSignin.setOnClickListener(view -> DoctorRegistration());

        Button patientSignin = findViewById(R.id.idSign_in_as_patient);
        patientSignin.setOnClickListener(view -> PatientRegistration());
    }
    public void DoctorRegistration() {
        Intent intent = new Intent(this, doc_logIn.class);
        startActivity(intent);
    }

    public void PatientRegistration() {
        Intent intent = new Intent(this, check_patient_exists.class);
        startActivity(intent);
    }
}
