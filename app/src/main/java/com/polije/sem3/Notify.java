package com.polije.sem3;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.polije.sem3.model.NotifyAdapter;
import com.polije.sem3.model.NotifyModelNew;
import com.polije.sem3.model.NotifyViewModel;
import com.polije.sem3.util.NotificationManager; // Your custom NotificationManager
import com.polije.sem3.util.WebSocketMessageListener;
import com.polije.sem3.util.WebSocketService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Notify extends Fragment implements WebSocketMessageListener {
    private Handler handler = new Handler(Looper.getMainLooper());
    private NotifyAdapter adapter;
    private RecyclerView recyclerView;
    private NotifyViewModel viewModel;
    private NotificationManager notificationManager; // Custom NotificationManager instance

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotifyViewModel.class);
        WebSocketService.setMessageListener(this); // Register WebSocket listener
        notificationManager = new NotificationManager(getContext()); // Initialize your custom NotificationManager
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notify, container, false);
        recyclerView = rootView.findViewById(R.id.recviewNotify);

        // Mengatur adapter untuk RecyclerView
        adapter = new NotifyAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Memuat data notifikasi dari SharedPreferences
        loadNotificationsFromSharedPreferences();

        // Mengamati LiveData untuk mendapatkan notifikasi terbaru
        viewModel.getNotifyList().observe(getViewLifecycleOwner(), notifyList -> {
            // Perbarui adapter dengan data baru
            adapter.updateData(notifyList);
        });

        return rootView;
    }

    // Metode untuk memuat notifikasi dari SharedPreferences
    private void loadNotificationsFromSharedPreferences() {
        // Memuat notifikasi dari SharedPreferences menggunakan custom NotificationManager
        List<NotifyModelNew> notifications = notificationManager.loadNotificationsFromSharedPreferences();

        // Perbarui ViewModel dengan data yang dimuat
        viewModel.setNotifyList(notifications);

        // Perbarui RecyclerView Adapter dengan data baru
        adapter.updateData(notifications);
    }

    @Override
    public void onMessageReceived(String message) {
        handler.post(() -> {
            Log.d("WebSocketMessage", message);
            createNotificationFromWebSocket();
            String pesan = "Selamat Pembayaran Tiket Wisata Berhasil terkonfirmasi!!! Anda sekarang dapat melihat informasi tiketnya di menu Booking, tunjukkan pada petugas penjaga loket saat ingin memasuki wisata.";
            String currentTime = getCurrentTime();

            NotifyModelNew newNotify = new NotifyModelNew("Notif Konfirmasi Pembayaran Tiket", pesan, currentTime);
            viewModel.addNotify(newNotify);

            // Simpan notifikasi ke SharedPreferences
            notificationManager.saveNotificationToSharedPreferences(newNotify);

            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Pesan WebSocket: " + message, Toast.LENGTH_SHORT).show();
        });
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void createNotificationFromWebSocket() {
        String currentTime = getCurrentTime();
        RemoteViews notificationLayout = new RemoteViews(getContext().getPackageName(), R.layout.activity_row_notif);
        notificationLayout.setTextViewText(R.id.notifTitle, "Notif Konfirmasi Pembayaran Tiket");
        notificationLayout.setTextViewText(R.id.timedate, currentTime);
        notificationLayout.setTextViewText(R.id.bodyNotif, "Selamat Pembayaran Tiket Wisata Berhasil terkonfirmasi!!! Anda sekarang dapat melihat informasi tiketnya di menu Booking, tunjukkan pada petugas penjaga loket saat ingin memasuki wisata.");
        notificationManager.createNotification("websocket_channel", "Notif Konfirmasi Pembayaran Tiket", notificationLayout);
    }



    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }
}
