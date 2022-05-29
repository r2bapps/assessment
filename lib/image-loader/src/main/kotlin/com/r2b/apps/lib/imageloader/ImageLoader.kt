package com.r2b.apps.lib.imageloader

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import okhttp3.OkHttpClient

interface ImageLoader {
    fun getImageLoader(): coil.ImageLoader
}

class ImageLoaderDelegate(context: Context, client: OkHttpClient?) : com.r2b.apps.lib.imageloader.ImageLoader {

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

    override fun getImageLoader(): coil.ImageLoader = coilImageLoader

}
