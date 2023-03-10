package com.example.digibro;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CandidateList extends AppCompatActivity {

    private Button show;
    private ListView list;
    private List<String> namelist= new ArrayList<>();
    private TextView Rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_list);

        show= findViewById(R.id.button2);
        list=findViewById(R.id.list);
        Rid=findViewById(R.id.textView5);

        UserRepository userRepository = UserRepository.getInstance();
        String userEmail = userRepository.getUserEmail();

        Rid.setText("welcome" +"    " + userEmail);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("Vendor")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        final QuerySnapshot valueFinal = value;
                        namelist.clear();
                        for(DocumentSnapshot s: valueFinal){
                            namelist.add("Name-" + " " + s.getString("Full Name") + "\n"+"Email-" + "  " + s.getString("name")
                                    +"     " +"age-" + s.getString("age") );

                        }
                        ArrayAdapter adapter = new ArrayAdapter<>(CandidateList.this,
                                android.R.layout.simple_selectable_list_item, namelist);
                        adapter.notifyDataSetChanged();
                        list.setAdapter(adapter);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // Get the email ID of the clicked candidate
                                DocumentSnapshot selectedDocument = valueFinal.getDocuments().get(position);
                                String email = selectedDocument.getString("name");

                                // Create an Intent object to launch the CandidateDetails activity
                                Intent intent = new Intent(CandidateList.this, ClickedItem.class);

                                // Put the email ID as an extra in the Intent object
                                intent.putExtra("email", email);

                                // Start the CandidateDetails activity
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }
}
