package com.example.qrcode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerRecyclerAdapter extends RecyclerView.Adapter<PlayerRecyclerAdapter.MyViewHolder> {
    private List<String> players;

    public PlayerRecyclerAdapter(List<String> players){
        this.players = players;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_players_lobby, parent, false);//touch me
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String stringToBind = players.get(position);
        holder.textView_playerName.setText(stringToBind);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_playerName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_playerName = itemView.findViewById(R.id.textView_card_playername);
        }
    }
}
