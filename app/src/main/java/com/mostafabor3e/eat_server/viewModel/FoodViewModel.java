package com.mostafabor3e.eat_server.viewModel;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mostafabor3e.eat_server.Model.Food;
import com.mostafabor3e.eat_server.Model.Gategroy;

import java.util.ArrayList;
import java.util.List;

public class FoodViewModel extends ViewModel {
    private MutableLiveData<List<Food>> lMLDF;
    FirebaseDatabase firebaseDatabase;
    List<Food> FoodList;
    DatabaseReference databaseReference;
    SharedPreferences.Editor editor;
    public void HomeViewModel() {
        lMLDF = new MutableLiveData<>();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Foods");
        FoodList=new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                FoodList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Food food=dataSnapshot.getValue(Food.class);

                    food.setId(Integer.parseInt(dataSnapshot.getKey()));
                    FoodList.add(food);


                }
                lMLDF.setValue(FoodList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FoodList.clear();


            }
        });
        //gategroyes.setValue(gategroyList);

    }

    public LiveData<List<Food>> getFood() {
        return lMLDF;
    }

}
