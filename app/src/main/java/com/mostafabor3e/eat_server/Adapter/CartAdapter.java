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
import com.mostafabor3e.eat_server.Model.Food;
import com.mostafabor3e.eat_server.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.AllMenuViewHolder> {
    List<Food>foodMList;
    Context context;

    public CartAdapter(List<Food> foodMList, Context context) {
        this.foodMList = foodMList;
        this.context = context;
    }

    @NonNull
    @Override
    public AllMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_details,null,false);

        return new AllMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllMenuViewHolder holder, int position) {
        Food food=foodMList.get(position);
        holder.allMenuName.setText(food.getName());
        holder.allMenuPrice.setText(food.getPrice()+"$");
        //holder.allMenuNote.setText(food.getDescription());
        Glide.with(context).load(food.getImage()).into(holder.allMenuImage);


    }

    @Override
    public int getItemCount() {
        return foodMList.size();
    }

    public static class AllMenuViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView allMenuName, allMenuNote, allMenuRating, allMenuTime, allMenuCharges, allMenuPrice;
        ImageView allMenuImage;
        CardView cardView;

        public AllMenuViewHolder(@NonNull View itemView) {
            super(itemView);

            allMenuName = itemView.findViewById(R.id.all_menu_name);
            allMenuPrice = itemView.findViewById(R.id.all_menu_price);
            allMenuImage = itemView.findViewById(R.id.all_menu_image);
            cardView=itemView.findViewById(R.id.order_card);
            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("select action");
            contextMenu.add(10,101,getAdapterPosition(),"delete");


        }
    }

}
