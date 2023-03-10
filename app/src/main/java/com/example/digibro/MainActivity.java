package com.example.digibro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    Button login1, signup1;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        signup1=findViewById(R.id.signup1);

        Button loginButton = findViewById(R.id.submit);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEditText = findViewById(R.id.email);
                EditText passwordEditText = findViewById(R.id.pwd);

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    // Show toast message if either email or password is empty
                    Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call the signInWithEmailAndPassword method to log in the user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<AuthResult>() {

                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User u = User.getInstance();
                                String user = u.getUser();
                                final String[] k = new String[1];
                                final String[] v = new String[1];

                                String dcPath = findDocumentPathByEmail(email, db, "All");
                                String dbPathV= findDocumentPathByEmail(email, db, "Vendor");

                            FirebaseFirestore.getInstance().collection("All").document(dcPath).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    k[0] = value.getString("domain");


                                   if(k[0].equals("Candidate")){

                                     FirebaseFirestore.getInstance().collection("Vendor").document(dbPathV).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                                                      v[0] = value.getString("Full Name");
                                                                     if(v[0]!= null){
                                                               Toast.makeText(MainActivity.this, "You are already Registered", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(MainActivity.this, CandidateDetails.class));
return;
                                                               }else {
                                                                         startActivity(new Intent(MainActivity.this, Login.class));
                                                                         return;
                                                                     }




                                }
                            });






                                   }

                                    if (k[0].equals("Recruiter")) {
                                       startActivity(new Intent(MainActivity.this, CandidateList.class));
                                   }

//                                    else {
//                                        startActivity(new Intent(MainActivity.this, Login.class));
//                                    }


                                }
                            });




















//                                if(user.equals("Candidate")){
//                                    startActivity(new Intent(MainActivity.this, Login.class));
//                                }
//                                if(user.equals("Recruiter")){
//                                    startActivity(new Intent(MainActivity.this, CandidateList.class));
//                                }









//                                startActivity(new Intent(MainActivity.this, Login.class));

                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                String userEmail = auth.getCurrentUser().getEmail();
                                UserRepository userRepository = UserRepository.getInstance();
                                userRepository.setUserEmail(userEmail);




                                Toast.makeText(MainActivity.this, "Welcome.", Toast.LENGTH_SHORT).show();

                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Please SignUp.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });










        signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(MainActivity.this, Signup.class));
            }
        });


    }


    public String findDocumentPathByEmail(String email, FirebaseFirestore db, String collectionName) {
        Task<QuerySnapshot> query = db.collection(collectionName).whereEqualTo("name", email).get();
        while (!query.isComplete()) {
            // wait for the query to complete
        }
        if (query.isSuccessful()) {
            for (QueryDocumentSnapshot document : query.getResult()) {
                String documentId = document.getId();
//                String documentPath = collectionName + "/" + documentId;
                return documentId;
            }
        }
        return null;
    }




}