package com.example.antriankesehatan.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkConfig {

    companion object{
        const val BASE_URL = "http://192.168.43.99:8000/"
       // const val BASE_URL = "http://192.45.132.86:8000/"
      //  private const val BASE_URL = "http://192.168.1.2:8000/"
       // private const val BASE_URL = "http://192.168.1.9:8000/"
    }

    fun getApiService(): ApiService {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//            .connectTimeout(120, TimeUnit.MILLISECONDS)
//            .callTimeout(120, TimeUnit.MILLISECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

}