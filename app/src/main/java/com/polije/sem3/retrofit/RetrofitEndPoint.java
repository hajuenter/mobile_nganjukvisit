package com.polije.sem3.retrofit;

import com.polije.sem3.model.BookingModel;
import com.polije.sem3.response.BookingResponse;
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
import com.polije.sem3.response.TiketResponse;
import com.polije.sem3.response.UlasanKirimResponse;
import com.polije.sem3.response.UlasanResponse;
import com.polije.sem3.response.UlasanResponse1;
import com.polije.sem3.response.UserResponse;
import com.polije.sem3.response.VerificationResponse;
import com.polije.sem3.response.WisataResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitEndPoint {
//======================================PAGE AWAL====================================
    //API Login:
    @FormUrlEncoded
    @POST("APIakun.php")
    Call<UserResponse> login(
            @Field("action") String action,
            @Field("email") String email,
            @Field("password") String password
    );

    //API
    @FormUrlEncoded
    @POST("dataUserApi.php")
    Call<UserResponse> addToken(
            @Field("email") String email,
            @Field("token") String token
    );

    //API Login dengan Google:
    @FormUrlEncoded
    @POST("APIakun.php")
    Call<UserResponse> logingoogle(
            @Field("action") String action,
            @Field("email") String userEmail
    );

    //API Register:
    @FormUrlEncoded
    @POST("APIakun.php")
    Call<UserResponse> register(
            @Field("action") String action,
            @Field("alamat") String alamat,
            @Field("email") String email,
            @Field("nama") String fullName,
            @Field("password") String password
        );
    //API OTP:
    @FormUrlEncoded
    @POST("kirimOtpApi.php")
    Call<VerificationResponse> sendmailotp (
            @Field("email") String email
    );
    //API LUPA PASSWORD
    @FormUrlEncoded
    @POST("APIakun.php")
    Call<UserResponse> lupapass (
            @Field("action") String action,
            @Field("email") String email,
            @Field("password") String password
    );


    //=================================================HOME=============================================
    @GET("APIhome.php")
    Call<DetailEventResponse> detailevent(
            @Query("action") String action,
            @Query("id_detail") String id_selected
    );
    //API Ambil Detail Row Wisata:
    @GET("get_detail_wisata.php")
    Call<WisataResponse> wisata(
    );
    //API Ambil Detail Row Event:
    @GET("get_detail_event.php")
    Call<EventResponse> event(
    );
    //API Ambil Detail Row Penginapan:
    @GET("get_detail_penginapan.php")
    Call<PenginapanResponse> penginapan(
    );
    //API Ambil Detail Row Kuliner:
    @GET("get_detail_kuliner.php")
    Call<KulinerResponse> kuliner(
    );
    //API Ambil Rekomendasi Penginapan:
    @GET("APIhome.php")
    Call<PenginapanResponse> penginapanpopuler(
            @Query("action") String action
    );
    //API Ambil Wisata Terpopuler:
    @GET("APIhome.php")
    Call<WisataResponse> wisatapopuler(
            @Query("action") String action
    );
    //API Ambil Kuliner Terpopuler:
    @GET("APIhome.php")
    Call<KulinerResponse> kulinerpopuler(
            @Query("action") String action
    );
    //API AMBIL DATA EVENT
    @GET("APIhome.php")
    Call<EventResponse> upcomingevent(
            @Query("action") String action
    );

    //API AMBIL DATA WISATA
    @GET("APIhome.php")
    Call<DetailWisataResponse> detailwisata(
            @Query("action") String action,
            @Query("id_detail") String id_selected
    );
    //API AMBIL DATA KULINER
    @GET("APIhome.php")
    Call<DetailKulinerResponse> detailkuliner(
            @Query("action") String action,
            @Query("id_detail") String idKuliner
    );
    //API AMBIL DATA PENGINAPAN
    @GET("APIhome.php")
    Call<DetailPenginapanResponse> detailpenginapan(
            @Query("action") String action,
            @Query("id_detail") String idPenginapan
    );
    //=================================================ULASAN=============================================
    @GET("APIhome.php")
    Call<UlasanResponse> ulasan(
            @Query("action") String action,
            @Query("table") String table,
            @Query("id_foreign") String id_selected
    );

    @GET("APIhome.php")
    Call<UlasanKirimResponse> ulasansaya(
            @Query("action") String action,
            @Query("table") String table,
            @Query("id_foreign") String id_selected,
            @Query("id_user") String idpengguna
    );


    @FormUrlEncoded
    @POST("APIhome.php")
    Call<UlasanKirimResponse> kirimulasan(
            @Query("action") String action,
            @Field("table") String table,
            @Field("id_user") String idPengguna,
            @Field("nama") String namaPengguna,
            @Field("komentar") String comment,
            @Field("rating") String rating,
            @Field("id_foreign") String idWisata
    );

    @FormUrlEncoded
    @POST("APIhome.php")
    Call<UlasanResponse1> editulasan(
            @Query("action") String action,
            @Field("table") String table,
            @Field("komentar") String comment,
            @Field("id_foreign") String idwisata,
            @Field("rating") Float rating,
            @Field("nama") String nama,
            @Field("id_user") String idpengguna
    );
    @FormUrlEncoded
    @POST("APIhome.php")
    Call<UlasanResponse> deleteulasan(
            @Query("action") String action,
            @Field("table") String table,
            @Field("id_user") String idpengguna,
            @Field("id_foreign") String idwisata
    );
    //=================================================PROFIL=============================================
    @FormUrlEncoded
    @POST("APIakun.php")
    Call<UserResponse> updateprofiles(
            @Field("action") String action,
            @Field("id_user") String idUser,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("no_hp") String no_hp
    );



    //=================================================FAVORIT=============================================
    //API TAMPIL FAV WISATA
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritWisataResponse>favwisata(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser
    );
    //API TAMPIL FAV PENGINAPAN
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritPenginapanResponse> favpenginapan(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser
    );
    //API TAMPIL FAV KULINER
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritKulinerResponse> favkuliner(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser
    );

    //API Tambah Fav Wisata:
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritWisataResponse> tambahfavwisata(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //API CEK FAV WISATA
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritWisataResponse> cekfavwisata(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //API HAPUS FAV WISATA
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritWisataResponse> deletefavwisata(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //API TAMBAH FAV PENGINAPAN
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritPenginapanResponse> tambahfavpenginapan(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //API CEK FAV PENGINAPAN
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritPenginapanResponse> cekfavpenginapan(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //API HAPUS FAV PENGINAPAN
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritPenginapanResponse> deletefavpenginapan(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //API TAMBAH FAV KULINER
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritKulinerResponse> tambahfavkuliner(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //API CEK FAV KULINER
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritKulinerResponse> cekfavkuliner(
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //API HAPUS FAV KULINER
    @FormUrlEncoded
    @POST("APIfavorit.php")
    Call<FavoritKulinerResponse> deletefavkuliner (
            @Field("action") String action,
            @Field("kategori") String kategori,
            @Field("id_user") String iduser,
            @Field("id_detail") String idDetail
    );
    //================================SEARCHING=========================================================
    //API SEARCHING WISATA
    @GET("APIhome.php")
    Call<WisataResponse> cariwisata (
            @Query("action") String action,
            @Query("key") String keyId,
            @Query("value") String value
    );
    //API SEARCHING PENGINAPAN
    @GET("APIhome.php")
    Call<PenginapanResponse> caripenginapan (
            @Query("action") String action,
            @Query("key") String keyId,
            @Query("value") String value
    );
    //API SEARCHING KULINER
    @GET("APIhome.php")
    Call<KulinerResponse> carikuliner (
            @Query("action") String action,
            @Query("key") String keyId,
            @Query("value") String value

    );
    //API SET FOTO PROFIL
    @FormUrlEncoded
    @POST("../user_profiles/show_profiles.php")
    Call<ResponseGetGambarProfil> getGambar(
            @Field("idPengguna") String idPengguna
    );

    //==================================BOOKING=====================================================

    // Mendapatkan tiket berdasarkan user
    @GET("APItiket.php")
    Call<TiketResponse> getTiketUser(
            @Query("action") String action,
            @Query("id_user") String idUser
    );

    // Mencari tiket berdasarkan nama wisata
    @GET("APItiket.php")
    Call<TiketResponse> searchTiket(
            @Query("action") String action,
            @Query("id_user") String id_user,
            @Query("search") String searchTerm
    );

    @POST("APItiket.php")  // Sesuaikan dengan file PHP yang sesuai
    Call<BookingResponse> createBooking(
            @Query("action") String action,    // Menambahkan parameter action dalam URL
            @Body BookingModel bookingModel    // Data pemesanan tiket
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

    @GET("APIhome.php")
    Call<NotifyResponse> notifevent(
            @Query("action") String action,
            @Query("tipe") String tipe
    );
    @GET("APIhome.php")
    Call<NotifyResponse> notifuser (
            @Query("action") String action,
            @Query("tipe") String tipe,
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
