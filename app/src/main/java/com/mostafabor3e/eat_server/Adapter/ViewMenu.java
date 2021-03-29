package com.mostafabor3e.eat_server.Adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewMenu extends RecyclerView.ViewHolder implements View.OnLongClickListener {
    public ViewMenu(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}



