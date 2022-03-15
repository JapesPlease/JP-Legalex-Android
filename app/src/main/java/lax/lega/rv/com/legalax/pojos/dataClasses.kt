package lax.lega.rv.com.legalax.pojos

import java.io.Serializable


data class LawyerSelectedCat(
    val cat_id: Int,
    val cat_info: LawyerSelectedCatInfo,
    val created_at: String,
    val id: Int,
    val lawyer_id: Int,
    val updated_at: String
):Serializable

data class LawyerSelectedCatInfo(
    val created_at: String,
    val id: Int,
    val name: String,
    val updated_at: String
):Serializable