package com.polije.sem3.main_menu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.WebSocket;

import androidx.appcompat.app.AppCompatActivity;

import com.polije.sem3.R;
import com.polije.sem3.model.BookingModel;
import com.polije.sem3.response.BookingResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.retrofit.RetrofitEndPoint;
import com.polije.sem3.util.UsersUtil;
import com.polije.sem3.util.WebSocketService;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

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
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hanya izinkan huruf dan spasi
                String filteredText = s.toString().replaceAll("[^a-zA-Z ]", "");
                if (!s.toString().equals(filteredText)) {
                    nameEditText.setText(filteredText);
                    int cursorPosition = filteredText.length();
                    if (cursorPosition <= nameEditText.getText().length()) {
                        nameEditText.setSelection(cursorPosition); // Mengatur seleksi pada posisi yang valid
                    } // Menjaga kursor di akhir
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        emailEditText.setText(usersUtil.getEmail());
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Tidak ada perubahan yang perlu dilakukan di sini
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                String input = charSequence.toString();
                String regex = "[a-zA-Z0-9._@+-]*";
                if (!input.matches(regex)) {
                    emailEditText.setText(input.replaceAll("[^a-zA-Z0-9._@+-]", ""));
                    int cursorPosition = input.length();
                    if (cursorPosition <= emailEditText.getText().length()) {
                        emailEditText.setSelection(cursorPosition); // Mengatur seleksi pada posisi yang valid
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Tidak ada perubahan yang perlu dilakukan di sini
            }
        });

        phoneEditText.setText(usersUtil.getNoTelp());
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String currentText = charSequence.toString();
                String filteredText = currentText.replaceAll("[^0-9]", ""); // Menghapus karakter selain angka

                // Jika teks yang dimasukkan tidak sesuai, set ulang EditText dengan teks yang telah difilter
                if (!currentText.equals(filteredText)) {
                    phoneEditText.setText(filteredText);
                    int cursorPosition = filteredText.length();
                    if (cursorPosition <= phoneEditText.getText().length()) {
                        phoneEditText.setSelection(cursorPosition); // Mengatur seleksi pada posisi yang valid
                    }// Menjaga kursor tetap di akhir
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        memberSpinner.setText("1");
        if (hargaTiket.equalsIgnoreCase("Gratis")) {
            ticketPriceTextView.setText("Gratis");
        } else {
            try {double ticketPrice = Double.parseDouble(hargaTiket);
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                String formattedPrice = currencyFormat.format(ticketPrice);
                String Price = formattedPrice.replace("Rp","Rp.").replace(",00","").trim();
                ticketPriceTextView.setText(Price);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(this, "Terjadi kesalahan dalam format harga tiket.", Toast.LENGTH_SHORT).show();
            }
        }
        String jumlah = memberSpinner.getText().toString();
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
                    if(hargaTiket == "Gratis"){
                        totalCostTextView.setText("Gratis");
                    }else{
                    calculateTotalCost(hargaTiket, jumlah);}
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
                    String name = nameEditText.getText().toString().trim();
                    String email = emailEditText.getText().toString().trim();
                    final String phone = phoneEditText.getText().toString().trim();  // Menambahkan final pada variabel phone
                    String date = dateEditText.getText().toString().trim();
                    String memberType = memberSpinner.getText().toString().trim();

                    if (phone.length() < 10 || phone.length() > 13) {
                        Toast.makeText(Booking.this, "Nomor telepon harus antara 10 hingga 13 digit.", Toast.LENGTH_SHORT).show();
                        return; // Menghentikan eksekusi lebih lanjut jika validasi gagal
                    } else {
                        // Validasi jika nomor telepon dimulai dengan 0 atau 62
                        if (!phone.startsWith("0") && !phone.startsWith("62")) {
                            Toast.makeText(Booking.this, "Nomor telepon harus dimulai dengan 0 atau 62.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || date.isEmpty() || memberType.isEmpty()) {
                            Toast.makeText(Booking.this, "Harap lengkapi data pesanan.", Toast.LENGTH_SHORT).show();
                        } else {
                            String ticketPrice = ticketPriceTextView.getText().toString().replace("Rp.", "").replace(".", "").trim();
                            String totalCost = totalCostTextView.getText().toString().replace("Rp.", "").replace(".", "").trim();

                            String wisataName = namaWisata;  // Ubah sesuai nama wisata
                            String status = "diproses";  // Status default, bisa disesuaikan
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
                                            // Memastikan nomor telepon diawali dengan + dan negara yang benar (mengganti 0 dengan +62)
                                            String phoneToUse = phone;
                                            if (phoneToUse.startsWith("0")) {
                                                phoneToUse = "+62" + phoneToUse.substring(1); // Ganti 0 dengan +62
                                            }

                                            String url = "https://wa.me/" + phoneToUse + "?text=Halo, saya telah berhasil memesan tiket untuk " + wisataName + " dengan ID Tiket: " + bookingResponse.getData().getId_tiket() + " ,dan saya ingin melakukan pembayaran";
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
                            showPaymentSuccessDialog();

                        }
                    }
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

        // Membatasi tanggal agar tidak bisa memilih sebelum hari ini
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        // Menampilkan DatePickerDialog
        datePickerDialog.show();
    }

    public void showPaymentSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_payment, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnBackHome = dialogView.findViewById(R.id.btn_back_home);
        btnBackHome.setOnClickListener(view -> {
            Intent intent = new Intent(Booking.this, Dashboard.class);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }
    private void calculateTotalCost(String hargaTiket, String memberCount) {
        try {
            // Mengecek apakah harga tiket adalah "Gratis"
            if (hargaTiket.equalsIgnoreCase("Gratis")) {
                totalCostTextView.setText("Gratis");
                return; // Tidak lanjutkan perhitungan jika harga tiket "Gratis"
            }

            // Mengambil harga tiket
            double ticketPrice = Double.parseDouble(hargaTiket);

            // Mengambil jumlah member dari EditText
            int memberCountValue = Integer.parseInt(memberCount);

            // Menghitung total biaya
            double totalCost = ticketPrice * memberCountValue;

            // Format hasil ke Rupiah
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String formattedTotalCost = currencyFormat.format(totalCost);
            String Price = formattedTotalCost.replace("Rp","Rp.").replace(",00","").trim();
            // Menampilkan hasil ke TextView
            totalCostTextView.setText(Price);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Terjadi kesalahan saat menghitung total biaya.", Toast.LENGTH_SHORT).show();
        }
    }



}
