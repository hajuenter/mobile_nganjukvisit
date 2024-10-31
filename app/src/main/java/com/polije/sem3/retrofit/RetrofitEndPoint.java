package com.polije.sem3.retrofit;

import com.polije.sem3.model.NotifyModelNew;
import com.polije.sem3.response.DetailEventResponse;
import com.polije.sem3.response.DetailKulinerResponse;
import com.polije.sem3.response.DetailPenginapanResponse;
import com.polije.sem3.response.DetailWisataResponse;
import com.polije.sem3.response.EventResponse;
import com.polije.sem3.response.FavoritKulinerResponse;
import com.polije.sem3.response.FavoritPenginapanResponse;
import com.polije.sem3.response.FavoritWisataResponse;
import com.polije.sem3.response.KulinerResponse;
import com.polije.sem3.response.NganjukVisitResponse;
import com.polije.sem3.response.NotifyResponse;
import com.polije.sem3.response.PenginapanResponse;
import com.polije.sem3.response.ResponseGetGambarProfil;
import com.polije.sem3.response.SendNotifResponse;
import com.polije.sem3.response.UlasanKirimResponse;
import com.polije.sem3.response.UlasanResponse;
import com.polije.sem3.response.UserResponse;
import com.polije.sem3.response.VerificationResponse;
import com.polije.sem3.response.WisataResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitEndPoint {
    @FormUrlEncoded
    @POST("loginApi.php")
    Call<UserResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );
    @FormUrlEncoded
    @POST("dataUserApi.php")
    Call<UserResponse> addToken(
            @Field("email") String email,
            @Field("token") String token
    );


    @FormUrlEncoded
    @POST("login_google.php")
    Call<UserResponse> logingoogle(
            @Field("email") String userEmail,
            @Field("device_token") String deviceToken
    );

    @FormUrlEncoded
    @POST("registerApi.php")
    Call<UserResponse> register(
            @Field("alamat") String alamat,
            @Field("email") String email,
            @Field("nama") String fullName,
            @Field("password") String password
        );

    @GET("data_wisata.php")
    Call<WisataResponse> wisata(
    );

    @GET("data_event.php")
    Call<EventResponse> event(
    );

    @GET("data_penginapan.php")
    Call<PenginapanResponse> penginapan(
    );

    @GET("data_kuliner.php")
    Call<KulinerResponse> kuliner(
    );

    @GET("endpoint_penginapan_rekomendasi.php")
    Call<PenginapanResponse> penginapanpopuler(
    );

    @GET("endpoint_wisata_populer.php")
    Call<WisataResponse> wisatapopuler(
    );

    @GET("endpoint_kuliner_terpopuler.php")
    Call<KulinerResponse> kulinerpopuler(
    );

    @GET("upcoming_event.php")
    Call<EventResponse> upcomingevent();

    @GET("detailed_data_wisata.php")
    Call<DetailWisataResponse> detailwisata(
            @Query("id_selected") String id_selected
    );

    @GET("detailed_data_kuliner.php")
    Call<DetailKulinerResponse> detailkuliner(
            @Query("id_selected") String idKuliner
    );

    @GET("detailed_data_penginapan.php")
    Call<DetailPenginapanResponse> detailpenginapan(
            @Query("id_selected") String idPenginapan
    );

    @GET("data_ulasan.php")
    Call<UlasanResponse> ulasan(
            @Query("id_selected") String id_selected
    );

    @GET("ulasan_saya.php")
    Call<UlasanKirimResponse> ulasansaya(
            @Query("id_selected") String id_selected,
            @Query("idPengguna") String idpengguna
    );

    @FormUrlEncoded
    @POST("tambah_ulasan.php")
    Call<UlasanKirimResponse> kirimulasan(
            @Field("idPengguna") String idPengguna,
            @Field("nama_pengguna") String namaPengguna,
            @Field("comment") String comment,
            @Field("wisataid") String idWisata
    );

    @FormUrlEncoded
    @POST("edit_ulasan.php")
    Call<UlasanResponse> editulasan(
            @Field("comment") String comment,
            @Field("wisataid") String idwisata,
            @Field("idPengguna") String idpengguna
    );

    @GET("delete_ulasan.php")
    Call<UlasanResponse> deleteulasan(
            @Query("idPengguna") String idpengguna,
            @Query("wisataid") String idwisata
    );

    @FormUrlEncoded
    @POST("update_profiles.php")
    Call<UserResponse> updateprofiles(
            @Field("iduser") String idUser,
            @Field("fullname") String fullname,
            @Field("email") String email,
            @Field("alamat") String alamat,
            @Field("notelp") String notelp
    );

    @GET("detailed_data_event.php")
    Call<DetailEventResponse> detailevent(
            @Query("id_selected") String idSelected
    );

    @FormUrlEncoded
    @POST("fav_wisata.php")
    Call<FavoritWisataResponse> favwisata(
        @Field("id_user") String iduser
    );

    @FormUrlEncoded
    @POST("fav_penginapan.php")
    Call<FavoritPenginapanResponse> favpenginapan(
            @Field("id_user") String iduser
    );

    @FormUrlEncoded
    @POST("fav_kuliner.php")
    Call<FavoritKulinerResponse> favkuliner(
            @Field("id_user") String iduser
    );

    @FormUrlEncoded
    @POST("../user_profiles/show_profiles.php")
    Call<ResponseGetGambarProfil> getGambar(
            @Field("idPengguna") String idPengguna
    );

    @GET("tambahfavorit_wisata.php")
    Call<FavoritWisataResponse> tambahfavwisata(
            @Query("id_pengguna") String idPengguna,
            @Query("id_wisata") String idWisata
    );

    @GET("cekfav_wisata.php")
    Call<FavoritWisataResponse> cekfavwisata(
            @Query("id_pengguna") String idPengguna,
            @Query("id_wisata") String idWisata
    );

    @GET("deletefav_wisata.php")
    Call<FavoritWisataResponse> deletefavwisata(
            @Query("id_pengguna") String idPengguna,
            @Query("id_wisata") String idWisata
    );

    @GET("tambahfavorit_penginapan.php")
    Call<FavoritPenginapanResponse> tambahfavpenginapan(
            @Query("id_pengguna") String idPengguna,
            @Query("id_penginapan") String idPenginapan
    );

    @GET("cekfav_penginapan.php")
    Call<FavoritPenginapanResponse> cekfavpenginapan(
            @Query("id_pengguna") String idPengguna,
            @Query("id_penginapan") String idPenginapan
    );

    @GET("deletefav_penginapan.php")
    Call<FavoritPenginapanResponse> deletefavpenginapan(
            @Query("id_pengguna") String idPengguna,
            @Query("id_penginapan") String idPenginapan
    );

    @GET("tambahfavorit_kuliner.php")
    Call<FavoritKulinerResponse> tambahfavkuliner(
        @Query("id_pengguna") String idPengguna,
        @Query("id_kuliner") String idKuliner
    );

    @GET("cekfav_kuliner.php")
    Call<FavoritKulinerResponse> cekfavkuliner(
            @Query("id_pengguna") String idPengguna,
            @Query("id_kuliner") String idKuliner
    );

    @GET("deletefav_kuliner.php")
    Call<FavoritKulinerResponse> deletefavkuliner (
            @Query("id_pengguna") String idPengguna,
            @Query("id_kuliner") String idKuliner
    );

    @GET("searching/search_wisata.php")
    Call<WisataResponse> cariwisata (
            @Query("key_value") String keyId
    );

    @GET("searching/search_penginapan.php")
    Call<PenginapanResponse> caripenginapan (
            @Query("key_value") String keyId
    );

    @GET("searching/search_kuliner.php")
    Call<KulinerResponse> carikuliner (
            @Query("key_value") String keyId
    );

    @FormUrlEncoded
    @POST("OTP/mail.php")
    Call<VerificationResponse> sendmailotp (
            @Field("email") String email, @Field("type") String type, @Field("action") String action
    );

    @FormUrlEncoded
    @POST("OTP/lupa_password.php")
    Call<UserResponse> lupapass (
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("koneksi/cek_koneksi.php") Call<NganjukVisitResponse> cekKoneksi();

    // ============================== tidak digunakan ==================================
    // mengirimkan notifikasi
    @FormUrlEncoded
    @POST("notif/notif1.php")
    Call<SendNotifResponse> welcomenotif (
            @Field("title") String Title,
            @Field("body") String BodyMsg,
            @Field("device_token") String deviceToken
    );

    // menampilkan notifikasi
    @GET("notif/getnotifuser.php")
    Call<UserResponse> getnotif (
            @Query("id_user") String idpengguna
    );

    // =========================================================================================

    @GET("notif/mynotification.php")
    Call<NotifyResponse> mynotif (
            @Query("id_user") String idpengguna
    );

    // add new session manual login
    @FormUrlEncoded
    @POST("add_session.php")
    Call<UserResponse> addintosession (
            @Field("email") String email,
            @Field("device_token") String deviceToken
    );

}
