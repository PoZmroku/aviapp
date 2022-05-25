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

public class Delete_Flight extends AppCompatActivity {

    private EditText d_d,d_m,d_y,flight;
    private Button delete;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference flightRef;
    private DocumentReference bankRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete__flight);

        d_d = findViewById(R.id.d_day);
        d_m = findViewById(R.id.name_delete);
        d_y = findViewById(R.id.d_year);
        flight = findViewById(R.id.g_booking_id);

        delete = findViewById(R.id.delete_ticket);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day,month,year,flight_int;
                try
                {
                    day = Integer.parseInt(d_d.getText().toString().trim());
                    month = Integer.parseInt(d_m.getText().toString().trim());
                    year = Integer.parseInt(d_y.getText().toString().trim());
                    Integer.parseInt(flight.getText().toString().trim());
                    flightRef = db.collection(day+"."+month+"."+year).document(flight.getText().toString().trim());
                    flightRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists())
                                {
                                    int price = Integer.parseInt(document.getString("price"));
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("status","Cancelled");
                                    flightRef.update(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>()
                                        {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                Toast.makeText(Delete_Flight.this,"Flight Successfully Deleted.",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    int c = 0;
                                    for(int i=1;i<=20;i++)
                                        if(!document.getString(i+"").equals("0"))
                                            c++;
                                    Log.d("Price",price+"");
                                    int change_price = c * price;

                                }
                                else
                                {
                                    Toast.makeText(Delete_Flight.this,"No Such Flight Exist",Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.d("Failed Deleting Flight", "Failed with: ", task.getException());
                            }
                        }
                    });
                }
                catch(Exception e)
                {
                    Toast.makeText(Delete_Flight.this,"Enter Data Correctly",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
