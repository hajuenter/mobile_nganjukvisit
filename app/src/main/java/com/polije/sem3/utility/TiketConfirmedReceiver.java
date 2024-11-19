package com.polije.sem3.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TiketConfirmedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.polije.sem3.TIKET_CONFIRMED".equals(intent.getAction())) {
            String namaWisata = intent.getStringExtra("nama_wisata");
            if (namaWisata != null) {
                Toast.makeText(context, "Tiket dikonfirmasi untuk: " + namaWisata, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
