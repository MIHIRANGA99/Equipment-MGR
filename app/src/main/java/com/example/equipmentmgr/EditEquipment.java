package com.example.equipmentmgr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditEquipment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText eName,modNo,partNo,serialNo,dateET;
    Spinner freqSpin;
    Button updateBTN;
    String frequency;
    Calendar c;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_equipment);

        Intent i = getIntent();
        String ID = i.getStringExtra("Eq");

        eName = findViewById(R.id.eNameETUPDATE);
        modNo = findViewById(R.id.modNoETUPDATE);
        partNo = findViewById(R.id.prtNoETUPDATE);
        serialNo = findViewById(R.id.serNoETUPDATE);
        dateET = findViewById(R.id.addedDateETUPDATE);
        freqSpin = findViewById(R.id.frequencySpinUPDATE);
        updateBTN = findViewById(R.id.updateBtnUPDATE);

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

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Equipment").child(ID);

        //setting texts
        database.child("eqName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                eName.setText(String.valueOf(task.getResult().getValue()));
            }
        });

        database.child("modNo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                modNo.setText(String.valueOf(task.getResult().getValue()));
            }
        });

        database.child("prtNo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                partNo.setText(String.valueOf(task.getResult().getValue()));
            }
        });

        database.child("serNo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                serialNo.setText(String.valueOf(task.getResult().getValue()));
            }
        });

        database.child("addedDate").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                dateET.setText(String.valueOf(task.getResult().getValue()));
            }
        });

        database.child("freq").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (String.valueOf(task.getResult().getValue()).equals("Annual")){
                    freqSpin.setSelection(1);
                }
                else if (String.valueOf(task.getResult().getValue()).equals("6 months")){
                    freqSpin.setSelection(2);
                }
                else if (String.valueOf(task.getResult().getValue()).equals("3 months")){
                    freqSpin.setSelection(3);
                }
                else {
                }
            }
        });



        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData(ID);
            }
        });
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

    private void insertData(String key){

        Map<String,Object> map = new HashMap<>();
        map.put("eqName", eName.getText().toString());
        map.put("modNo", modNo.getText().toString());
        map.put("prtNo", partNo.getText().toString());
        map.put("serNo", serialNo.getText().toString());
        map.put("freq", frequency);
        map.put("addedDate", dateET.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Equipment").child(key).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Equipment Updated!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Cannot Update Equipment!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}