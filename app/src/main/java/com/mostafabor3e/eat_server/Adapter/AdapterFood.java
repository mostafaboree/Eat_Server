package com.mostafabor3e.eat_server.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.mostafabor3e.eat_server.Interface.SetonClickLisener;
import com.mostafabor3e.eat_server.Model.Food;
import com.mostafabor3e.eat_server.R;

import java.util.List;

public class AdapterFood extends RecyclerView.Adapter<AdapterFood.HolderGategory> {
    private List<Food>gategroys;
    private Context context;
    private SetonClickLisener setonClickLisener;
    private View.OnCreateContextMenuListener onCreateContextMenuListener;



    public AdapterFood(List<Food> gategroys, Context context) {
        this.gategroys = gategroys;
        this.context = context;
       // this.setonClickLisener = setonClickLisener;
    }

    @NonNull
    @Override
    public HolderGategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_gategroy,null,false);
        return new HolderGategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGategory holder, final int position) {
        Food foodM =gategroys.get(position);
        holder.name.setText(foodM.getName());
        Glide.with(context).load(foodM.getImage()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return  gategroys.size();
    }

    class HolderGategory extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener {
        TextView name;
        ImageView imageView;
        CardView continer;
        public HolderGategory(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tv_geta_item);
            imageView=itemView.findViewById(R.id.iv_gate_item);
            continer=itemView.findViewById(R.id.layout_re);
            continer.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select The Action");
            contextMenu.add(0,2,this.getAdapterPosition(),"Update");
            contextMenu.add(0,3,this.getAdapterPosition(),"Delete");

        }
    }
}
