package com.polije.sem3.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;
import com.polije.sem3.R;
import com.polije.sem3.model.TiketModel;

import java.util.List;

public class TiketModelAdapter extends RecyclerView.Adapter<TiketModelAdapter.TiketViewHolder> {
    private Context context;
    private List<TiketModel> tiketList;

    public TiketModelAdapter(Context context, List<TiketModel> tiketList) {
        this.context = context;
        this.tiketList = tiketList;
    }

    // Update status tiket setelah berhasil
    public void updateTiketStatus(TiketModel tiket) {
        for (int i = 0; i < tiketList.size(); i++) {
            // Membandingkan dengan tipe int
            if (tiketList.get(i).getId_tiket() == tiket.getId_tiket()) {
                tiketList.set(i, tiket);  // Perbarui item yang sesuai
                notifyItemChanged(i);  // Memberitahukan adapter bahwa item tersebut sudah diubah
                break;
            }
        }
    }


    @Override
    public TiketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_row_tiket, parent, false);
        return new TiketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TiketViewHolder holder, int position) {
        TiketModel tiket = tiketList.get(position);
        holder.tiketTitle.setText(tiket.getNama_wisata());
        holder.tiketUser.setText("Nama : " + tiket.getNama_pemesan());
        holder.tiketJumlah.setText("Pengunjung : " + tiket.getJumlah());
        holder.tiketTanggal.setText("Tanggal : " + tiket.getTanggal());
        holder.tiketStatus.setText("Status : " + tiket.getStatus());
        /*try {
            JSONObject qrData = new JSONObject();
            qrData.put("nama_wisata", tiketTitle);
            qrData.put("nama_pemesan", tiketUser);
            qrData.put("jumlah_pengunjung", tiketJumlah);
            qrData.put("tanggal", tiketTanggal);
            qrData.put("status", tiketStatus);

            // Generate QR code dari JSON data
            String dataQR = qrData.toString(); // Mengubah JSON menjadi string
            Bitmap bitmap = generateQRCode(dataQR);
            holder.gambarQR.setImageBitmap(bitmap); // Tampilkan QR code di ImageView
        } catch (JSONException | WriterException e) {
            e.printStackTrace();
        }*/

        // Set any other views or actions (like button listeners) if necessary
    }

    @Override
    public int getItemCount() {
        return tiketList.size();
    }

    public void updateTiketList(List<TiketModel> newTiketList) {
        this.tiketList = newTiketList;
        notifyDataSetChanged();
    }

    public class TiketViewHolder extends RecyclerView.ViewHolder {
        TextView tiketTitle, tiketUser, tiketJumlah, tiketTanggal, tiketStatus;
        CardView cardView;
        ImageView gambarQR;

        public TiketViewHolder(View itemView) {
            super(itemView);
            tiketTitle = itemView.findViewById(R.id.tiketTitle);
            tiketUser = itemView.findViewById(R.id.tiketuser);
            tiketJumlah = itemView.findViewById(R.id.tiketjumlah);
            tiketTanggal = itemView.findViewById(R.id.tikettanggal);
            tiketStatus = itemView.findViewById(R.id.tiketstatus);
            cardView = itemView.findViewById(R.id.cardview);
            gambarQR = itemView.findViewById(R.id.gambarQR);
            try {
                JSONObject qrData = new JSONObject();
                qrData.put("nama_wisata", tiketTitle);
                qrData.put("nama_pemesan", tiketUser);
                qrData.put("jumlah_pengunjung", tiketJumlah);
                qrData.put("tanggal", tiketTanggal);
                qrData.put("status", tiketStatus);

                // Generate QR code dari JSON data
                String dataQR = qrData.toString(); // Mengubah JSON menjadi string
                Bitmap bitmap = generateQRCode(dataQR);
                gambarQR.setImageBitmap(bitmap); // Tampilkan QR code di ImageView
            } catch (JSONException | WriterException e) {
                e.printStackTrace();
            }
        }

        public void bindData(TiketModel tiket) {
            // Set TextView
            tiketTitle.setText(tiket.getNama_wisata());
            tiketUser.setText("Nama: " + tiket.getNama_pemesan());
            tiketJumlah.setText("Pengunjung: " + tiket.getJumlah());
            tiketTanggal.setText("Tanggal: " + tiket.getTanggal());
            tiketStatus.setText("Status: " + tiket.getStatus());

            // Membuat JSON data dari detail tiket
            try {
                JSONObject qrData = new JSONObject();
                qrData.put("nama_wisata", tiket.getNama_wisata());
                qrData.put("nama_pemesan", tiket.getNama_pemesan());
                qrData.put("jumlah_pengunjung", tiket.getJumlah());
                qrData.put("tanggal", tiket.getTanggal());
                qrData.put("status", tiket.getStatus());

                // Generate QR code dari JSON data
                String dataQR = qrData.toString(); // Mengubah JSON menjadi string
                Bitmap bitmap = generateQRCode(dataQR);
                gambarQR.setImageBitmap(bitmap); // Tampilkan QR code di ImageView
            } catch (JSONException | WriterException e) {
                e.printStackTrace();
            }
        }
        private Bitmap generateQRCode(String data) throws WriterException {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            int size = 120; // Ukuran QR code
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        }
}}
