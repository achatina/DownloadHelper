# Download Helper

A small library to help developers download file.

## Getting Started

To start dowload just do like that:

```Downloader.init(context, Handler(), this).download(URL)```
                        
Then implement Download.OnDownloadListener interface (or pass it as a third varable in init() mehtod)

```override fun onSuccess(url: String, file: File) {
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
    }

    override fun onProgressUpdate(currentProgress: Int, total: Int) {
        val loadingProgress = "Loading: $currentProgress/$total"
        Log.d(TAG, loadingProggress)
    }

    override fun onFailure(e: Exception) {
        val error = "Error: ${e.message}"
        Log.e(TAG, error)
    }```

And don't forget about permissions! It needs this permissions: INTERNTER, WRITE/READ_EXTERNAL_STORAGE and ACCESS_NETWORK_STATE.

And that's all. It will return to you a File object in onSuccess and it's url, so you can keep it's path and url where you whant and use it.

### Downloading modes

There are three download methods (for now).

1. It will give a filesDir path and default name as 8 last charakters of the url, or last chars after '/' 
```download(url: String)```

2. It will have a filesDir path and your custom name
```download(url: String, fileName: String)```

3. In this method you can give yout custom names and custom path
```download(url: String, path: String, fileName: String)```
