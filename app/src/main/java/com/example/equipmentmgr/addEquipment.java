package com.example.equipmentmgr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addEquipment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText eName,modNo,partNo,serialNo,dateET;
    Spinner freqSpin;
    Button addBTN;
    String frequency;
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);

        eName = findViewById(R.id.eNameET);
        modNo = findViewById(R.id.modNoET);
        partNo = findViewById(R.id.prtNoET);
        serialNo = findViewById(R.id.serNoET);
        dateET = findViewById(R.id.addedDateET);

        addBTN = findViewById(R.id.addBtn);

        freqSpin = findViewById(R.id.frequencySpin);
        frequency = "";
        //SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpin.setAdapter(adapter);

        freqSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                frequency  = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //DatePicker
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment DatePicker = new datePickerFragment();
                DatePicker.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData(serialNo.getText().toString(), eName.getText().toString());
            }
        });

    }

    private void insertData(String serNo, String name){

        //CHECKING
        if(eName.getText().toString().equals("") ||
                modNo.getText().toString().equals("") ||
                partNo.getText().toString().equals("") ||
                serialNo.getText().toString().equals("") ||
                dateET.getText().toString().equals("") ||
                frequency.equals("Select Frequency")){

            Toast.makeText(getApplicationContext(), "Please Fill Details", Toast.LENGTH_SHORT).show();

        }
        else {

            FirebaseDatabase.getInstance().getReference().child("Equipment").orderByChild("serNo").equalTo(serNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot1) {

                    FirebaseDatabase.getInstance().getReference().child("Equipment").orderByChild("eqName").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {

                            System.out.println(snapshot1.exists());
                            System.out.println(snapshot2.exists());

                            if(snapshot1.exists() && snapshot2.exists()){
                                Toast.makeText(getApplicationContext(), "This Item is Already exists!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                DatabaseReference push = FirebaseDatabase.getInstance().getReference().child("Equipment").push();
                                String ID = push.getKey();

                                Map<String,Object> map = new HashMap<>();
                                map.put("eqName", eName.getText().toString());
                                map.put("modNo", modNo.getText().toString());
                                map.put("prtNo", partNo.getText().toString());
                                map.put("serNo", serialNo.getText().toString());
                                map.put("freq", frequency);
                                map.put("addedDate", dateET.getText().toString());
                                map.put("ID", ID);

                                push
                                        .setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "Equipment Added!", Toast.LENGTH_SHORT).show();
                                                clear();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Cannot Add Equipment!", Toast.LENGTH_SHORT).show();
                                    }
                                });
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

    }

    private void clear() {
        eName.setText("");
        modNo.setText("");
        partNo.setText("");
        serialNo.setText("");
        dateET.setText("");
        freqSpin.setSelection(0);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        dateET.setText(date);
    }
}