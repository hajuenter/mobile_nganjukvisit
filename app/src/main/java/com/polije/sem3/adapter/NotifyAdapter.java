package com.polije.sem3.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polije.sem3.R;
import com.polije.sem3.model.NotifyModelNew;

import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {
    private List<NotifyModelNew> notifyList;

    public NotifyAdapter(List<NotifyModelNew> notifyList) {
        this.notifyList = notifyList;
    }

    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_row_notif, parent, false);
        return new NotifyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
        NotifyModelNew notify = notifyList.get(position);
        holder.title.setText(notify.getJudul());
        holder.body.setText(notify.getBodynotif());
    }

    @Override
    public int getItemCount() {
        return notifyList.size();
    }

    public void updateData(List<NotifyModelNew> newData) {
        this.notifyList.clear(); // Hapus data lama
        this.notifyList.addAll(newData); // Tambahkan data baru
        notifyDataSetChanged(); // Beri tahu RecyclerView untuk memperbarui tampilan
    }


    public static class NotifyViewHolder extends RecyclerView.ViewHolder {
        TextView title, body, time;

        public NotifyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notifTitle);
            body = itemView.findViewById(R.id.bodyNotif);
        }
    }
}
