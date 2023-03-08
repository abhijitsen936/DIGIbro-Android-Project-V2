package com.example.digibro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class CandidateDetails extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mNameTextView, mAgeTextView, mEmailTextView;
    private Button mResumeButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_details);

        //  // Initialize views
        mImageView = findViewById(R.id.imageView);
        mNameTextView = findViewById(R.id.textView12);
        mAgeTextView = findViewById(R.id.textView4);
        mEmailTextView = findViewById(R.id.textView9);
        mResumeButton = findViewById(R.id.button3);

        //   // Initialize Firebase components
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //   // Load candidate data from Firestore
        UserRepository userRepository = UserRepository.getInstance();


        String candidateId = userRepository.getUserEmail();
        String collectionName = "Vendor";

        String cd=findDocumentPathByEmail(candidateId, db, collectionName);











        DocumentReference candidateRef = mFirestore.collection("Vendor").document(cd);
        candidateRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Get candidate data
                    String name = documentSnapshot.getString("Full Name");
                    String age = documentSnapshot.getString("age");
                    String email = documentSnapshot.getString("name");
                    String resumeUrl = documentSnapshot.getString("resume link");
                    String imageFilename = documentSnapshot.getString("Profile picture Link");

                    //   // Set candidate data to views
                    mNameTextView.setText(name);
                    mAgeTextView.setText(age);
                    mEmailTextView.setText(email);

                    //     // Load candidate image from Firebase Storage
                    Picasso.get().load(imageFilename).into(mImageView);

                    //  // Set click listener for resume button
                    mResumeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //       // Open candidate resume
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(resumeUrl));
                            startActivity(intent);
                        }
                    });
                }
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



