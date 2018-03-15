@file: JvmName("Download")
package com.download_helper

import java.io.File

/**
 * Created by Nikita on 12.03.2018.
 */
interface Download {

    fun download(url: String)

    fun download(url: String, fileName: String)

    fun download(url: String, path: String, fileName: String)

    interface OnDownloadListener{
        fun onSuccess(url: String, file: File)

        fun onProgressUpdate(currentProgress: Int, total: Int)

        fun onFailure(e: Exception)
    }
}