package com.example.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcode.ImageManager.ImageFactory;
import com.example.qrcode.ImageManager.ImageService;
import com.example.qrcode.gameManager.GameService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class QRCodeRecyclerAdapter extends RecyclerView.Adapter<QRCodeRecyclerAdapter.MyViewHolder>{
    private List<QRCodeInfo> QRCodesInfo;
    ImageService imageService;
    GameService gameService;
    String TAG = "QRCodeRecyclerAdapter";

    public QRCodeRecyclerAdapter(List<QRCodeInfo> QRCodesInfo) {this.QRCodesInfo = QRCodesInfo;}

    @NonNull
    @Override
    public QRCodeRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_qrcode, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        imageService = ImageFactory.getInstance();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QRCodeRecyclerAdapter.MyViewHolder holder, int position) {
        QRCodeInfo qrCodeInfoBind = QRCodesInfo.get(position);
        holder.qrCodeInfo = qrCodeInfoBind;
    }

    @Override
    public int getItemCount() {return QRCodesInfo.size();}

    public class MyViewHolder extends RecyclerView.ViewHolder {
        QRCodeInfo qrCodeInfo;
        Bitmap image;
        ImageView btnEditQRCode, btnDeleteQRCode;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEditQRCode = itemView.findViewById(R.id.imageView_cardQRCode_edit);
            btnDeleteQRCode = itemView.findViewById(R.id.imageView_cardQRCode_delete);

            btnEditQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchQRCodeImageBeforeEditing();
                }
            });

            btnDeleteQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteQRCode();
                }
            });
        }

        private void fetchQRCodeImageBeforeEditing(){

            //Méthode Synchrone car on à besoins de l'image avant d'édit.
            if(image == null && qrCodeInfo.getImageRef() != null) {
                  imageService.downloadImage(qrCodeInfo.getImageRef()).addOnCompleteListener(new OnCompleteListener<Bitmap>() {
                    @Override
                    public void onComplete(@NonNull Task<Bitmap> task) {
                        if (task.isSuccessful()) {
                            image = task.getResult();
                            editQRCode();
                        }else{
                            Log.d(TAG, "N'a pas pus receuillir l'image");
                        }

                    }
                });
            }else{
                editQRCode();
            }
        }

        private void editQRCode() {
            Intent editQRCodeIntent = new Intent();
            //TODO: Ouvrir l'Activity qui va probablement se nommer addOrEditQRCodeActivity
            // Envoyer qrCodeInfo dans l'Activity
            // Après c'est une gestion facile de l'activité
            // Si on recoit qrCodeInfo, c'est un edit et on lock le TextViewQRCode (ID QRCode)
            // Si il y en a pas, c'est un ajout et on permet le changement du textViewQRCode (ID QRCode)
            // Si on fait un ajout et l'ID est le même que l'un dans la BD (Vérification de +), on empêche l'ajout avec un bloque et un affichage sur la view (text rouge etc.)
        }
        private void deleteQRCode(){
            //TODO: 1. Message de confirmation
            // 2. On supprime de la BD
            // 3. On supprime le View dans l'adapter
        }
    }
}


