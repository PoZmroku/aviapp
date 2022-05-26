package com.awesome.aviapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Delete_Flight_Ticket extends AppCompatActivity {

    private EditText name,booking_id;
    private Button delete_ticket;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference bookingRef;
    DocumentReference bankRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete__flight__ticket);

        name = findViewById(R.id.name_delete);
        booking_id = findViewById(R.id.g_booking_id);

        delete_ticket = findViewById(R.id.delete_ticket);

        delete_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id;
                try
                {
                    id = Integer.parseInt(booking_id.getText().toString().trim());
                    bookingRef = db.collection(booking_id.getText().toString().trim()).document(name.getText().toString().trim());
                    bankRef = db.collection("Bank").document("bank");
                    final Passenger passenger = new Passenger();
                    bookingRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                final DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if (document.getString("status").equals("Cancelled"))
                                        Toast.makeText(Delete_Flight_Ticket.this, "Ticket Already Cancelled", Toast.LENGTH_LONG).show();
                                    else {

                                        final Bank bank = new Bank();
                                        bank.setNo2(document.getString("price"));
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("status", "Cancelled");
                                        bookingRef.update(map)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Delete_Flight_Ticket.this, "Ticket Successfully Cancelled.", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                        bankRef.get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        bank.setNo1(documentSnapshot.getString("amount"));
                                                        String new_amount = bank.sub();
                                                        Map<String, Object> map1 = new HashMap<>();
                                                        map1.put("amount", new_amount);
                                                        bankRef.update(map1);
                                                    }
                                                });

                                        bookingRef.get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        String date = "" + documentSnapshot.getLong("day") + "." + documentSnapshot.getLong("month") + "." + documentSnapshot.getLong("year");
                                                        String flight = "" + documentSnapshot.getString("flight");
                                                        String seat = document.getString("seat");
                                                        Map<String, Object> map2 = new HashMap<>();
                                                        map2.put(seat, "0");
                                                        db.collection(date).document(flight).update(map2);
                                                    }
                                                });


                                    }
                                }
                                else
                                {
                                    Toast.makeText(Delete_Flight_Ticket.this,"No Such Booking Exist",Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Log.d("Failed Deleting Booking", "Failed with: ", task.getException());
                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(Delete_Flight_Ticket.this,"Enter Data Correctly",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
