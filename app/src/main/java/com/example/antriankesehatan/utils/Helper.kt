package com.example.antriankesehatan.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.network.NetworkConfig.Companion.BASE_URL
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun File?.toMultipartBody(name: String = "file"): MultipartBody.Part? {
    if (this == null) return null
    val reqFile: RequestBody = this.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, this.name, reqFile)
}

fun Context.loadImageCircle(url: String, image: CircleImageView?) {
    if (image != null) {
        Glide.with(this)
            .load(url)
            .centerCrop()
            .into(image)
    }
}

fun Context.loadImageView(url: String, image: ImageView) {
    Glide.with(this)
        .load(url)
        .centerCrop()
        .into(image)
}

object Helper {
    const val BASE_IMAGE_URL_DOKTER = "http://192.168.43.99:8000/public/photo_dokter/"
    const val BASE_IMAGE_URL_USER = "${BASE_URL}storage/"
}
