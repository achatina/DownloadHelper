@file:  JvmName("Downloader")
package com.download_helper

import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by Nikita on 12.03.2018.
 */
class Downloader(private val context: Context,
                 private val uiThread: Handler,
                 private val listener: Download.OnDownloadListener) : Download {

    private val BUFFER_LENGTH = 1024
    private val NOTIFY_PERIOD = 150 * 1024

    companion object {
        @JvmStatic
        fun init(context: Context, uiThread: Handler, listener: Download.OnDownloadListener)
                : Downloader = Downloader(context, uiThread, listener)

        @JvmStatic
        fun extractNameFromUrl(url: String)
                : String = url.substring(url.lastIndexOf('/') + 1)

    }

    override fun download(url: String) {
        download(url, context.filesDir.path, extractNameFromUrl(url))
    }

    override fun download(url: String, fileName: String) {
        download(url, context.filesDir.path, fileName)
    }

    override fun download(url: String, path: String, fileName: String) = Thread(Runnable {
        var fileOutput: FileOutputStream? = null
        try {
            val file = File(context.filesDir, fileName)

            if (!isDeviceOnline(context)) {
                if (file.exists()) {
                    notifySuccess(url, file)
                    return@Runnable
                } else {
                    //in this case it can be empty so we need to delete
                    file.delete()
                    notifyFailure(Exception("No connection"))
                    return@Runnable
                }
            }

            fileOutput = FileOutputStream(file)
            val urlObj = URL(url)
            val urlConnection = urlObj.openConnection() as HttpURLConnection
            val totalSize = urlConnection.contentLength
            val buffer = ByteArray(BUFFER_LENGTH)
            var bufferLength = 0
            var downloadedSize = 0
            var counter = 0
            val input = BufferedInputStream(urlConnection.inputStream)
            bufferLength = input.read(buffer)

            while (bufferLength > 0) {
                fileOutput.write(buffer, 0, bufferLength)
                downloadedSize += bufferLength
                counter += bufferLength
                if (counter > NOTIFY_PERIOD) {
                    notifyProgress(downloadedSize, totalSize)
                    counter = 0
                }
                bufferLength = input.read(buffer)
            }

            notifySuccess(url, file)
            urlConnection.disconnect()
            fileOutput.close()


        } catch (e: MalformedURLException) {
            notifyFailure(e)
        } catch (e: IOException) {
            notifyFailure(e)
        } finally {
            if (fileOutput != null) {
                try {
                    fileOutput.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }).start()

    private fun notifySuccess(url: String, pdfFile: File) {
        uiThread.post { listener.onSuccess(url, pdfFile) }
    }

    private fun notifyFailure(e: Exception) {
        uiThread.post { listener.onFailure(e) }
    }

    private fun notifyProgress(downloadedSize: Int, totalSize: Int) {
        uiThread.post { listener.onProgressUpdate(downloadedSize, totalSize) }
    }


    /**
     * Needs ACCESS_NETWORK_STATE permission
     */
    fun isDeviceOnline(context: Context) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}