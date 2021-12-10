package com.example.equipmentmgr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText searchET;
    LinearLayout eqListLL;
    RecyclerView eqListRV;
    EquipmentAdapter equipmentAdapter;
    Button addEq,searchBtn;

    @Override
    protected void onStart() {
        super.onStart();
        equipmentAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addEq = findViewById(R.id.addEqBtn);
        eqListRV = findViewById(R.id.equipmentRV);
        searchET = findViewById(R.id.searchET);
        searchBtn = findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTxt(searchET.getText().toString());
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        eqListRV.setLayoutManager(manager);

        FirebaseRecyclerOptions<EquipmentModel> options =
                new FirebaseRecyclerOptions.Builder<EquipmentModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Equipment"), EquipmentModel.class)
                    .build();

        equipmentAdapter = new EquipmentAdapter(options);
        eqListRV.setAdapter(equipmentAdapter);

        addEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, addEquipment.class);
                startActivity(intent);
            }
        });
    }

    public void searchTxt(String str){
        String upper = str.toUpperCase();
        String lower = str.toLowerCase();
        FirebaseRecyclerOptions<EquipmentModel> options =
                new FirebaseRecyclerOptions.Builder<EquipmentModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Equipment").orderByChild("eqName").startAt(upper).endAt(lower+"~"), EquipmentModel.class)
                        .build();

        EquipmentAdapter equipmentAdapter = new EquipmentAdapter(options);
        equipmentAdapter.startListening();
        eqListRV.setAdapter(equipmentAdapter);
    }


}