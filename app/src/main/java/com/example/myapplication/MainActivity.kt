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


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<ImageView>(R.id.image)
        val copyImage = findViewById<ImageView>(R.id.copy)

        view.post {
            val concatDrawable = concatDrawable(loadBitmapFromView(view), this)
            copyImage.setImageBitmap(concatDrawable)
            view.visibility = View.GONE
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

fun concatDrawable(originalBitmap: Bitmap, context: Context): Bitmap {
    val resources = context.resources

    val scale: Float = resources.displayMetrics.density

    val bannerHeight = 72 * scale.toInt()
    val destBitmap = Bitmap.createBitmap(
        originalBitmap.width,
        originalBitmap.height + bannerHeight,
        originalBitmap.config
    )
    val canvas = Canvas(destBitmap)

    canvas.drawRect(
        Rect(
            0,
            originalBitmap.height,
            originalBitmap.width,
            originalBitmap.height + bannerHeight
        ), Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            style = Paint.Style.FILL
        })

    canvas.drawBitmap(
        originalBitmap,
        null,
        Rect(0, 0, originalBitmap.width, originalBitmap.height),
        Paint()
    )

    val logoDrawable: Drawable = resources.getDrawable(R.drawable.ic_logo)
    val apnaIconRect = getApnaIconRect(logoDrawable, bannerHeight)
    apnaIconRect.offset(0, originalBitmap.height)
    logoDrawable.bounds = apnaIconRect
    logoDrawable.draw(canvas)


    val playStoreDrawable: Drawable = resources.getDrawable(R.drawable.badge_copy)
    val playstoreRect = getPlaystoreRect(playStoreDrawable, bannerHeight, originalBitmap.width)
    playstoreRect.offset(0, originalBitmap.height)
    playStoreDrawable.bounds = playstoreRect
    playStoreDrawable.draw(canvas)

    val textLeft = apnaIconRect.right + 40 * scale
    val textRight = playstoreRect.left - 40 * scale

    val textWidth = textRight - textLeft

    val textLayout =
        drawTextToBitmap(context, "Get the best jobs and advice on the apna app", textWidth.toInt())

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

    val margin = height / 4
    val expectedHeight = height - 2 * margin

    val expectedWidth = expectedHeight * ratio

    return Rect(0, 0, expectedWidth, expectedHeight).apply {
        offset(margin, margin)
    }
}

fun getPlaystoreRect(playStoreBitmap: Drawable, height: Int, width: Int): Rect {
    val expectedHeight = height / 1.8f

    val expectedWidth =
        playStoreBitmap.intrinsicWidth * expectedHeight / playStoreBitmap.intrinsicHeight

    val marginLogo = height / 4f

    return Rect(
        (width - marginLogo - expectedWidth).toInt(),
        marginLogo.toInt(),
        (width - marginLogo).toInt(),
        (marginLogo + expectedHeight).toInt()
    )
}

fun drawTextToBitmap(context: Context, text: String, width: Int): StaticLayout {
    val resources: Resources = context.resources
    val scale: Float = resources.displayMetrics.density
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    val plain = ResourcesCompat.getFont(context, R.font.firasans_bold)
    paint.color = Color.rgb(255, 255, 255)
    paint.textSize = 30 * scale
    paint.typeface = plain
    return StaticLayout(
        text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
    )
}