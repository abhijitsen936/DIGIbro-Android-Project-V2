package com.example.digibro;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
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
                FirebaseFirestore.getInstance().collection("Vendor").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        namelist.clear();
                        for(DocumentSnapshot s: value){
                            namelist.add(s.getString("Full Name") + ":"+ "  " + s.getString("name") +"   " +"age-" + s.getString("age") );

                        }
                        ArrayAdapter adapter = new ArrayAdapter<String>(CandidateList.this, android.R.layout.simple_selectable_list_item, namelist);
                    adapter.notifyDataSetChanged();
                    list.setAdapter(adapter);

                    }
                });
            }
        });

    }
}