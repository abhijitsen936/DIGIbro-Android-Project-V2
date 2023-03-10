package com.example.digibro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private EditText Username, Password, Password2;
    private RadioGroup RadioGroupB;
    private Button Submit;
    private RadioButton rb;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize Firebase Authentication and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // Initialize UI elements
        Username = findViewById(R.id.name);
        Password = findViewById(R.id.password);
        Password2 = findViewById(R.id.password2);
        RadioGroupB = findViewById(R.id.radioGroupB);

        Submit = findViewById(R.id.submit);

        // Set click listener for Submit button
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                String password2 = Password2.getText().toString();
                int k = RadioGroupB.getCheckedRadioButtonId();
                rb = findViewById(k);
                UserRepository userRepository = UserRepository.getInstance();
                userRepository.setUserEmail(username);


                if (username.isEmpty() || password.isEmpty() || password2.isEmpty() || k == -1) {
                    Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(password2)) {
                    Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {


                    regis( username, password);
                }
            }
        });

    }

    private void reg() {
        Map<String, String> v = new HashMap<>();
        v.put("name", Username.getText().toString());
        v.put("pass", Password.getText().toString());
        v.put("domain", rb.getText().toString());

        String S = rb.getText().toString();
        String u= rb.getText().toString();





        FirebaseFirestore.getInstance().collection("All").add(v).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {




                    if (S.equals("Candidate")) {
                        FirebaseFirestore.getInstance().collection("Vendor").add(v).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {


                                    Toast.makeText(Signup.this, "Registration Successful", Toast.LENGTH_SHORT).show();



                                    startActivity(new Intent(Signup.this, Login.class));
                                } else {
                                    Toast.makeText(Signup.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    if (S.equals("Recruiter")) {

                        FirebaseFirestore.getInstance().collection("RC").add(v).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {

                                    User user = User.getInstance();
                                    user.setUser(u);
                                    Toast.makeText(Signup.this, "Registration Successful", Toast.LENGTH_SHORT).show();


                                    startActivity(new Intent(Signup.this, CandidateList.class));
                                } else {
                                    Toast.makeText(Signup.this, "Registration Failed ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                } else {
                    Toast.makeText(Signup.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });









    }

        private void regis(String a, String b){
            mAuth.createUserWithEmailAndPassword(a, b).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                reg();
                            } else {
                                Toast.makeText(Signup.this, "Registration Failed -", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        }
    }
