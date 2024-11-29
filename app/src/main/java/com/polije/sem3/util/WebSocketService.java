package com.polije.sem3.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.polije.sem3.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService extends Service {

    private WebSocket webSocket1; // WebSocket untuk port 8080
    private WebSocket webSocket2; // WebSocket untuk port 8081
    private Handler uiHandler;
    private static WebSocketMessageListener bookListener;  // Listener untuk Book.java
    private static WebSocketMessageListener notifyListener;  // Listener untuk Notify.java

    private NotificationManager notificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uiHandler = new Handler(Looper.getMainLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        createNotificationChannel();  // Membuat channel notifikasi untuk Android 8.0+

        setupWebSocket1();  // WebSocket pertama (8080)
        setupWebSocket2();  // WebSocket kedua (8081)
        return START_STICKY;
    }

    private void setupWebSocket1() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.137.125:8080").build();
        webSocket1 = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocket1", "Terhubung ke port 8080");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket1", "Pesan diterima dari port 8080: " + text);
                sendMessageToListener(text, bookListener);  // Kirim ke Book.java
                showNotification(text);  // Tampilkan notifikasi
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e("WebSocket1", "Koneksi Gagal di port 8080", t);
            }
        });
    }

    private void setupWebSocket2() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.137.125:8081").build();
        webSocket2 = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocket2", "Terhubung ke port 8081");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket2", "Pesan diterima dari port 8081: " + text);
                sendMessageToListener(text, notifyListener);  // Kirim ke Notify.java
                showNotification(text);  // Tampilkan notifikasi
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e("WebSocket2", "Koneksi Gagal di port 8081", t);
            }
        });
    }

    private void sendMessageToListener(final String message, final WebSocketMessageListener listener) {
        if (listener != null) {
            uiHandler.post(() -> listener.onMessageReceived(message));
        }
    }

    public static void setBookListener(WebSocketMessageListener listener) {
        bookListener = listener;  // Set listener untuk Book.java
    }

    public static void setNotifyListener(WebSocketMessageListener listener) {
        notifyListener = listener;  // Set listener untuk Notify.java
    }

    @Override
    public void onDestroy() {
        if (webSocket1 != null) {
            webSocket1.close(1000, "Service dihentikan");
        }
        if (webSocket2 != null) {
            webSocket2.close(1000, "Service dihentikan");
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(String message) {
        // Menampilkan notifikasi setiap kali pesan diterima
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "websocket_channel")
                .setSmallIcon(R.drawable.newlogo_nganjukvisit)
                .setContentTitle("Pesan Baru")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "WebSocket Channel";
            String description = "Channel untuk notifikasi WebSocket";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("websocket_channel", name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }
    }
}