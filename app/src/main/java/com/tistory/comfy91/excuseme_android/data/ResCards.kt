package com.tistory.comfy91.excuseme_android.data

import com.google.gson.annotations.SerializedName

/**
 * success = 성공여부
 * message = ex: "카드 전체 조회 성공"
 */
data class ResCards(
    val success: Boolean,
    val message: String,
    val data: List<CardBean>?
)

