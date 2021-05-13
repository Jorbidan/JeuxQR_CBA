package com.example.qrcode;

import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcode.gameManager.GameService;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ImageRecyclerAdapter  extends RecyclerView.Adapter<ImageRecyclerAdapter.MyViewHolder>{
    private List<StorageReference> storageReferences;
    private ImageAdapterInterface imageAdapterInterface;
    public ImageRecyclerAdapter(List<StorageReference> storageReferences, ImageAdapterInterface imageAdapterInterface){
        this.storageReferences = storageReferences;
        this.imageAdapterInterface = imageAdapterInterface;
    };

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
        holder.imageAdapterInterface = imageAdapterInterface;
        holder.position = position;
    }

    @Override
    public int getItemCount() {return storageReferences.size();}

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        int position;
        TextView textViewImageName;
        Button btn_chooseImage;
        ImageView imageView_deleteImage;
        ImageAdapterInterface imageAdapterInterface;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewImageName = itemView.findViewById(R.id.textView_cardImageChoose_imageName);
            btn_chooseImage = itemView.findViewById(R.id.btn_cardImageChoose_chooseImage);
            imageView_deleteImage = itemView.findViewById(R.id.imageView_cardImageChoose_deleteImage);
            btn_chooseImage.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                   imageAdapterInterface.imageChosen(position);
                }
            });
            imageView_deleteImage.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {imageAdapterInterface.imageDelete(position);}
            });
        }

    }

    public interface ImageAdapterInterface {
        void imageChosen(int position);
        void imageDelete(int position);
    }
}
