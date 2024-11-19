package com.polije.sem3.model;

public class TiketModel {
    private int id_tiket;
    private int id_wisata;
    private String nama_wisata;
    private int harga_tiket;
    private int jumlah;
    private String tanggal;
    private String status;
    private String nama_pemesan;

    // Getter dan Setter
    public int getId_tiket() {
        return id_tiket;
    }

    public void setId_tiket(int id_tiket) {
        this.id_tiket = id_tiket;
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

    public int getHarga_tiket() {
        return harga_tiket;
    }

    public void setHarga_tiket(int harga_tiket) {
        this.harga_tiket = harga_tiket;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNama_pemesan() {
        return nama_pemesan;
    }

    public void setNama_pemesan(String nama_pemesan) {
        this.nama_pemesan = nama_pemesan;
    }
}
