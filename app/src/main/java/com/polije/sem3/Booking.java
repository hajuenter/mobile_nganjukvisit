package com.polije.sem3;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import androidx.appcompat.app.AppCompatActivity;

import com.polije.sem3.detail.DetailInformasi;
import com.polije.sem3.model.BookingModel;
import com.polije.sem3.response.BookingResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.retrofit.RetrofitEndPoint;
import com.polije.sem3.util.UsersUtil;
import com.polije.sem3.util.WebSocketService;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Booking extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, dateEditText, memberSpinner;
    private TextView totalCostTextView, ticketPriceTextView;
    private CheckBox consentCheckBox;
    private Button checkoutButton, backButton;
    private Book bookfragment;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_booking);
        Intent intent = getIntent();
        String idWisata = intent.getStringExtra("idWisata");
        String namaWisata = intent.getStringExtra("namaWisata");
        String hargaTiket = intent.getStringExtra("hargaTiket");
        String NoHp = intent.getStringExtra("nohp");
        UsersUtil usersUtil = new UsersUtil(this);

        // Inisialisasi semua view
        nameEditText = findViewById(R.id.namapesanan);
        emailEditText = findViewById(R.id.emailpesanan);
        phoneEditText = findViewById(R.id.nomorpesanan);
        dateEditText = findViewById(R.id.tanggalpesanan);
        memberSpinner = findViewById(R.id.comboboxmember);
        totalCostTextView = findViewById(R.id.txttotalcost);
        ticketPriceTextView = findViewById(R.id.txthargatiket);
        consentCheckBox = findViewById(R.id.checkBox);
        checkoutButton = findViewById(R.id.buttonbayar);
        backButton = findViewById(R.id.buttonBack);
        String userId = usersUtil.getId();
        nameEditText.setText(usersUtil.getUsername());
        emailEditText.setText(usersUtil.getEmail());
        phoneEditText.setText(usersUtil.getNoTelp());
        memberSpinner.setText("1");
        ticketPriceTextView.setText("Rp."+hargaTiket);
        String jumlah = memberSpinner.getText().toString();
        // Menambahkan listener untuk klik pada EditText (tanggal)
        dateEditText.setOnClickListener(v -> showDatePicker());
        calculateTotalCost(hargaTiket,jumlah);
        memberSpinner.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Saat teks diubah, perbarui total biaya
                String jumlah = memberSpinner.getText().toString();
                if (!jumlah.isEmpty()) {
                    calculateTotalCost(hargaTiket, jumlah);
                }else{
                    totalCostTextView.setText("Rp.0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Event listener untuk tombol bayar
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consentCheckBox.isChecked()) {
                    String name = nameEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String phone = phoneEditText.getText().toString();
                    String date = dateEditText.getText().toString();
                    String memberType = memberSpinner.getText().toString();

                    // Menghilangkan teks "Rp." dari harga tiket dan total biaya
                    String ticketPrice = ticketPriceTextView.getText().toString().replace("Rp.", "").trim();
                    String totalCost = totalCostTextView.getText().toString().replace("Rp.", "").trim();

                    String wisataName = namaWisata;  // Ubah sesuai nama wisata
                    String status = "diproses";  // Status default, bisa disesuaikan

                    // Membuat objek BookingModel
                    BookingModel bookingRequest = new BookingModel(idWisata, userId, date, memberType, ticketPrice, wisataName, status, totalCost);

                    // Menentukan action yang ingin dipanggil (misalnya: 'pesan')
                    String action = "pesan";
                    Intent serviceIntent = new Intent(Booking.this, WebSocketService.class);
                    startService(serviceIntent);  // Menjalankan Service untuk WebSocket
                    // Mendapatkan instance RetrofitEndPoint
                    RetrofitEndPoint api = Client.getInstance();

                    // Mengirim permintaan ke server
                    Call<BookingResponse> call = api.createBooking(action, bookingRequest);
                    call.enqueue(new Callback<BookingResponse>() {
                        @Override
                        public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                            if (response.isSuccessful()) {
                                BookingResponse bookingResponse = response.body();
                                if (bookingResponse != null && bookingResponse.isSuccess()) {
                                    // Pemesanan berhasil
                                    Toast.makeText(Booking.this, "Checkout sukses untuk: " + name, Toast.LENGTH_SHORT).show();
                                    String url = "https://wa.me/+"+NoHp+"?text=Halo, saya telah berhasil memesan tiket untuk " + namaWisata + " dengan ID Tiket: " + bookingResponse.getData().getId_tiket()+" ,dan saya ingin melakukan pembayaran";
                                    Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                                    whatsappIntent.setData(Uri.parse(url));
                                    startActivity(whatsappIntent);
                                } else {
                                    // Gagal membuat pesanan
                                    Toast.makeText(Booking.this, "Gagal membuat pesanan: " + bookingResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Kesalahan saat menghubungi server
                                Toast.makeText(Booking.this, "Terjadi kesalahan saat menghubungi server.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<BookingResponse> call, Throwable t) {
                            // Kesalahan jaringan
                            Toast.makeText(Booking.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(Booking.this, Dashboard.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Booking.this, "Harap setujui persyaratan.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Event listener untuk tombol kembali
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Mengakhiri aktivitas
            }
        });
    }
    // Method untuk menampilkan DatePickerDialog
    private void showDatePicker() {
        // Mendapatkan tanggal hari ini
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Membuat DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Menampilkan tanggal yang dipilih di EditText
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        dateEditText.setText(selectedDate);
                    }
                },
                year, month, day
        );

        // Menampilkan DatePickerDialog
        datePickerDialog.show();
    }
    private void calculateTotalCost(String hargaTiket, String memberCount) {
        try {
            // Mengambil harga tiket
            double ticketPrice = Double.parseDouble(hargaTiket);

            // Mengambil jumlah member dari EditText
            int memberCountValue = Integer.parseInt(memberCount);

            // Menghitung total biaya
            double totalCost = ticketPrice * memberCountValue;
            totalCostTextView.setText(String.format("Rp.%d", (long) totalCost));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Terjadi kesalahan saat menghitung total biaya.", Toast.LENGTH_SHORT).show();
        }
    }

}
