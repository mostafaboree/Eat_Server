package com.mostafabor3e.eat_server.viewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mostafabor3e.eat_server.Model.Gategroy;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
   // Context context;


    private MutableLiveData<List<Gategroy>> gategroyes;
    FirebaseDatabase firebaseDatabase;
    List<Gategroy> gategroyList=new ArrayList<>();
    DatabaseReference databaseReference;
   // String key;

    SharedPreferences.Editor editor;
    //SharedPreferences preferences=context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
   //String key =preferences.getString("key","none");

    public HomeViewModel() {

        gategroyes = new MutableLiveData<>();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Category");
        gategroyList=new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                gategroyList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Gategroy gategroy=dataSnapshot.getValue(Gategroy.class);

                    assert gategroy != null;
                    gategroy.setKey(dataSnapshot.getKey());
                    gategroyList.add(gategroy);


                }
                gategroyes.setValue(gategroyList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //gategroyes.setValue(gategroyList);

    }

    public LiveData<List<Gategroy>> getGategroy() {
        return gategroyes;
    }




}