package com.example.antriankesehatan.network

import com.example.antriankesehatan.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.*


interface ApiService {

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
    ): Call<RegisterResponse>


    @FormUrlEncoded
    @POST("api/login")
    fun requestLogin(
        @Header("access_token") apiKey: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>


    @POST("api/logout")
    fun requestLogout(
        @Header("Authorization") apiKey: String,
        @Header("Accept") accept: String = "application/json",
    ): Call<LogoutResponse>


    @GET("api/dokter")
    fun getListDoctor(
        @Header("Authorization") token:String
    ): Call<GetDoctorResponse>


    @Multipart
    @POST("api/user/photo")
    fun uploadPhoto(
        @Header("Authorization") apiKey: String,
        @Part file: MultipartBody.Part,
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("api/user")
    fun updateProfile(
        @Header("Accept") json: String,
        @Header("Authorization") apiKey: String,
        @Field("name") name: String,
        @Field("no_tlp") telp: String,
        @Field("jenis_kelamin") gender: String,
        @Field("alamat") address: String,
    ): Call<UpdateProfileResponse>


}