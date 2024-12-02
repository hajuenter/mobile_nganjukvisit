package com.polije.sem3.model;

import android.graphics.Bitmap;

public class TiketModel {
    private String id_detail_tiket; // Diubah ke String untuk menyesuaikan ID unik
    private int id_user;
    private String nama_pemesan; // Nama user
    private int id_wisata;
    private String nama_wisata; // Nama wisata dari detail_wisata
    private int jumlah;
    private String tanggal;
    private int harga;
    private int total;
    private int kembalian;
    private String status; // Status tiket ('gagal', 'diproses', 'berhasil')
    private Bitmap qrCode; // QR code jika diperlukan (optional)

    // Getter dan Setter
    public String getId_detail_tiket() {
        return id_detail_tiket;
    }

    public void setId_detail_tiket(String id_detail_tiket) {
        this.id_detail_tiket = id_detail_tiket;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNama_pemesan() {
        return nama_pemesan;
    }

    public void setNama_pemesan(String nama_pemesan) {
        this.nama_pemesan = nama_pemesan;
    }

    public int getId_wisata() {
        return id_wisata;
    }

    public void setId_wisata(int id_wisata) {
        this.id_wisata = id_wisata;
    }

    public String getNama_wisata() {
        return nama_wisata;
    }

    public void setNama_wisata(String nama_wisata) {
        this.nama_wisata = nama_wisata;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getKembalian() {
        return kembalian;
    }

    public void setKembalian(int kembalian) {
        this.kembalian = kembalian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Bitmap getQrCode() {
        return qrCode;
    }

    public void setQrCode(Bitmap qrCode) {
        this.qrCode = qrCode;
    }
}
