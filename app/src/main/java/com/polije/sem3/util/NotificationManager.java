package com.polije.sem3.util;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.polije.sem3.model.NotifyModelNew;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationManager {

    private Context context;
    private NotificationManagerCompat notificationManagerCompat;

    public NotificationManager(Context context) {
        this.context = context;
    }

    // Model untuk Notifikasi
    public static class NotifyModel {
        private String title;
        private String message;
        private long timestamp;

        public NotifyModel(String title, String message, long timestamp) {
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    // Menyimpan Notifikasi ke dalam File
    // Pastikan Anda tidak menulis baris kosong
    public void saveNotificationToFile(NotifyModelNew notifyModel) {
        try {
            if (notifyModel == null) {
                Log.e("NotificationManager", "Notifikasi tidak valid, tidak bisa disimpan.");
                return;
            }

            Gson gson = new Gson();
            String json = gson.toJson(notifyModel);

            File file = new File(context.getFilesDir(), "notifications.json");

            // Menulis data ke file, pastikan setiap notifikasi ada di baris baru
            try (FileOutputStream fos = new FileOutputStream(file, true); // true untuk append
                 OutputStreamWriter writer = new OutputStreamWriter(fos)) {
                if (!json.trim().isEmpty()) {
                    writer.write(json + "\n"); // Menambahkan newline setelah setiap notifikasi
                }
            }

        } catch (IOException e) {
            Log.e("NotificationManager", "Kesalahan menyimpan notifikasi: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void createNotification(String channelId, String title, RemoteViews customLayout) {
        // Pastikan Anda memiliki konteks
        if (context == null) {
            Log.e("NotificationError", "Context is null, cannot create notification.");
            return;
        }

        // Periksa apakah izin POST_NOTIFICATIONS telah diberikan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("NotificationError", "Permission POST_NOTIFICATIONS is not granted.");
                return; // Keluar dari metode jika izin belum diberikan
            }
        }

        // Membuat builder untuk notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_notification_overlay) // Setel ikon kecil
                .setCustomContentView(customLayout) // Gunakan tata letak kustom
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Notifikasi akan hilang saat diklik

        try {
            // Tampilkan notifikasi menggunakan NotificationManagerCompat
            notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            // Tangani pengecualian jika ada masalah dengan izin
            Log.e("NotificationError", "SecurityException: Izin ditolak atau masalah lain terjadi", e);
        }
    }



    // Membaca Notifikasi dari File
    public List<NotifyModel> loadNotificationsFromFile() {
        List<NotifyModel> notifications = new ArrayList<>();
        File file = new File(context.getFilesDir(), "notifications.json");

        if (!file.exists()) {
            return notifications; // Jika file tidak ada, kembalikan daftar kosong
        }

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(fis)) {

            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().isEmpty() || line.equals("[]")) {
                    // Abaikan baris kosong atau baris yang berisi []
                    continue;
                }
                try {
                    // Parse setiap baris sebagai objek JSON terpisah
                    NotifyModel notifyModel = new Gson().fromJson(line, NotifyModel.class);
                    notifications.add(notifyModel);
                } catch (JsonSyntaxException e) {
                    Log.e("NotificationManager", "Gagal parsing baris JSON: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return notifications;
    }





    // Menghapus Notifikasi yang Sudah Expired (lebih dari 24 jam)
    public void removeExpiredNotifications() {
        List<NotifyModel> notifications = loadNotificationsFromFile();
        long currentTime = System.currentTimeMillis();

        Iterator<NotifyModel> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            NotifyModel notify = iterator.next();
            if (currentTime - notify.getTimestamp() > 24 * 60 * 60 * 1000) { // 24 jam dalam milidetik
                iterator.remove(); // Menghapus notifikasi yang sudah lebih dari 24 jam
            }
        }

        // Menyimpan ulang data yang sudah diperbarui
        saveNotificationsToFile(notifications);
    }

    // Menyimpan Ulang Daftar Notifikasi setelah Dihapus
    public void saveNotificationsToFile(List<NotifyModel> notifications) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(notifications);

            File file = new File(context.getFilesDir(), "notifications.json");

            // Menulis ulang data ke file
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter writer = new OutputStreamWriter(fos)) {
                writer.write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Menghapus Notifikasi Setelah Diklik
    public void onNotificationClicked(NotifyModel notifyModel) {
        // Menghapus notifikasi yang diklik dari file
        List<NotifyModel> notifications = loadNotificationsFromFile();
        notifications.remove(notifyModel);

        // Menyimpan ulang data yang sudah diperbarui
        saveNotificationsToFile(notifications);

        // Tampilkan pesan atau lakukan tindakan lainnya
        Toast.makeText(context, "Notifikasi dihapus setelah diklik", Toast.LENGTH_SHORT).show();
    }
}
