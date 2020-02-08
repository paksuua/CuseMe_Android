package com.tistory.comfy91.excuseme_android.data

class SingletoneToken private constructor() {
    var token: String? = null

    companion object {
        @Volatile private var instance: SingletoneToken? = null

        @JvmStatic fun getInstance(): SingletoneToken =
            instance ?: synchronized(this) {
                instance ?: SingletoneToken().also {
                    instance = it
                }
            }
    }
}