package com.example.antriankesehatan.modelimport com.google.gson.annotations.SerializedNamedata class SearchDokterResponse(	@field:SerializedName("data")	val data: DataSearchDokter,	@field:SerializedName("meta")	val meta: MetaSearchDokter)data class MetaSearchDokter(	@field:SerializedName("code")	val code: Int,	@field:SerializedName("message")	val message: String,	@field:SerializedName("status")	val status: String)data class LinksItemSearchDokter(	@field:SerializedName("active")	val active: Boolean,	@field:SerializedName("label")	val label: String,	@field:SerializedName("url")	val url: Any)data class DataSearchDokter(	@field:SerializedName("per_page")	val perPage: Int,	@field:SerializedName("data")	val data: List<DataItemSearchDokter>,	@field:SerializedName("last_page")	val lastPage: Int,	@field:SerializedName("next_page_url")	val nextPageUrl: Any,	@field:SerializedName("prev_page_url")	val prevPageUrl: Any,	@field:SerializedName("first_page_url")	val firstPageUrl: String,	@field:SerializedName("path")	val path: String,	@field:SerializedName("total")	val total: Int,	@field:SerializedName("last_page_url")	val lastPageUrl: String,	@field:SerializedName("from")	val from: Int,	@field:SerializedName("links")	val links: List<LinksItemSearchDokter>,	@field:SerializedName("to")	val to: Int,	@field:SerializedName("current_page")	val currentPage: Int)data class DataItemSearchDokter(	@field:SerializedName("bidang_dokter")	val bidangDokter: String,	@field:SerializedName("code")	val code: Any,	@field:SerializedName("updated_at")	val updatedAt: String,	@field:SerializedName("nama_dokter")	val namaDokter: String,	@field:SerializedName("created_at")	val createdAt: String,	@field:SerializedName("id")	val id: Int,	@field:SerializedName("hari_praktek")	val hariPraktek: List<Any>,	@field:SerializedName("photo_dokter")	val photoDokter: String)