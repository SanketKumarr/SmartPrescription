package com.example.smartprescription;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class prescribe_a_patient extends AppCompatActivity {

    boolean verified = false;
    EditText patient_number;
    EditText patient_uid;
    EditText prescription;
    //    EditText medicine_dosage;
//    EditText medicine_duration;
    Button submit;
    String name = "";
    FirebaseUser user;
    String userId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_prescribe_a_patient);

        patient_number = findViewById(R.id.patient_number_toCheck);
        patient_uid = findViewById(R.id.patient_id_toCheck);
        prescription = findViewById(R.id.id_prescription_input);
//        medicine_dosage = findViewById(R.id.id_dosage_input);
//        medicine_duration = findViewById(R.id.id_duration_input);
        submit = findViewById(R.id.id_validate);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEmptyFields()) {
                    validate_patient_number();
                }
            }
        });
    }

    public boolean checkEmptyFields() {
        String number = patient_number.getText().toString().trim();
        String id = patient_uid.getText().toString().trim();
        String pres = prescription.getText().toString().trim();

        if (TextUtils.isEmpty(number)) {
            patient_number.setError("Number cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(id)) {
            patient_uid.setError("ID cannot be empty");
            return false;
        } else if (TextUtils.isEmpty(pres)) {
            prescription.setError("Prescription cannot be empty");
            return false;
        } else {
            return true;
        }
    }

    public void validate_patient_number() {
        String Pnumber = patient_number.getText().toString().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User data")
                .document(Pnumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    Toast.makeText(prescribe_a_patient.this, "Number verified", Toast.LENGTH_SHORT).show();
                    validate_patient_uid();
                } else {
                    Toast.makeText(prescribe_a_patient.this, "Number not registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void upload_prescription() {

        String number = patient_number.getText().toString().trim();
        String pres = prescription.getText().toString().trim();
        String doc_name = user.getDisplayName();
        String hosp_name = "";
//        String dosage = medicine_dosage.getText().toString().trim();
//        String duration = medicine_duration.getText().toString().trim();
        String curr_date;
        Calendar calendar;
        SimpleDateFormat simpleDateFormat;


        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd.LLLL.yyyy");
        curr_date = simpleDateFormat.format(calendar.getTime()).toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("Prescription", pres);
//        map.put("Dosage", dosage);
//        map.put("Duration", duration);
        map.put("Doctor name", doc_name);
        map.put("Timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User data").document(number)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name = document.getString("Full name");
//                        Toast.makeText(prescribe_a_patient.this, name, Toast.LENGTH_SHORT).show();
                        db.collection("prescribed_patients")
                                .document(number)
                                .collection(name)
                                .document(curr_date).set(map);

                    } else {
                        Toast.makeText(prescribe_a_patient.this, "No such document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(prescribe_a_patient.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d("full name", name);
        System.out.println("name: " + name);


//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("prescribed_patients")

//                .document(number)
//                .collection(curr_date)
//                .document("Medicine name: " + medicine).set(map);

//        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Toast.makeText(this, "Prescription uploaded", Toast.LENGTH_SHORT).show();

        prescription.getText().clear();
//        medicine_duration.getText().clear();
//        medicine_dosage.getText().clear();


    }

    public void validate_patient_uid() {
        String Puid = patient_uid.getText().toString().trim();
        String number = patient_number.getText().toString().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference collRef = db.collection("User data");
//        Query query = collRef.whereEqualTo("UID", Puid);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot document : task.getResult()) {
//                        if (document.exists()) {
//                            Toast.makeText(prescribe_a_patient.this, "Id verified", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(prescribe_a_patient.this, "Patient verified", Toast.LENGTH_SHORT).show();
//                            upload_prescription();
//
//                        } else {
//                            Toast.makeText(prescribe_a_patient.this, "Id doesn't exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//        });

        db.collection("User data").document(number)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (Puid.equals(document.getString("UID"))) {
                            Toast.makeText(prescribe_a_patient.this, "Id verified", Toast.LENGTH_SHORT).show();
                            Toast.makeText(prescribe_a_patient.this, "Patient verified", Toast.LENGTH_SHORT).show();
                            upload_prescription();
                        } else {
                            Toast.makeText(prescribe_a_patient.this, "Id doesn't match or exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(prescribe_a_patient.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


}