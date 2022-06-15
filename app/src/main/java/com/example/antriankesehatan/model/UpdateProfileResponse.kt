package com.example.antriankesehatan.modelimport com.google.gson.annotations.SerializedNamedata class UpdateProfileResponse(	@field:SerializedName("data")	val data: DataUpdateProfile,	@field:SerializedName("meta")	val meta: MetaUpdateProfile)data class MetaUpdateProfile(	@field:SerializedName("code")	val code: Int,	@field:SerializedName("message")	val message: String,	@field:SerializedName("status")	val status: String)data class DataUpdateProfile(	@field:SerializedName("photoProfile")	val photoProfile: String,	@field:SerializedName("no_tlp")	val noTlp: String,	@field:SerializedName("updated_at")	val updatedAt: String,	@field:SerializedName("roles")	val roles: String,	@field:SerializedName("name")	val name: String,	@field:SerializedName("created_at")	val createdAt: String,	@field:SerializedName("email_verified_at")	val emailVerifiedAt: Any,	@field:SerializedName("id")	val id: Int,	@field:SerializedName("jenis_kelamin")	val jenisKelamin: String,	@field:SerializedName("email")	val email: String,	@field:SerializedName("alamat")	val alamat: String)