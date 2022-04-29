package com.example.smartprescription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class patient_homePage extends AppCompatActivity {

    TextView getId;
    String currentUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String phoneNo;
    Button loadPrescription;
    Button logOut;
    EditText patientNumber;
    EditText patientUid;
    TextView loadedMedicine;
    String fullName;
    EditText displayDate;
    DatePickerDialog.OnDateSetListener dateSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_patient_home_page);

        if (currentUser != null) {
            phoneNo = currentUser.getPhoneNumber();
        } else {
            System.out.println("Current user is null");
        }

        getId = findViewById(R.id.getUID);
        loadPrescription = findViewById(R.id.id_load_prescription);
        patientNumber = findViewById(R.id.id_patient_number);
        patientUid = findViewById(R.id.id_patient_uid);
        logOut = findViewById(R.id.id_patientLogOut);
        loadedMedicine = findViewById(R.id.prescription_loadedMedicine);
        displayDate = findViewById(R.id.prescription_date);

//        displayDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar cal = Calendar.getInstance();
//                int year = cal.get(Calendar.YEAR);
//                int month = cal.get(Calendar.MONTH);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog dialog = new DatePickerDialog(
//                        patient_homePage.this,
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//                        dateSetListener, year, month, day);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//            }
//        });
//
//        dateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                month = month + 1;
//                String date = day + "/" + month + "/" + year;
//                displayDate.setText(date);
//            }
//        };

        getId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNo, null, currentUserUid, null, null);
                            Toast.makeText(patient_homePage.this, "ID sent via SMS", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(patient_homePage.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
                    }
                }
            }

        });

        loadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate_patient_number();

            }
        });

        logOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });


    }



    public void sendSMS() {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, currentUserUid, null, null);
            Toast.makeText(patient_homePage.this, "ID sent via SMS", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(patient_homePage.this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    public void validate_patient_number() {
        String Pnumber = patientNumber.getText().toString().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User data")
                .document(Pnumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    Toast.makeText(patient_homePage.this, "Number verified", Toast.LENGTH_SHORT).show();
                    validate_patient_uid();
                } else {
                    Toast.makeText(patient_homePage.this, "Number not registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validate_patient_uid() {
        String Puid = patientUid.getText().toString().trim();
        String number = patientNumber.getText().toString().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User data").document(number)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (Puid.equals(document.getString("UID"))) {
                            Toast.makeText(patient_homePage.this, "Id verified", Toast.LENGTH_SHORT).show();
                            Toast.makeText(patient_homePage.this, "Patient verified", Toast.LENGTH_SHORT).show();
                            loadPrescription();

                        } else {
                            Toast.makeText(patient_homePage.this, "Id doesn't match or exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(patient_homePage.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void loadPrescription() {

//        Log.d("ADMIN MSG", "in load prescription");

        String number = patientNumber.getText().toString().trim();
        String id = patientUid.getText().toString().trim();
        String date = displayDate.getText().toString().trim();

        loadedMedicine.setText("");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User data").document(number)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fullName = document.getString("Full name");
                        db.collection("prescribed_patients").document(number)
                                .collection(fullName).document(date)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()) {
                                        loadedMedicine.setText(document.getString("Prescription"));
                                    }
                                    else {
                                        Toast.makeText(patient_homePage.this, "Prescription of given date doesn't exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(patient_homePage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
//                        Log.e("ADMIN MSG", "document doesn't exist");
                    }
                }
            }
        });
    }

}