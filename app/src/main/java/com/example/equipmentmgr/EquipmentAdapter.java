package com.example.equipmentmgr;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EquipmentAdapter extends FirebaseRecyclerAdapter<EquipmentModel,EquipmentAdapter.eqViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public EquipmentAdapter(@NonNull FirebaseRecyclerOptions<EquipmentModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull eqViewHolder holder, int position, @NonNull EquipmentModel model) {

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

        String date[] = model.getAddedDate().split(", ");
        String dayOfMonth = date[0];
        String monthWithDate = date[1];
        String month[] = monthWithDate.split(" ");


        try {
            holder.exactMonth = month[1];
            holder.exactDay = month[0];
            holder.year = month[2];
        }catch (Exception e){
            holder.exactMonth = month[0];
            holder.exactDay = month[1];
            holder.year = date[2];
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(holder.year));
        cal.set(Calendar.MONTH, months.get(holder.exactMonth));
        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(holder.exactDay));

        if(model.getFreq().equals("Annual")){

            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);

            holder.expireDateTV.setText(DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime()));
        }
        else if (model.getFreq().equals("6 months")){

            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 6);
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);

            holder.expireDateTV.setText(DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime()));
        }
        else if (model.getFreq().equals("3 months")){

            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 3);
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);

            holder.expireDateTV.setText(DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime()));
        }
        else {
            holder.expireDateTV.setText("Cannot Calculate");
        }

        Calendar current = Calendar.getInstance();
        String calcur = DateFormat.getDateInstance(DateFormat.FULL).format(current.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 14);
        System.out.println(DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime()));

        Date calTime = cal.getTime();
        Date c = current.getTime();
        int calInt = (int) (calTime.getTime()/1000);
        int curInt = (int) (c.getTime()/1000);

        if(curInt > calInt){
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 7);
            Date calTime1 = cal.getTime();
            int calInt1 = (int) (calTime1.getTime()/1000);

            holder.indicator.setBackgroundColor(Color.rgb(165, 124, 0));

            if(curInt > calInt1){
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 7);
                Date calTime2 = cal.getTime();
                int calInt2 = (int) (calTime2.getTime()/1000);

                holder.indicator.setBackgroundColor(Color.rgb(165, 0, 0));
                if(curInt > calInt2){
                    holder.itemCard.getBackground().setAlpha(90);
                }
            }
        }
        else {
            holder.indicator.setBackgroundColor(Color.rgb(44, 44, 44));
        }

        holder.eqNameTV.setText(model.getEqName());
        holder.modNumTV.setText(model.getModNo());
        holder.prtNumTV.setText(model.getPrtNo());
        holder.serNumTV.setText(model.getSerNo());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.eqNameTV.getContext());
                builder.setTitle("Are you sure?")
                        .setMessage("This will remove this item permanently!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("Equipment").child(model.getID()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(holder.eqNameTV.getContext(), model.getEqName() + " successfully removed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.eqNameTV.getContext(), "Equipment Deletion unsuccessful!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.eqNameTV.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditEquipment.class);
                intent.putExtra("Eq", model.getID().toString());
                view.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public eqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new eqViewHolder(view);
    }

    class eqViewHolder extends RecyclerView.ViewHolder{

        TextView eqNameTV, modNumTV, prtNumTV, serNumTV, expireDateTV, indicator;
        ImageView editBtn,deleteBtn;
        CardView itemCard;
        String exactMonth;
        String exactDay;
        String year;

        public eqViewHolder(@NonNull View itemView) {
            super(itemView);

            eqNameTV = (TextView) itemView.findViewById(R.id.equipNameTV);
            modNumTV = (TextView) itemView.findViewById(R.id.modelNumTV);
            prtNumTV = (TextView) itemView.findViewById(R.id.partNumTV);
            serNumTV = (TextView) itemView.findViewById(R.id.serialNumTV);
            expireDateTV = (TextView) itemView.findViewById(R.id.expireDateTV);
            editBtn = (ImageView) itemView.findViewById(R.id.btnEdit);
            deleteBtn = (ImageView) itemView.findViewById(R.id.btnDelete);
            indicator = (TextView) itemView.findViewById(R.id.indicator);
            itemCard = (CardView) itemView.findViewById(R.id.itemCardView);



        }
    }
}
