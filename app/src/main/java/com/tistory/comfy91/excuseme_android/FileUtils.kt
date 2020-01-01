package com.tistory.comfy91.excuseme_android

import android.net.Uri
import com.tistory.comfy91.excuseme_android.data.SingletoneToken


class FileUtils private constructor() {


    companion object {
        @Volatile
        private var instance: FileUtils? = null

        @JvmStatic
        fun getInstance(): FileUtils =
            instance ?: synchronized(this) {
                instance ?: FileUtils().also {
                    instance = it
                }
            }

        private const val MIME_TYPE_AUDIO = "audio/*"
        private const val MIME_TYPE_TEXT = "text/*"
        private const val MIME_TYPE_IMAGE = "image/*"
        private const val MIME_TYPE_VIDEO = "video/*"
        private const val MIME_TYPE_APP = "application/*"

        private const val HIDDEN_PREFIX = "."

        fun getExtension(uri: String): String? {
            if (uri == null) {
                return null
            }

            val dot = uri.lastIndexOf (".")
            if (dot >= 0) {
                return uri.substring(dot);
            } else {
                // No extension.
                return ""
            }
        }

        fun isLocal(url: String?): Boolean {
            return if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
                true
            } else false
        }

        fun isMediaUri(uri: Uri): Boolean {
            return "media".equals(uri.getAuthority(), ignoreCase = true)
        }

    }
}