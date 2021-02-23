package com.example.myapplication

import android.content.Context
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


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<ImageView>(R.id.image)
        val copyImage = findViewById<ImageView>(R.id.copy)

        view.post {
            val concatDrawable = this.generatePoweredByImage(loadBitmapFromView(view))
            copyImage.setImageBitmap(concatDrawable)
            view.visibility = View.GONE
        }
    }
}

fun loadBitmapFromView(v: View): Bitmap {
    val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.draw(c)
    return b
}

fun Context.generatePoweredByImage(originalBitmap: Bitmap): Bitmap {
    val scale = 1 / 7f
    val bannerHeight = originalBitmap.width * scale
    val destBitmap = Bitmap.createBitmap(
        originalBitmap.width,
        (originalBitmap.height + bannerHeight).toInt(),
        originalBitmap.config
    )
    val canvas = Canvas(destBitmap)

    canvas.drawRect(
        Rect(
            0,
            originalBitmap.height,
            originalBitmap.width,
            (originalBitmap.height + bannerHeight).toInt()
        ), Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(this@generatePoweredByImage, R.color.colorPrimary)
            style = Paint.Style.FILL
        })

    canvas.drawBitmap(
        originalBitmap,
        null,
        Rect(0, 0, originalBitmap.width, originalBitmap.height),
        Paint()
    )

    val logoDrawable: Drawable =
        ContextCompat.getDrawable(this, R.drawable.ic_logo)
            ?: throw Exception("Unable to fetch the drawable ic_logo")
    val apnaIconRect = getApnaIconRect(logoDrawable, bannerHeight.toInt())
    apnaIconRect.offset(0, originalBitmap.height)
    logoDrawable.bounds = apnaIconRect
    logoDrawable.draw(canvas)


    val playStoreDrawable: Drawable =
        ContextCompat.getDrawable(this, R.drawable.badge_copy)
            ?: throw Exception("Unable to fetch the drawable badge_copy")

    val playStoreRect =
        getPlaystoreRect(playStoreDrawable, bannerHeight.toInt(), originalBitmap.width)
    playStoreRect.offset(0, originalBitmap.height)
    playStoreDrawable.bounds = playStoreRect
    playStoreDrawable.draw(canvas)

    val ratio =
        (originalBitmap.width - (apnaIconRect.width() + playStoreRect.width())) / originalBitmap.width.toFloat()

    val textLeft = apnaIconRect.right + 40 / ratio
    val textRight = playStoreRect.left - 40 / ratio

    val textWidth = textRight - textLeft

    val textLayout =
        drawTextToBitmap(
            this,
            "Get the best jobs and advice on the apna app",
            textWidth.toInt(),
            ratio
        )

    val textHeight = textLayout.height
    val textY: Float = (bannerHeight - textHeight) / 2f
    val savePoint = canvas.save()
    canvas.translate(textLeft, textY + originalBitmap.height)
    textLayout.draw(canvas)
    canvas.restoreToCount(savePoint)

    return destBitmap
}

fun getApnaIconRect(logoDrawable: Drawable, height: Int): Rect {
    val ratio = logoDrawable.intrinsicWidth / logoDrawable.intrinsicHeight

    val margin = height / 5
    val expectedHeight = height - 2 * margin

    val expectedWidth = expectedHeight * ratio

    return Rect(0, 0, expectedWidth, expectedHeight).apply {
        offset(margin, margin)
    }
}

fun getPlaystoreRect(playStoreBitmap: Drawable, height: Int, width: Int): Rect {
    val marginLogo = height / 6
    val expectedHeight = height - marginLogo * 2

    val expectedWidth =
        playStoreBitmap.intrinsicWidth * expectedHeight / playStoreBitmap.intrinsicHeight

    return Rect(
        width - marginLogo - expectedWidth,
        marginLogo,
        width - marginLogo,
        marginLogo + expectedHeight
    )
}

fun drawTextToBitmap(context: Context, text: String, width: Int, ratio: Float): StaticLayout {
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    val plain = ResourcesCompat.getFont(context, R.font.firasans_bold)
    paint.color = Color.rgb(255, 255, 255)
    paint.textSize = 22 / ratio
    paint.typeface = plain
    return StaticLayout(
        text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
    )
}