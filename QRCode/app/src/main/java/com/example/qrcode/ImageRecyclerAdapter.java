package com.example.qrcode;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcode.gameManager.GameService;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ImageRecyclerAdapter  extends RecyclerView.Adapter<ImageRecyclerAdapter.MyViewHolder>{
    private List<StorageReference> storageReferences;

    public interface ImageAdapterInterface {
        void imageChosen(Bitmap chosenImage, String imageRef);
    }
    public ImageRecyclerAdapter(List<StorageReference> storageReferences){this.storageReferences = storageReferences;};

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.card_image_choose, parent, false);
       MyViewHolder viewholder = new MyViewHolder(view);
       return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StorageReference storageReferenceBind = storageReferences.get(position);
        holder.textViewImageName.setText(storageReferenceBind.getName());

    }

    @Override
    public int getItemCount() {return storageReferences.size();}

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewImageName;
        Button btn_chooseImage, btn_deleteImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewImageName = itemView.findViewById(R.id.textView_cardImageChoose_imageName);
            btn_chooseImage = itemView.findViewById(R.id.btn_cardImageChoose_chooseImage);
            btn_deleteImage = itemView.findViewById(R.id.btn_cardImageChoose_deleteImage);
        }
    }
}
