package com.example.equipmentmgr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText searchET;
    LinearLayout eqListLL;
    RecyclerView eqListRV;
    EquipmentAdapter equipmentAdapter;
    Button addEq,searchBtn;
    Spinner filterSpin;

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
        filterSpin = findViewById(R.id.filterSpin);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTxt(searchET.getText().toString());
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        eqListRV.setLayoutManager(manager);

        FirebaseDatabase.getInstance().getReference().child("Equipment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    getCalender(snapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        //SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.filter, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpin.setAdapter(adapter);

        filterSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    eqListRV.setAdapter(equipmentAdapter);
                }
                else if (i == 1){
                    filterTxtbool("true");
                }
                else if (i == 2){
                    filterTxt("Alpha");
                }
                else if (i == 3){
                    filterTxt("Yellow");
                }
                else if (i == 4){
                    filterTxt("Red");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getCalender(String id) {

        FirebaseDatabase.getInstance().getReference().child("Equipment").child(id).child("addedDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String exactMonth, exactDay, year;
                String aDate = snapshot.getValue().toString();

                HashMap<String, Integer> months = new HashMap<String, Integer>();
                months.put("January", 0);
                months.put("February", 1);
                months.put("March", 2);
                months.put("April", 3);
                months.put("May", 4);
                months.put("June", 5);
                months.put("July", 6);
                months.put("August", 7);
                months.put("September", 8);
                months.put("October", 9);
                months.put("November", 10);
                months.put("December", 11);

                String date[] = aDate.split(", ");
                String dayOfMonth = date[0];
                String monthWithDate = date[1];
                String month[] = monthWithDate.split(" ");


                try {
                    exactMonth = month[1];
                    exactDay = month[0];
                    year = month[2];
                }catch (Exception e){
                    exactMonth = month[0];
                    exactDay = month[1];
                    year = date[2];
                }

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(year));
                cal.set(Calendar.MONTH, months.get(exactMonth));
                cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(exactDay));

                FirebaseDatabase.getInstance().getReference().child("Equipment").child(id).child("freq").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String color;

                        if(snapshot.getValue().equals("Annual")){

                            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
                        }
                        else if (snapshot.getValue().equals("6 months")){

                            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 6);
                            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
                        }
                        else if (snapshot.getValue().equals("3 months")){

                            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 3);
                            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
                        }

                        Calendar current = Calendar.getInstance();
                        String calcur = DateFormat.getDateInstance(DateFormat.FULL).format(current.getTime());

                        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 14);

                        Date calTime = cal.getTime();
                        Date c = current.getTime();
                        int calInt = (int) (calTime.getTime()/1000);
                        int curInt = (int) (c.getTime()/1000);

                        //set alpha initially
                        color = "normal";

                        if(curInt > calInt){
                            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 7);
                            Date calTime1 = cal.getTime();
                            int calInt1 = (int) (calTime1.getTime()/1000);

                            color = "Yellow";

                            if(curInt > calInt1){
                                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 7);
                                Date calTime2 = cal.getTime();
                                int calInt2 = (int) (calTime2.getTime()/1000);

                                color = "Red";

                                if(curInt > calInt2){
                                    color = "Alpha";
                                }
                            }
                        }
                        if(color.equals("Alpha")){
                            FirebaseDatabase.getInstance().getReference().child("Equipment").child(id).child("color").setValue("Alpha");
                        }
                        else if (color.equals("Red")){
                            FirebaseDatabase.getInstance().getReference().child("Equipment").child(id).child("color").setValue("Red");
                        }
                        else if (color.equals("Yellow")){
                            FirebaseDatabase.getInstance().getReference().child("Equipment").child(id).child("color").setValue("Yellow");
                        }
                        else if (color.equals("Normal")){
                            FirebaseDatabase.getInstance().getReference().child("Equipment").child(id).child("color").setValue("Normal");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void searchTxt(String str){
        String upper = str.toUpperCase();
        String lower = str.toLowerCase();
        FirebaseRecyclerOptions<EquipmentModel> options =
                new FirebaseRecyclerOptions.Builder<EquipmentModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Equipment").orderByChild("eqName").startAt(upper).endAt(lower+"/uf8ff"), EquipmentModel.class)
                        .build();

        EquipmentAdapter equipmentAdapter = new EquipmentAdapter(options);
        equipmentAdapter.startListening();
        eqListRV.setAdapter(equipmentAdapter);
    }

    public void filterTxt(String str){
        FirebaseRecyclerOptions<EquipmentModel> options =
                new FirebaseRecyclerOptions.Builder<EquipmentModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Equipment").orderByChild("color").startAt(str).endAt(str+"/uf8ff"), EquipmentModel.class)
                        .build();

        EquipmentAdapter equipmentAdapter = new EquipmentAdapter(options);
        equipmentAdapter.startListening();
        eqListRV.setAdapter(equipmentAdapter);
    }

    public void filterTxtbool(String str){
        FirebaseRecyclerOptions<EquipmentModel> options =
                new FirebaseRecyclerOptions.Builder<EquipmentModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Equipment").orderByChild("STC").startAt(str).endAt(str+"/uf8ff"), EquipmentModel.class)
                        .build();

        EquipmentAdapter equipmentAdapter = new EquipmentAdapter(options);
        equipmentAdapter.startListening();
        eqListRV.setAdapter(equipmentAdapter);
    }


}