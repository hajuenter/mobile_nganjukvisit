package com.polije.sem3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.polije.sem3.model.TiketModelAdapter;
import com.polije.sem3.model.TiketModel;
import com.polije.sem3.response.TiketResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;
import com.polije.sem3.util.WebSocketMessageListener;
import com.polije.sem3.util.WebSocketService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Book extends Fragment implements WebSocketMessageListener {

    private RecyclerView recyclerView;
    private TiketModelAdapter tiketAdapter;
    private LinearLayout layoutSearch;
    private EditText searchBox;
    private Handler handler= new Handler(Looper.getMainLooper());
    private WebSocket webSocket;
    private static final String SERVER_URL = "ws://172.16.103.107:8080"; // Ganti dengan IP yang benar untuk WebSocket server Anda
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        recyclerView = view.findViewById(R.id.recyclerviewListPenginapan);
        layoutSearch = view.findViewById(R.id.layoutSearch);
        searchBox = view.findViewById(R.id.searchbox);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        UsersUtil userUtil = new UsersUtil(requireContext());
        String idUser = userUtil.getId();
        loadTiketUser(idUser);
/*
        setupWebSocket();*/ // Mengatur WebSocket
        WebSocketService.setBookListener((WebSocketMessageListener) this);
        searchBox.setOnClickListener(v -> searchTiket(searchBox.getText().toString()));

        return view;
    }

    @Override
    public void onMessageReceived(String message) {
        // Tangani pesan yang diterima
        handler.post(() -> {
            UsersUtil util = new UsersUtil(requireContext());
            loadTiketUser(util.getId());
            Toast.makeText(getContext(), "Pesan WebSocket: " + message, Toast.LENGTH_SHORT).show();
        });
    }
    /*private void updateUIWithMessage(String message) {
        // Gunakan handler untuk memastikan eksekusi di main thread
        handler.post(() -> {
            // Misalnya, update TextView dengan pesan
            UsersUtil util =new UsersUtil(requireContext());
            loadTiketUser(util.getId());
        });
    }*/
/*

    private void setupWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                // Koneksi WebSocket berhasil
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "WebSocket Terhubung", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // Pesan diterima dari server
                handleWebSocketMessage(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // Jika pesan diterima dalam bentuk byte
                handleWebSocketMessage(bytes.utf8());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "WebSocket Menutup", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                // Pastikan fragment masih terpasang sebelum memperbarui UI
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "WebSocket Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

        });
    }
*/

    void handleWebSocketMessage(String message) {
        // Tangani pesan yang diterima dari server WebSocket
        requireActivity().runOnUiThread(() -> {
            try {
                // Coba untuk parsing data yang diterima sebagai JSON
                JSONArray updatedTickets = new JSONArray(message);
                // Menampilkan pesan pembaruan tiket
                Toast.makeText(getContext(), "Ada pembaruan tiket!", Toast.LENGTH_SHORT).show();
                // Anda bisa memproses tiket lebih lanjut di sini, misalnya memperbarui UI
                UsersUtil util =new UsersUtil(requireContext());
                loadTiketUser(util.getId());
            } catch (JSONException e) {
                // Menangani error jika pesan bukan dalam format yang diinginkan
                e.printStackTrace();
            }
        });
    }

    private Bitmap generateQRCode(String data) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int size = 120; // Ukuran QR code
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }
    private void loadTiketUser(String idUser) {
        Client.getInstance().getTiketUser("tampilkan", idUser).enqueue(new Callback<TiketResponse>() {
            @Override
            public void onResponse(Call<TiketResponse> call, Response<TiketResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TiketModel> tiketList = response.body().getData();
                    // Pengecekan status dan generate QR code
                    for (TiketModel tiket : tiketList) {
                        if ("berhasil".equalsIgnoreCase(tiket.getStatus())) {
                            try {
                                // Membuat data JSON untuk QR Code
                                JSONObject qrData = new JSONObject();
                                qrData.put("id_detail_tiket", tiket.get_id_detail_tiket());
                                qrData.put("nama_wisata", tiket.getNama_wisata());
                                qrData.put("nama_pemesan", tiket.getNama_pemesan());
                                qrData.put("jumlah_pengunjung", tiket.getJumlah());
                                qrData.put("tanggal", tiket.getTanggal());
                                qrData.put("status", tiket.getStatus());

                                // Menghasilkan QR code
                                String dataQR = qrData.toString(); // Mengubah JSON menjadi string
                                Bitmap bitmap = generateQRCode(dataQR);

                                // Menyimpan QR code di dalam tiket (bisa disesuaikan)
                                tiket.setQrCode(bitmap);

                            } catch (JSONException | WriterException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    tiketAdapter = new TiketModelAdapter(getContext(), tiketList);
                    recyclerView.setAdapter(tiketAdapter);
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Gagal memuat tiket", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<TiketResponse> call, Throwable t) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Koneksi gagal", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void searchTiket(String query) {
        Client.getInstance().searchTiket("cari", query).enqueue(new Callback<TiketResponse>() {
            @Override
            public void onResponse(Call<TiketResponse> call, Response<TiketResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TiketModel> tiketList = response.body().getData();
                    tiketAdapter.updateTiketList(tiketList);
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Tidak ditemukan tiket", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<TiketResponse> call, Throwable t) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Koneksi gagal", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, null);// Menutup koneksi WebSocket
        }
        WebSocketService.setBookListener(null);
    }
}
