package com.example.qrcode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcode.gameManager.QRCodeInfo;

import java.util.List;

public class QRCodeRecyclerAdapter extends RecyclerView.Adapter<QRCodeRecyclerAdapter.MyViewHolder>{
    private List<QRCodeInfo> QRCodesInfo;
    String TAG = "QRCodeRecyclerAdapter";
    QRCodeAdapterInterface qrCodeAdapterInterface;
    public interface QRCodeAdapterInterface {
        void editQRCode(int position);
        void deleteQRCode(int position);
    }

    public QRCodeRecyclerAdapter(List<QRCodeInfo> QRCodesInfo, QRCodeAdapterInterface qrCodeAdapterInterface) {this.QRCodesInfo = QRCodesInfo; this.qrCodeAdapterInterface = qrCodeAdapterInterface;}

    @NonNull
    @Override
    public QRCodeRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_qrcode, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QRCodeRecyclerAdapter.MyViewHolder holder, int position) {
        QRCodeInfo qrCodeInfoBind = QRCodesInfo.get(position);
        holder.position = position;
        holder.qrCodeAdapterInterface = qrCodeAdapterInterface;
        holder.textView_QRCodeId.setText(qrCodeInfoBind.getQrCode());
        holder.textView_title.setText(qrCodeInfoBind.getTitle());
        holder.btnEditQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCodeAdapterInterface.editQRCode(position);
            }
        });
    }

    @Override
    public int getItemCount() {return QRCodesInfo.size();}

    public class MyViewHolder extends RecyclerView.ViewHolder {
        int position;
        QRCodeAdapterInterface qrCodeAdapterInterface;
        ImageView btnEditQRCode, btnDeleteQRCode;
        TextView textView_title, textView_QRCodeId;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEditQRCode = itemView.findViewById(R.id.imageView_cardQRCode_edit);
            btnDeleteQRCode = itemView.findViewById(R.id.imageView_cardQRCode_delete);
            textView_title = itemView.findViewById(R.id.textView_cardQRCode_title);
            textView_QRCodeId = itemView.findViewById(R.id.textView_cardQRCode_QRCodeId);
            btnEditQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   qrCodeAdapterInterface.editQRCode(position);
                }
            });

            btnDeleteQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qrCodeAdapterInterface.deleteQRCode(position);
                }
            });
        }

    }
}


