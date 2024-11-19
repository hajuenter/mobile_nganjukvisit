package com.polije.sem3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventModel {
    @Expose
    @SerializedName("id_event")
    private String idEvent;
    @SerializedName("nama_event")
    private String nama;
    private String lokasi;
    @SerializedName("tanggal_event")
    private String tanggaldanwaktu;
    private String hari;
    @SerializedName("deskripsi_event")
    private String deskripsi;
    @SerializedName("contact_person")
    private String contactPerson;
    private String gambar;

    public EventModel(String nama, String lokasi, String tanggaldanwaktu, String hari, String idEvent, String contactPerson, String gambar) {
        this.nama = nama;
        this.lokasi = lokasi;
        this.tanggaldanwaktu = tanggaldanwaktu;
        this.hari = hari;
        this.idEvent = idEvent;
        this.contactPerson = contactPerson;
        this.gambar = gambar;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public void setHari(String hari) {
        this.hari = hari;
    }
    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }
    public void setTanggaldanwaktu(String tanggaldanwaktu) {
        this.tanggaldanwaktu = tanggaldanwaktu;
    }
    public String getNama() {
        return nama;
    }
    public String getHari() {
        return hari;
    }
    public String getLokasi() {
        return lokasi;
    }
    public String getTanggaldanwaktu() {
        return tanggaldanwaktu;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
