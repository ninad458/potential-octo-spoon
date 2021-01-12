package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mView = findViewById<ImageView>(R.id.image)
        val takeScreenshotOfPost = takeScreenshotOfPost(mView)
        Log.d("zzzzzz", "onCreate: $takeScreenshotOfPost")
        mView.post {
//            findViewById<ImageView>(R.id.copy).setImageBitmap(loadBitmapFromView(mView))
            findViewById<ImageView>(R.id.copy).setImageBitmap(
                concatDrawable(
                    loadBitmapFromView(
                        mView
                    ), this
                )
            )
        }
    }
}

fun takeScreenshotOfPost(mView: View?): Uri? {
    val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/DumDum"
    val dir = File(folderPath)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val path = dir.absolutePath + "/invite" + ".png"
    mView?.isDrawingCacheEnabled = true
    val bitmap: Bitmap? = Bitmap.createBitmap(mView?.drawingCache ?: return null)
    mView.isDrawingCacheEnabled = false
    val imageFile = File(path)
    val outputStream = FileOutputStream(imageFile)
    val quality = 100
    bitmap?.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
    outputStream.flush()
    outputStream.close()

    return FileProvider.getUriForFile(mView.context, "com.example.myapplication", File(path))

}

fun loadBitmapFromView(v: View): Bitmap {
    val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.draw(c)
    return b
}

fun concatDrawable(originalBitmap: Bitmap, context: Context): Bitmap {
    val resources = context.resources

    val width: Int = originalBitmap.width
    val height: Int = originalBitmap.height

    val drbl: Drawable = resources.getDrawable(R.drawable.badge_copy)

    val tpImg = (drbl as BitmapDrawable?)!!.bitmap

    val ratio = width / tpImg.width

    val b = Bitmap.createBitmap(
        originalBitmap.width,
        originalBitmap.height + tpImg.height * ratio,
        originalBitmap.config
    )
    val comboImage = Canvas(b)

    drbl.setBounds(0, /*orignalBitmap.height*/0, height, width)

    val topImage = Bitmap.createScaledBitmap(tpImg, width, tpImg.height * ratio, true)

    comboImage.drawBitmap(originalBitmap, 0f, 0f, null)
    comboImage.drawBitmap(topImage, 0f, originalBitmap.height.toFloat(), null)
    return b
}