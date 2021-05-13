package com.example.qrcode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcode.gameManager.Resultats;

import java.util.List;

public class ResultatRecyclerAdapter extends RecyclerView.Adapter<ResultatRecyclerAdapter.MyViewHolder> {
    private List<Resultats> resultats;

    public ResultatRecyclerAdapter(List<Resultats> resultats){ this.resultats = resultats; }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_resultat, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String playerName = resultats.get(position).getPlayerName();
        String playerTime = resultats.get(position).getPlayerTime().toString();
        holder.textView_playerName.setText(playerName);
        holder.textView_playerTime.setText(playerTime);
    }

    @Override
    public int getItemCount() {
        return resultats.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_playerName;
        TextView textView_playerTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_playerName = itemView.findViewById(R.id.textView_cardResultat_displayName);
            textView_playerTime = itemView.findViewById(R.id.textView_cardResultat_time);
        }
    }
}
