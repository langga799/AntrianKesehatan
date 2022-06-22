package com.example.antriankesehatan.modelimport com.google.gson.annotations.SerializedNamedata class RiwayatResponse(	@field:SerializedName("data")	val data: List<DataItemRiwayat>,	@field:SerializedName("meta")	val meta: MetaRiwayat)data class DataItemRiwayat(	@field:SerializedName("jam_praktek")	val jamPraktek: JamPraktek,	@field:SerializedName("jam_praktek_id")	val jamPraktekId: Int,	@field:SerializedName("tanggal_pendaftaran")	val tanggalPendaftaran: String,	@field:SerializedName("antrian")	val antrian: String,	@field:SerializedName("created_at")	val createdAt: String,	@field:SerializedName("keluhan")	val keluhan: String,	@field:SerializedName("shiff")	val shiff: String,	@field:SerializedName("dokter")	val dokter: DokterRiwayat,	@field:SerializedName("transaksi")	val transaksi: String,	@field:SerializedName("updated_at")	val updatedAt: String,	@field:SerializedName("user_id")	val userId: Int,	@field:SerializedName("dokter_id")	val dokterId: Int,	@field:SerializedName("id")	val id: Int,	@field:SerializedName("status")	val status: String)data class DokterRiwayat(	@field:SerializedName("bidang_dokter")	val bidangDokter: String,	@field:SerializedName("code")	val code: String,	@field:SerializedName("updated_at")	val updatedAt: String,	@field:SerializedName("nama_dokter")	val namaDokter: String,	@field:SerializedName("created_at")	val createdAt: String,	@field:SerializedName("id")	val id: Int,	@field:SerializedName("photo_dokter")	val photoDokter: String)data class MetaRiwayat(	@field:SerializedName("code")	val code: Int,	@field:SerializedName("message")	val message: String,	@field:SerializedName("status")	val status: String)data class JamPraktek(	@field:SerializedName("hari_praktek_id")	val hariPraktekId: Int,	@field:SerializedName("jam_praktek")	val jamPraktek: String,	@field:SerializedName("updated_at")	val updatedAt: String,	@field:SerializedName("shift")	val shift: String,	@field:SerializedName("created_at")	val createdAt: String,	@field:SerializedName("id")	val id: Int)