package com.mostafabor3e.eat_server.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.mostafabor3e.eat_server.Interface.SetonClickLisener;
import com.mostafabor3e.eat_server.Model.Request;
import com.mostafabor3e.eat_server.R;

import java.util.List;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.ViewHolderOrder> {
    List<Request>orderes;
    Context context;
    SetonClickLisener setonClickLisener;

    public AdapterOrder(List<Request> orderes, Context context, SetonClickLisener setonClickLisener) {
        this.orderes = orderes;
        this.context = context;
        this.setonClickLisener = setonClickLisener;
    }

    @NonNull
    @Override
    public ViewHolderOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.order_item,null,false);
        return new ViewHolderOrder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderOrder holder, int position) {
        Request order=orderes.get(position);
        holder.status.setText(order.getStute());
        holder.order_id.setText(order.getId());
        holder.phone.setText(order.getPhone());
        holder.address.setText(order.getAddress());


    }

    @Override
    public int getItemCount() {
        return orderes.size();
    }

    public class ViewHolderOrder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener {
        TextView phone,order_id,address,status;
        CardView cardView;
        public ViewHolderOrder(@NonNull View itemView) {
            super(itemView);
            phone=itemView.findViewById(R.id.tv_order_phone);
            address=itemView.findViewById(R.id.tv_order_address);
            order_id=itemView.findViewById(R.id.tv_order_id);
            status=itemView.findViewById(R.id.tv_order_stut);
            cardView=itemView.findViewById(R.id.card_order);
            cardView.setOnCreateContextMenuListener(this);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            setonClickLisener.onclick(view,getAdapterPosition(),false);


        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select The Action");
            contextMenu.add(0,4,this.getAdapterPosition(),"Details");
            contextMenu.add(0,2,this.getAdapterPosition(),"Update");
            contextMenu.add(0,3,this.getAdapterPosition(),"Delete");

        }
    }
}
