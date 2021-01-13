package com.example.myapplication

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat


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

fun generateSolidBitmap(context: Context, width: Int, height: Int): Bitmap? {
    val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawColor(ContextCompat.getColor(context, R.color.colorPrimary))
    return image
}

fun loadBitmapFromView(v: View): Bitmap {
    val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.draw(c)
    return b
}

private const val TAG = "MainActivity"

fun concatDrawable(originalBitmap: Bitmap, context: Context): Bitmap {
    val resources = context.resources

    val scale: Float = resources.displayMetrics.density

    val width: Int = originalBitmap.width
    val height: Int = originalBitmap.height

    val tpImg = drawTextToBitmap(
        context, generateSolidBitmap(context, width, 72 * scale.toInt())!!, "Get the best jobs\n" +
                "and advice on the apna app"
    )!!


    val ratio = width / tpImg.width

    val b = Bitmap.createBitmap(
        originalBitmap.width,
        originalBitmap.height + tpImg.height * ratio,
        originalBitmap.config
    )
    val comboImage = Canvas(b)

    val topImage = Bitmap.createScaledBitmap(tpImg, width, tpImg.height * ratio, true)

    comboImage.drawBitmap(originalBitmap, 0f, 0f, null)
    comboImage.drawBitmap(topImage, 0f, originalBitmap.height.toFloat(), null)
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
        val y = (copyBitmap.height - bounds.height()) / 2f
        canvas.drawText(multiLinedTexts[0], x, y, paint)
        canvas.drawText(multiLinedTexts[1], x, y + paint.descent() - paint.ascent(), paint)
        copyBitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}