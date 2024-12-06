package com.polije.sem3.main_menu;

import android.annotation.SuppressLint;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.polije.sem3.R;
import com.polije.sem3.adapter.NotifyAdapter;
import com.polije.sem3.model.NotifyModelNew;
import com.polije.sem3.model.NotifyViewModel;
import com.polije.sem3.response.NotifyResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.NotificationManager; // Custom NotificationManager
import com.polije.sem3.util.UsersUtil;
import com.polije.sem3.util.WebSocketMessageListener;
import com.polije.sem3.util.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notify extends Fragment implements WebSocketMessageListener {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private NotifyAdapter adapter;
    private RecyclerView recyclerView;
    private NotifyViewModel viewModel;
    private NotificationManager notificationManager;
    private String idUser;// Custom NotificationManager instance

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotifyViewModel.class);
        WebSocketService.setNotifyListener(this); // Register WebSocket listener
        notificationManager = new NotificationManager(getContext()); // Initialize custom NotificationManager
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notify, container, false);
        recyclerView = rootView.findViewById(R.id.recviewNotify);
        UsersUtil util = new UsersUtil(requireContext());
        String idUser = util.getId();
        // Mengatur adapter untuk RecyclerView
        adapter = new NotifyAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        getNotifikasi("event", "tiket", idUser);
        // Mengamati LiveData untuk mendapatkan notifikasi terbaru
        viewModel.getNotifyList().observe(getViewLifecycleOwner(), notifyList -> {
            // Perbarui adapter dengan data baru
            adapter.updateData(notifyList);
        });

        return rootView;
    }

    private final List<NotifyModelNew> allNotifications = new ArrayList<>(); // Global list untuk semua notifikasi

    public void getNotifikasi(String judulEvent, String judulTiket, String id_user) {
        // Memanggil API untuk mendapatkan "Event Baru"
        Client.getInstance().notifevent("notif", judulEvent).enqueue(new Callback<NotifyResponse>() {
            @Override
            public void onResponse(Call<NotifyResponse> call, Response<NotifyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NotifyResponse notifikasiResponse = response.body();
                    if (notifikasiResponse.getStatus().equals("success")) {
                        List<NotifyModelNew> notif = notifikasiResponse.getData(); // Ambil data list notifikasi

                        if (notif != null && !notif.isEmpty()) {
                            allNotifications.addAll(notif); // Tambahkan ke global list
                            adapter.updateData(new ArrayList<>(allNotifications)); // Perbarui adapter
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NotifyResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error Event: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Memanggil API untuk mendapatkan "Tiket Wisata"
        Client.getInstance().notifuser("notif", judulTiket, id_user).enqueue(new Callback<NotifyResponse>() {
            @Override
            public void onResponse(Call<NotifyResponse> call, Response<NotifyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NotifyResponse notifikasiResponse = response.body();
                    if (notifikasiResponse.getStatus().equals("success")) {
                        List<NotifyModelNew> notif = notifikasiResponse.getData(); // Ambil data list notifikasi

                        if (notif != null && !notif.isEmpty()) {
                            allNotifications.addAll(notif); // Tambahkan ke global list
                            adapter.updateData(new ArrayList<>(allNotifications)); // Perbarui adapter
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NotifyResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error Tiket: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // WebSocket message listener untuk notifikasi
    @Override
    public void onMessageReceived(String message) {
        handler.post(() -> {
            Log.d("WebSocketMessage", message);

            try {
                // Parsing pesan WebSocket sebagai JSON
                JSONObject messageObject = new JSONObject(message);
                String judul = messageObject.getString("judul");
                String isi = messageObject.getString("isi");
                String waktu = getCurrentTime(); // Menggunakan waktu saat ini

                // Menentukan apakah notifikasi untuk semua pengguna atau pengguna tertentu
                if (judul.contains("Event Baru")) {
                    // Kirim notifikasi untuk semua pengguna
                    NotifyModelNew eventNotification = new NotifyModelNew(judul, isi);
                    viewModel.addNotify(eventNotification);
                    adapter.notifyDataSetChanged();
                    showNotification(judul, isi);
                } else if (judul.contains("Tiket Wisata")) {
                    // Kirim notifikasi hanya untuk pengguna yang memesan tiket
                    String userId = messageObject.getString("user_id"); // ID user yang memesan tiket
                    String activeUserId = userId; // Ganti dengan ID pengguna yang aktif (misalnya dari session/login)

                    if (userId.equals(activeUserId)) {
                        NotifyModelNew ticketNotification = new NotifyModelNew(judul, isi);
                        viewModel.addNotify(ticketNotification);
                        adapter.notifyDataSetChanged();
                        showNotification(judul, isi);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            getNotifikasi("event", "tiket", idUser);
        });
    }

    // Fungsi untuk menampilkan notifikasi
    private void showNotification(String title, String message) {
        RemoteViews notificationLayout = new RemoteViews(getContext().getPackageName(), R.layout.activity_row_notif);
        notificationLayout.setTextViewText(R.id.notifTitle, title);
        notificationLayout.setTextViewText(R.id.bodyNotif, message);
    }

    // Mengambil waktu saat ini dalam format yang diinginkan
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Mengecek izin untuk notifikasi pada Android 13 dan lebih tinggi
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
