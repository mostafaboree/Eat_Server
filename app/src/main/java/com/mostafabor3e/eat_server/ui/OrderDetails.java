package com.mostafabor3e.eat_server.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mostafabor3e.eat_server.Adapter.CartAdapter;
import com.mostafabor3e.eat_server.Model.Food;
import com.mostafabor3e.eat_server.Model.Request;
import com.mostafabor3e.eat_server.R;

import java.util.ArrayList;
import java.util.List;

public class OrderDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Food>orderList;
    CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        orderList=new ArrayList<>();
        recyclerView=findViewById(R.id.rec_order_d);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
        recyclerView.setHasFixedSize(true);

        Intent intent=getIntent();
        String id=intent.getStringExtra("order_id");
        getListOrder(id);

    }
    public void getListOrder(String order_id){
        DatabaseReference db=FirebaseDatabase.getInstance().getReference("Request").child(order_id);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Request order=snapshot.getValue(Request.class);
                order.getFoods();
                cartAdapter=new CartAdapter(order.getFoods(),getBaseContext());
                recyclerView.setAdapter(cartAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}