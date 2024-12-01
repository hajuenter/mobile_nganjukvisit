//package com.polije.sem3.apigoogle;
//
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.polije.sem3.model.NotifyModelNew;
//import com.polije.sem3.util.NotificationManager;
//
//import java.util.List;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = "FCMService";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // Cek apakah pesan FCM memiliki data
//        if (remoteMessage.getData().size() > 0) {
//            // Proses data dari FCM
//            String title = remoteMessage.getData().get("title");
//            String body = remoteMessage.getData().get("body");
//
//            // Jika pesan memiliki data, buat notifikasi
//            if (title != null && body != null) {
//                // Gunakan NotificationManager untuk membuat notifikasi
//                CustomNotificationManager notificationManager = new CustomNotificationManager(getApplicationContext());
//                notificationManager.createNotification("fcm_channel", title, body);
//            }
//        }
//    }
//
//    @Override
//    public void onNewToken(String token) {
//        // Token baru diterima dari FCM, simpan token ke server atau shared preferences jika diperlukan
//        Log.d(TAG, "FCM Token: " + token);
//    }
//}
