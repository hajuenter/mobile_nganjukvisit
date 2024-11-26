package com.polije.sem3.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.polije.sem3.model.NotifyModelNew;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationManager {

    private Context context;
    private NotificationManagerCompat notificationManagerCompat;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "notification_prefs";
    private static final String KEY_NOTIFICATIONS = "notifications";

    public NotificationManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        notificationManagerCompat = NotificationManagerCompat.from(context);  // Inisialisasi NotificationManagerCompat
    }

    // Menyimpan Notifikasi ke SharedPreferences
    public void saveNotificationToSharedPreferences(NotifyModelNew notifyModelNew) {
        if (notifyModelNew == null) {
            Log.e("NotificationManager", "Notifikasi tidak valid, tidak bisa disimpan.");
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(notifyModelNew);

        // Memuat notifikasi yang sudah ada
        List<NotifyModelNew> notifications = loadNotificationsFromSharedPreferences();
        notifications.add(notifyModelNew);

        // Menyimpan ulang daftar notifikasi ke SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NOTIFICATIONS, gson.toJson(notifications));
        editor.apply();
    }

    // Membaca JSON Notifikasi dari SharedPreferences
    private List<NotifyModelNew> loadNotificationsJsonFromSharedPreferences() {
        String json = sharedPreferences.getString(KEY_NOTIFICATIONS, "[]");
        try {
            // Mengembalikan list of NotifyModelNew dari JSON
            return new Gson().fromJson(json, new TypeToken<List<NotifyModelNew>>(){}.getType());
        } catch (JsonSyntaxException e) {
            Log.e("NotificationManager", "Gagal parsing JSON notifikasi: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Membaca dan Parsing Notifikasi dari SharedPreferences
    public List<NotifyModelNew> loadNotificationsFromSharedPreferences() {
        return loadNotificationsJsonFromSharedPreferences();
    }

    // Menghapus Notifikasi Expired (lebih dari 24 jam)
    public void removeExpiredNotifications() {
        List<NotifyModelNew> notifications = loadNotificationsFromSharedPreferences();
        long currentTime = System.currentTimeMillis();

        Iterator<NotifyModelNew> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            NotifyModelNew notify = iterator.next();
            // Jika notifikasi lebih dari 24 jam, hapus dari daftar
            if (currentTime - Long.parseLong(notify.getTanggalnotif()) > 24 * 60 * 60 * 1000) { // 24 jam dalam milidetik
                iterator.remove();
            }
        }
        saveNotificationsToSharedPreferences(notifications); // Simpan perubahan ke SharedPreferences
    }

    // Menyimpan ulang daftar notifikasi setelah diperbarui
    private void saveNotificationsToSharedPreferences(List<NotifyModelNew> notifications) {
        Gson gson = new Gson();
        String json = gson.toJson(notifications);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NOTIFICATIONS, json);
        editor.apply();
    }

    // Menghapus Notifikasi Setelah Diklik
    public void onNotificationClicked(NotifyModelNew notifyModelNew) {
        List<NotifyModelNew> notifications = loadNotificationsFromSharedPreferences();
        notifications.remove(notifyModelNew);

        saveNotificationsToSharedPreferences(notifications);
        Toast.makeText(context, "Notifikasi dihapus setelah diklik", Toast.LENGTH_SHORT).show();
    }

    // Membuat Notifikasi
    public void createNotification(String channelId, String title, RemoteViews customLayout) {
        if (context == null) {
            Log.e("NotificationError", "Context is null, cannot create notification.");
            return;
        }

        // Periksa izin jika diperlukan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.e("NotificationError", "Permission POST_NOTIFICATIONS is not granted.");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setCustomContentView(customLayout)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        try {
            notificationManagerCompat.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            Log.e("NotificationError", "SecurityException: Izin ditolak atau masalah lain terjadi", e);
        }
    }
}
