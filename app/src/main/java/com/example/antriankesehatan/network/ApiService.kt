package com.example.antriankesehatan.network

import com.example.antriankesehatan.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    // ======================================= Register
    @FormUrlEncoded
    @POST("api/register")
    fun requestRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("no_tlp") noTelp: String,
        @Field("jenis_kelamin") gender: String,
        @Field("alamat") address: String,
        @Field("no_bpjs") bpjs: String,
    ): Call<RegisterResponse>


    // ======================================= Login
    @FormUrlEncoded
    @POST("api/login")
    fun requestLogin(
        @Header("access_token") apiKey: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>


    // ======================================= Get Profile User
    @GET("api/user")
    fun getProfile(
        @Header("Authorization") token: String,
    ): Call<GetProfileResponse>


    // ======================================= Logout User
    @POST("api/logout")
    fun requestLogout(
        @Header("Authorization") apiKey: String,
        @Header("Accept") accept: String = "application/json",
    ): Call<LogoutResponse>


    // ======================================= Get List Doctor
    @GET("api/dokter")
    fun getListDoctor(
        @Header("Authorization") token: String,
    ): Call<GetDoctorResponse>


    // ======================================= Upload Photo User
    @Multipart
    @POST("api/user/photo")
    fun uploadPhoto(
        @Header("Authorization") apiKey: String,
        @Part file: MultipartBody.Part,
    ): Call<ResponseBody>


    // ======================================= Update Profile User
    @FormUrlEncoded
    @POST("api/user")
    fun updateProfile(
        @Header("Accept") json: String,
        @Header("Authorization") apiKey: String,
        @Field("name") name: String,
        @Field("no_tlp") telp: String,
        @Field("jenis_kelamin") gender: String,
        @Field("alamat") address: String,
        @Field("no_bpjs") bpjs: String,
    ): Call<UpdateProfileResponse>


    // ======================================= Register Patient
    @FormUrlEncoded
    @POST("api/pendaftaran")
    fun setScheduleAntrian(
        @Header("Authorization") token: String,
        @Field("dokter_id") doctorId: String,
        @Field("jam_praktek_id") jamPraktekId: String,
        @Field("shiff") shiff: String,
        @Field("tanggal_pendaftaran") tanggalPendaftaran: String,
        @Field("transaksi") transaksi: String,
        @Field("keluhan") keluhan: String,
    ): Call<SetScheduleAntrianResponse>


    // ======================================= Get Data Antrian Patient
    @GET("api/antrian")
    fun getNoAntrian(
        @Header("Authorization") token: String,
    ): Call<GetAntrianResponse>


    // ======================================= Get Searching Data Dokter
    @GET("api/dokter")
    fun searchDataDokter(
        @Header("Authorization") token: String,
        @Query("nama_dokter") query: String,
    ): Call<GetDoctorResponse>


    // ======================================= Get Riwayat
    @GET("api/riwayat")
    fun getHistoryPatient(
        @Header("Authorization") token: String,
    ): Call<RiwayatResponse>


    // ======================================= Get Antrian Saat Ini
    @GET("api/antrian")
    fun getAntrianSaatIni(
        @Header("Authorization") token: String,
        @Query("now") query: String,
    ): Call<CurrentAntrianResponse>

}