package com.example.myapplication

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap


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

    val apnaIcon = addApnaIcon(resources, originalBitmap)!!
    val withPlayStoreIcon = addPlayStoreIcon(resources, apnaIcon)!!
    val tpImg = drawTextToBitmap(
        context, withPlayStoreIcon, "Get the best jobs and advice on the apna app"
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

fun addApnaIcon(resources: Resources, bitmap: Bitmap): Bitmap? {
    val scale = resources.displayMetrics.density

    val bannerHeight = 72 * scale.toInt()

    val logoDrawable: Drawable = resources.getDrawable(R.drawable.ic_logo)

    val b = Bitmap.createBitmap(bitmap.width, bannerHeight, bitmap.config)

    val comboImage = Canvas(b)

    comboImage.drawRect(Rect(0, 0, b.width, b.height), Paint().apply {
        isAntiAlias = true
        color = Color.RED
        style = Paint.Style.FILL
    })


    val marginLogo = 16 * scale.toInt()

    val ratio = logoDrawable.intrinsicWidth / logoDrawable.intrinsicHeight

    val expectedHeight = comboImage.height - (marginLogo * 2)

    val expectedWidth = expectedHeight * ratio

    val rect = Rect(0, 0, expectedWidth, expectedHeight).apply {
        offset(marginLogo, marginLogo)
    }

    logoDrawable.bounds = rect

    logoDrawable.draw(comboImage)

    return b
}

fun addPlayStoreIcon(resources: Resources, bitmap: Bitmap): Bitmap? {
    val scale = resources.displayMetrics.density

    val playStoreDrawable: Drawable = resources.getDrawable(R.drawable.badge_copy)
    val playStoreBitmap = playStoreDrawable.toBitmap()

    val b = Bitmap.createBitmap(
        bitmap.width,
        bitmap.height,
        bitmap.config
    )
    val comboImage = Canvas(b)

    val height = bitmap.height / 1.8f
    val width = playStoreBitmap.width * height / playStoreBitmap.height
    val topImage = Bitmap.createScaledBitmap(playStoreBitmap, width.toInt(), height.toInt(), true)

    comboImage.drawBitmap(bitmap, 0f, 0f, null)
    val marginLogo = 8 * scale

    val dest =
        RectF(
            bitmap.width - marginLogo - width,
            bitmap.height / 4f,
            bitmap.width - marginLogo,
            bitmap.height / 4f + height
        )
//    dest.offset(-marginLogo, -marginLogo)

    comboImage.drawBitmap(topImage, null, dest, null)
    return b
}

fun drawTextToBitmap(context: Context, bitmap: Bitmap, text: String): Bitmap? {
    return try {
        val resources: Resources = context.resources
        val scale: Float = resources.displayMetrics.density
        val bitmapConfig = bitmap.config ?: Bitmap.Config.ARGB_8888

        val textWidth: Int = bitmap.width - (250 * scale).toInt()

        val copyBitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(copyBitmap)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        val plain = ResourcesCompat.getFont(context, R.font.firasans_bold) ?: return null
        paint.color = Color.rgb(255, 255, 255)
        paint.textSize = 12 * scale
        paint.typeface = plain
        val textLayout = StaticLayout(
            text, paint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
        )

        val textHeight = textLayout.height

        val x: Float = (bitmap.width - textWidth) / 2f - 20 * scale
        val y: Float = (bitmap.height - textHeight) / 2f

        val savePoint = canvas.save()
        canvas.translate(x, y)
        textLayout.draw(canvas)
        canvas.restoreToCount(savePoint)
        copyBitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}