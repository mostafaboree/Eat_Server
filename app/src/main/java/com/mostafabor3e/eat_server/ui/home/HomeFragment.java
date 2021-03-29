package com.mostafabor3e.eat_server.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mostafabor3e.eat_server.Adapter.AdapterGategroy;
import com.mostafabor3e.eat_server.Interface.SetonClickLisener;
import com.mostafabor3e.eat_server.Model.Gategroy;
import com.mostafabor3e.eat_server.R;
import com.mostafabor3e.eat_server.ui.FoodD;
import com.mostafabor3e.eat_server.viewModel.HomeViewModel;

import java.util.List;

public class HomeFragment extends Fragment  {
    AdapterGategroy adapterGategroy;
    RecyclerView recyclerView;

    private HomeViewModel homeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.rec_get);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        homeViewModel.getGategroy().observe(getViewLifecycleOwner(), new Observer<List<Gategroy>>() {
            @Override
            public void onChanged(final List<Gategroy> gategroys) {
               // Toast.makeText(getContext(), "b"+gategroys.get(1).getName(), Toast.LENGTH_SHORT).show();
                adapterGategroy=new AdapterGategroy(gategroys, getContext(), new SetonClickLisener() {
                    @Override
                    public void onclick(View veiw, int postion, boolean isLongClick) {
                        Intent intent=new Intent(getContext(), FoodD.class);
                        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("key",gategroys.get(postion).getKey());
                        editor.apply();
                        startActivity(intent);
                    }
                });

                recyclerView.setAdapter(adapterGategroy);

            }
        });

        return root;
    }


}