package com.r2b.apps.lib.imageloader

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import okhttp3.OkHttpClient

interface ImageLoader {
    fun load(url: String, imageView: ImageView, @DrawableRes placeholder: Int?)
}

class ImageLoaderDelegate(context: Context, client: OkHttpClient?) : com.r2b.apps.lib.imageloader.ImageLoader {

    // TODO: Configure to avoid redirect to image not available on marvel api
    // https://i.annihil.us/u/prod/marvel/i/mg/b/40/image_not_available.jpg

    private val coilImageLoader =
        ImageLoader.Builder(context)
            .apply { client?.let { okHttpClient(client)  } }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()

    override fun load(url: String, imageView: ImageView, @DrawableRes placeholder: Int?) {
        val request = ImageRequest.Builder(imageView.context)
            .data(url)
            .target(imageView)
            .apply { placeholder?.let { placeholder(placeholder) } }
            .build()
        coilImageLoader.enqueue(request)
    }

}
