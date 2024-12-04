package com.polije.sem3.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SSEClient {

    private static final String TAG = "SSEClient";
    private final Context context;
    private boolean listening;
    private final Thread listenerThread;

    public SSEClient(String serverUrl, Context context) {
        this.context = context;
        this.listening = true;

        // Membuat thread terpisah untuk mendengarkan SSE
        listenerThread = new Thread(() -> listenToSSE(serverUrl));
        listenerThread.start();
    }

    private void listenToSSE(String serverUrl) {
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "text/event-stream");
            connection.setDoInput(true);
            connection.setConnectTimeout(5000); // Timeout 5 detik

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while (listening && (line = reader.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String jsonData = line.substring(6); // Menghapus "data: "
                    Log.d(TAG, "Message received: " + jsonData);

                    try {
                        // Parsing JSON response
                        JSONArray ticketsArray = new JSONArray(jsonData);

                        for (int i = 0; i < ticketsArray.length(); i++) {
                            JSONObject ticketObj = ticketsArray.getJSONObject(i);
                            String status = ticketObj.getString("status");
                            if ("berhasil".equalsIgnoreCase(status)) {
                                String namaWisata = ticketObj.getString("nama_wisata");
                                String idUser = ticketObj.getString("id_user");

                                // Membuat Intent untuk mengirim nama wisata yang berhasil
                                Intent intent = new Intent("com.polije.sem3.TIKET_CONFIRMED");
                                intent.putExtra("nama_wisata", namaWisata);
                                intent.putExtra("id_user", idUser);
                                context.sendBroadcast(intent); // Mengirim broadcast untuk update UI
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    public void close() {
        listening = false;
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt(); // Menghentikan thread
        }
        Log.d(TAG, "SSE Client closed");
    }
}
