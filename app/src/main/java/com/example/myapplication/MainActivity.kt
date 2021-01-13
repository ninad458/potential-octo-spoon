package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<ImageView>(R.id.image)
        val copyImage = findViewById<ImageView>(R.id.copy)

        view.post {
            val concatDrawable = concatDrawable(loadBitmapFromView(view), this)
//            copyImage.setImageBitmap(concatDrawable)
            val addTextToBitmap = drawTextToBitmap(
                this, concatDrawable, "Get the best jobs\n" +
                        "and advice on the apna app"
            )
            copyImage.setImageBitmap(addTextToBitmap)
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

private const val TAG = "MainActivity"
fun addTextToBitmap(activity: Activity, bitmap: Bitmap): Bitmap? {
    val b = Bitmap.createBitmap(
        bitmap.width,
        bitmap.height + 75,
        bitmap.config
    )
    val plain = ResourcesCompat.getFont(activity, R.font.firasans_black) ?: return null
    Log.d(TAG, "addTextToBitmap: $plain")
    val comboImage = Canvas(b)
    val paint = Paint()
    paint.typeface = plain
    comboImage.drawText("Sample text in bold", 0f, 0f, paint)
    return b
}

fun drawTextToBitmap(context: Context, bitmap: Bitmap, text: String): Bitmap? {
    return try {
        val resources: Resources = context.resources
        val scale: Float = resources.displayMetrics.density
        val bitmapConfig = bitmap.config ?: Bitmap.Config.ARGB_8888

        val copyBitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(copyBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val plain = ResourcesCompat.getFont(context, R.font.firasans_bold) ?: return null
        paint.color = Color.rgb(255, 255, 255)
        paint.textSize = 12 * scale
        paint.typeface = plain
        val bounds = Rect()
        val multiLinedTexts = text.split("\n")
        paint.getTextBounds(text, 0, multiLinedTexts[0].length, bounds)
        val x = 72 * scale
        val y = (copyBitmap.height + bounds.height()) / 5f
        canvas.drawText(multiLinedTexts[0], x, y, paint)
        canvas.drawText(text.split('\n')[1], x, y + paint.descent() - paint.ascent(), paint)
        copyBitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}