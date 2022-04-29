package com.example.smartprescription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class doctor_homePage extends AppCompatActivity {

    Button prescribe;
    Button logout;
    Button resendLink;
    TextView verifyMsg;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_doctor_home_page);

        prescribe = findViewById(R.id.id_prescribe_a_patient);
        logout = findViewById(R.id.id_log_out_doc);
        mAuth = FirebaseAuth.getInstance();
        verifyMsg = findViewById(R.id.id_emailVerifying_msg);
        resendLink = findViewById(R.id.id_resend_link);

        FirebaseUser user = mAuth.getCurrentUser();

        if(!user.isEmailVerified()) {
            resendLink.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            resendLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Verification link has been sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("EMAIL NOT SENT: ", "MAIL NOT SENT " + e.getMessage());
                        }
                    });
                }
            });
        }

        prescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isEmailVerified()) {
                    Intent intent = new Intent(doctor_homePage.this, prescribe_a_patient.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(doctor_homePage.this, "Verify your email first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(doctor_homePage.this, MainActivity.class));
            }
        });
    }
}