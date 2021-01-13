package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<ImageView>(R.id.image)
        val copyImage = findViewById<ImageView>(R.id.copy)

        view.post {
            val concatDrawable = concatDrawable(loadBitmapFromView(view), this)
            copyImage.setImageBitmap(concatDrawable)
        }
    }
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

    drbl.setBounds(0, 0, height, width)

    val topImage = Bitmap.createScaledBitmap(tpImg, width, tpImg.height * ratio, true)

    comboImage.drawBitmap(originalBitmap, 0f, 0f, null)
    comboImage.drawBitmap(topImage, 0f, originalBitmap.height.toFloat(), null)
    return b
}