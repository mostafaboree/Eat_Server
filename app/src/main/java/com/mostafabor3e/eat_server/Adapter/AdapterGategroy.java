package com.mostafabor3e.eat_server.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.mostafabor3e.eat_server.Interface.SetonClickLisener;
import com.mostafabor3e.eat_server.Model.Gategroy;
import com.mostafabor3e.eat_server.R;
import com.mostafabor3e.eat_server.ui.MainActivity;

import java.util.List;

public class AdapterGategroy extends RecyclerView.Adapter<AdapterGategroy.HolderGategory>{
    private List<Gategroy>gategroys;
    private Context context;
    private SetonClickLisener setonClickLisener;
    private View.OnCreateContextMenuListener onCreateContextMenuListener;


    public AdapterGategroy(List<Gategroy> gategroys, Context context, SetonClickLisener setonClickLisener) {
        this.gategroys = gategroys;
        this.context = context;
        this.setonClickLisener = setonClickLisener;
        //this.onCreateContextMenuListener = onCreateContextMenuListener;
    }

    @NonNull
    @Override
    public HolderGategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_gategroy,null,false);
        return new HolderGategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGategory holder, final int position) {
        Gategroy gategroy=gategroys.get(position);
        holder.name.setText(gategroy.getName().toString());
        Glide.with(context).load(gategroy.getImage()).into(holder.imageView);




    }
    public void getitem(int position){
        Gategroy gategroy=gategroys.get(position);
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
           continer.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   setonClickLisener.onclick(view,getAdapterPosition(),false);
               }
           });

        }




        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select the action");
           // contextMenu.setHeaderIcon(R.drawable.ic_baseline_remove_circle_outline_24);

            contextMenu.add(0,0,this.getAdapterPosition(),"update");
            contextMenu.add(0,1,this.getAdapterPosition(),"delete");


        }


    }
}
