package com.polije.sem3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModel {

    @Expose
    @SerializedName("id_user")
    private String idUser;
    @SerializedName("nama")
    private String nama;
    @SerializedName("password")
    private String passwordUser;
/*    @SerializedName("fullname")
    private String fullName;*/
    @SerializedName("email")
    private String emailUser;
    @SerializedName("role")
    private String levelUser;
    @SerializedName("no_hp")
    private String noTelepon;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("gambar")
    private String gambar;

    public UserModel(String idUser, String username, String passwordUser, String emailUser, String noTelepon, String alamat, String gambar) {
        this.idUser = idUser;
        this.nama = username;
        this.passwordUser = passwordUser;
/*
        this.fullName = fullName;
*/
        this.emailUser = emailUser;
        this.noTelepon = noTelepon;
        this.alamat = alamat;
        this.gambar = gambar;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return nama;
    }

    public void setUsername(String username) {
        this.nama = username;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }

    /*public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
*/
    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public String getLevelUser() {
        return levelUser;
    }

    public void setLevelUser(String levelUser) {
        this.levelUser = levelUser;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
