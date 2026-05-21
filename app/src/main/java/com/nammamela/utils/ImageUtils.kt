package com.nammamela.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUtils {

    private const val BASE64_PREFIX = "base64:"
    private const val MAX_WIDTH = 600
    private const val MAX_HEIGHT = 600
    private const val JPEG_QUALITY = 60

    /**
     * Reads an image from a content URI, compresses it, and returns a Base64-encoded string.
     * The returned string is prefixed with "base64:" for identification.
     */
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            // Resize to fit within max dimensions
            val scaledBitmap = scaleBitmap(originalBitmap, MAX_WIDTH, MAX_HEIGHT)

            // Compress to JPEG
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)

            // Convert to Base64
            val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            // Clean up
            if (scaledBitmap != originalBitmap) scaledBitmap.recycle()
            originalBitmap.recycle()
            outputStream.close()

            BASE64_PREFIX + base64String
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Decodes a Base64-encoded image string into a Bitmap.
     */
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val pureBase64 = base64String.removePrefix(BASE64_PREFIX)
            val decodedBytes = Base64.decode(pureBase64, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Loads an image into an ImageView from any source:
     * - Base64 string (prefixed with "base64:")
     * - Local file path (starts with "/")
     * - URL (starts with "http")
     */
    fun loadImage(context: Context, imageSource: String, imageView: ImageView, circleCrop: Boolean = false) {
        if (imageSource.isEmpty()) return

        when {
            imageSource.startsWith(BASE64_PREFIX) -> {
                val bitmap = base64ToBitmap(imageSource)
                if (bitmap != null) {
                    if (circleCrop) {
                        Glide.with(context).load(bitmap).circleCrop().into(imageView)
                    } else {
                        Glide.with(context).load(bitmap).into(imageView)
                    }
                }
            }
            imageSource.startsWith("/") -> {
                val file = File(imageSource)
                if (circleCrop) {
                    Glide.with(context).load(file).circleCrop().into(imageView)
                } else {
                    Glide.with(context).load(file).into(imageView)
                }
            }
            else -> {
                if (circleCrop) {
                    Glide.with(context).load(imageSource).circleCrop().into(imageView)
                } else {
                    Glide.with(context).load(imageSource).into(imageView)
                }
            }
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) return bitmap

        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
