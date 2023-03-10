package com.example.digibro;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;

import android.net.Uri;
        import android.os.Bundle;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Login extends AppCompatActivity {
    private Uri mImageUri;
    private Uri mFileUri;
    String k, dl;
    private static final int FILE_REQUEST_ID = 2;
    private final int IMG_REQUEST_ID = 1;



    private TextView fileNameTextView;

    private ImageView ProfilePic;

    private EditText InputCName;
    private EditText InputCAge;
    private Button UploadResume, IMGbutton, btnSave;



    FirebaseStorage storage;
    private StorageReference mStorageRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    UserRepository userRepository = UserRepository.getInstance();
    String userEmail = userRepository.getUserEmail();


    String collectionName = "Vendor";
    String userId = userEmail;
    String documentPath = findDocumentPathByEmail(userId, db, collectionName);
    DocumentReference docRef = db.collection("Vendor").document(documentPath);

    public Login() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IMGbutton = findViewById(R.id.button);
        btnSave = findViewById(R.id.BtnSave);
        ProfilePic = findViewById(R.id.ProfilePic);
        InputCName = findViewById(R.id.InputCName);
        InputCAge = findViewById(R.id.InputCAge);
        UploadResume = findViewById(R.id.UploadResume);
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        fileNameTextView=findViewById(R.id.fileNameTextView);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        final ProgressDialog p = new ProgressDialog(Login.this);
        p.setTitle("Uploading");





        IMGbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select picture"), IMG_REQUEST_ID);



            }
        });





        UploadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(i, FILE_REQUEST_ID);









                                        }
        });



        // save button use to save the information which are selected
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mImageUri != null) {

                    StorageReference reference = mStorageRef.child("picture/" + UUID.randomUUID().toString());
                    reference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    k = uri.toString();
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("Profile picture Link", k);

                                    docRef.update(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Toast.makeText(Login.this, "Picture Uploading Successful", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Login.this, CandidateDetails.class));

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(Login.this, "Picture Uploading Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            });





                        }



                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    });


                }


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
                                            dl = uri.toString();

                                            Map<String, Object> cv = new HashMap<>();
                                            cv.put("resume link", dl);


                                            docRef.update(cv)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Toast.makeText(Login.this, "CV Uploading Successful", Toast.LENGTH_SHORT).show();


                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            Toast.makeText(Login.this, "CV Uploading Failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });


                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "ERROR", Toast.LENGTH_SHORT).show();
                                }
                            });







                }






                String name = InputCName.getText().toString();
                String age = InputCAge.getText().toString();

                UserRepository userRepository = UserRepository.getInstance();
                String userEmail = userRepository.getUserEmail();


                String collectionName = "Vendor";
                String userId = userEmail;

                String documentPath = findDocumentPathByEmail(userId, db, collectionName);


                FirebaseFirestore db = FirebaseFirestore.getInstance();

                DocumentReference docRef = db.collection("Vendor").document(documentPath);

                Map<String, Object> data = new HashMap<>();
                data.put("Full Name", name);
                data.put("age", age);
                data.put("resume link", dl);
                data.put("Profile picture Link", k);

                docRef.set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(Login.this, "Candidate profile created successfully", Toast.LENGTH_SHORT).show();



                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(Login.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        });



            }
        });












    }









    // to add the picture on the image view ............
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST_ID && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            ProfilePic.setImageURI(mImageUri);

        }
        if (requestCode == FILE_REQUEST_ID && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mFileUri = data.getData();
            fileNameTextView.setText("Your File Added");

        }

    }








    //  this function find the database in Firestore, using the email. which is saved in UserRepository
    // and using this function i got the path, using that path i can add the extra data on the same database
    // where the signup data is already added. we will merge those

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