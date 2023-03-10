package com.example.digibro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CandidateDetails extends AppCompatActivity {
//    final ProgressDialog p = new ProgressDialog(CandidateDetails.this);

    private Uri mImageUri;
    private Uri mFileUri;

    private static final int FILE_REQUEST_ID = 2;
    private final int IMG_REQUEST_ID = 1;
    private ImageView mImageView;
    private TextView mNameTextView, mAgeTextView, mEmailTextView;
    private Button mResumeButton, IMGButton, CVButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageRef;


//
    UserRepository userRepository = UserRepository.getInstance();
    String userEmail = userRepository.getUserEmail();


    String collectionName = "Vendor";
    String userId = userEmail;
    String documentPath = findDocumentPathByEmail(userId, db, collectionName);

    DocumentReference docRef = db.collection("Vendor").document(documentPath);





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
        IMGButton=findViewById(R.id.button5);
        CVButton=findViewById(R.id.button6);

        //   // Initialize Firebase components
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //   // Load candidate data from Firestore
        UserRepository userRepository = UserRepository.getInstance();


        String candidateId = userRepository.getUserEmail();
        String collectionName = "Vendor";

        String cd=findDocumentPathByEmail(candidateId, db, collectionName);

//








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

                      // Set click listener for resume button
                    mResumeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //       // Open candidate resume
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(resumeUrl));
                            startActivity(intent);
                        }
                    });

                    IMGButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "select picture"), IMG_REQUEST_ID);




                        }
                    });




                    CVButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.setType("application/pdf");
                            startActivityForResult(i, FILE_REQUEST_ID);



                        }
                    });


                }
            }




        });











    }




    // to add the picture on the image view ............
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST_ID && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);


            if (mImageUri != null) {

                StorageReference reference = mStorageRef.child("picture/" + UUID.randomUUID().toString());
                reference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String k = uri.toString();

                                Map<String, Object> data = new HashMap<>();
                                data.put("Profile picture Link", k);

                                docRef.update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Toast.makeText(CandidateDetails.this, "Picture Uploading Successful", Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(CandidateDetails.this, "Picture Uploading Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        });






                    }



                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(CandidateDetails.this, "ERROR in Picture", Toast.LENGTH_SHORT).show();
                    }
                });


            }




        }
        if (requestCode == FILE_REQUEST_ID && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mFileUri = data.getData();



            if (mFileUri != null) {

                StorageReference fileReference = mStorageRef.child("files/" + UUID.randomUUID().toString()
                );
                fileReference.putFile(mFileUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();


                                        Map<String, Object> cv = new HashMap<>();
                                        cv.put("resume link", downloadUrl);
                                        docRef.update(cv)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Toast.makeText(CandidateDetails.this, "CV Uploading Successful", Toast.LENGTH_SHORT).show();


                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        Toast.makeText(CandidateDetails.this, "CV Uploading Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });




                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(CandidateDetails.this, "ERROR in CV", Toast.LENGTH_SHORT).show();
                            }
                        });







            }




        }

    }











    public String findDocumentPathByEmail(String email, FirebaseFirestore db, String collectionName) {
        Task<QuerySnapshot> query = db.collection(collectionName).whereEqualTo("name", email).get();
        while (!query.isComplete()) {
            // wait for the query to complete
        }
        if (query.isSuccessful()) {
            for (QueryDocumentSnapshot document : query.getResult()) {
                String documentId = document.getId();
                return documentId;
            }
        }
        return null;
    }





}



