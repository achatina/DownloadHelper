package com.downloadhelper

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.download_helper.Download
import com.download_helper.Downloader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), Download.OnDownloadListener{
    override fun onSuccess(url: String, file: File) {
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
        progress.text = file.absolutePath
    }

    override fun onProgressUpdate(currentProgress: Int, total: Int) {
        val loadingProgress = "Loading: $currentProgress/$total"
        progress.text = loadingProgress
    }

    override fun onFailure(e: Exception) {
        val error = "Error: ${e.message}"
        progress.text = error
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        load.setOnClickListener {
            Downloader.init(this, Handler(), this)
                .download(url.text.toString())
        }
    }
}
