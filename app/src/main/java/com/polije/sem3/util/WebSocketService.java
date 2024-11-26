package com.polije.sem3.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.content.pm.PackageManager;

import com.polije.sem3.Notify;
import com.polije.sem3.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService extends Service {

    private WebSocket webSocket;
    private Handler uiHandler;
    private static WebSocketMessageListener messageListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uiHandler = new Handler(Looper.getMainLooper());  // Buat handler untuk UI thread
        setupWebSocket();
        return START_STICKY; // Agar service tetap berjalan
    }

    private void setupWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.1.4:8080").build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocket", "Terhubung");
            }


            @Override
            public void onMessage(WebSocket webSocket, String text) {
                /*sendMessageToUi(text);*/
                NotificationManager notificationManager = new NotificationManager(getApplicationContext());
                sendMessageToListener(text);
                Log.d("WebSocket", "Pesan diterima: " + text);
                String currentTime = getCurrentTime();
                RemoteViews notificationLayout = new RemoteViews(getApplicationContext().getPackageName(), R.layout.activity_row_notif);
                notificationLayout.setTextViewText(R.id.notifTitle, "Notif Konfirmasi Pembayaran Tiket");
                notificationLayout.setTextViewText(R.id.timedate, currentTime);
                notificationLayout.setTextViewText(R.id.bodyNotif, "Selamat Pembayaran Tiket Wisata Berhasil terkonfirmasi!!! Anda sekarang dapat melihat informasi tiketnya di menu Booking, tunjukkan pada petugas penjaga loket saat ingin memasuki wisata.");
                notificationManager.createNotification("websocket_channel", "Notif Konfirmasi Pembayaran Tiket", notificationLayout);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e("WebSocket", "Koneksi Gagal", t);
            }
        });
    }
    private void sendMessageToListener(final String message) {
        if (messageListener != null) {
            uiHandler.post(() -> messageListener.onMessageReceived(message));
        }
    }

    public static void setMessageListener(WebSocketMessageListener listener) {
        messageListener = listener;
    }
    /*private void showNotification() {
        NotificationManager notificationManager = new NotificationManager(getApplicationContext());
        String currentTime = getCurrentTime();
        RemoteViews notificationLayout = new RemoteViews(getApplicationContext().getPackageName(), R.layout.activity_row_notif);
        notificationLayout.setTextViewText(R.id.notifTitle, "Notif Konfirmasi Pembayaran Tiket");
        notificationLayout.setTextViewText(R.id.timedate, currentTime);
        notificationLayout.setTextViewText(R.id.bodyNotif, "Selamat Pembayaran Tiket Wisata Berhasil terkonfirmasi!!! Anda sekarang dapat melihat informasi tiketnya di menu Booking, tunjukkan pada petugas penjaga loket saat ingin memasuki wisata.");
        // Membuat notifikasi
        notificationManager.createNotification(
                "websocket_channel",
                "Pesan WebSocket",
                notificationLayout // Pesan yang diterima dari WebSocket
        );
    }*/
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    /*private void sendMessageToUi(final String message) {
        // Kirim pesan ke UI thread menggunakan Handler
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                // Anda dapat menangani pesan di sini, misalnya, dengan update UI
                // Misalnya, kirim pesan ke Activity atau Fragment
                Intent intent = new Intent("com.polije.sem3.WEBSOCKET_MESSAGE");
                intent.putExtra("message", message);
                sendBroadcast(intent);  // Kirim pesan ke receiver jika diperlukan
            }
        });
    }*/

    @Override
    public void onDestroy() {
        if (webSocket != null) {
            webSocket.close(1000, "Service dihentikan");
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  // Tidak digunakan dalam hal ini
    }
}
