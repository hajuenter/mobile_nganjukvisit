package com.polije.sem3.model;

public class BookingModel {
    private String id_wisata;
    private String id_user;
    private String tanggal;
    private String jumlah;
    private String harga_tiket;
    private String nama_wisata;
    private String status;
    private String total;

    // Constructor
    public BookingModel(String Id_wisata, String id_user,
                        String tanggal, String jumlah,
                        String harga_tiket, String nama_wisata, String status,String total) {

        this.id_wisata = Id_wisata;
        this.id_user = id_user;
        this.tanggal = tanggal;
        this.jumlah = jumlah;
        this.harga_tiket = harga_tiket;
        this.nama_wisata = nama_wisata;
        this.status = status;
        this.total = total;
    }


    public String getId_wisata() {
        return id_wisata;
    }

    public void setId_wisata(String Id_wisata) {
        this.id_wisata = Id_wisata;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }





    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String member_type) {
        this.jumlah = member_type;
    }


    public String getHarga_tiket() {
        return harga_tiket;
    }

    public void setHarga_tiket(String harga_tiket) {
        this.harga_tiket = harga_tiket;
    }

    public String getNama_wisata() {
        return nama_wisata;
    }

    public void setNama_wisata(String nama_wisata) {
        this.nama_wisata = nama_wisata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
