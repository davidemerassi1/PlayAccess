package it.unimi.di.ewlab.iss.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Base64
import androidx.core.net.toUri
import it.unimi.di.ewlab.common.R
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt


object Utils {

    fun decodeBase64ToBitmap(encodedImage: String): Bitmap {
        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun encodeBitmapToBase64String(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    //Uri parameter is string conversion of Uri
    fun decodeUriToBitmap(uri: String, context: Context): Bitmap{
        val imageUri = uri.toUri()
        val imageStream: InputStream? = imageUri.let {
            context.contentResolver?.openInputStream(it)
        }
        return BitmapFactory.decodeStream(imageStream)
    }

    fun decodeUriToBase64String(uri: String, context: Context): String{
        return encodeBitmapToBase64String(decodeUriToBitmap(uri, context))
    }

    private val colors = listOf(
        R.color.defaultPointerColor,
        R.color.emerald,
        R.color.blue,
        R.color.fucsia,
        R.color.orange,
        R.color.porpora,
        R.color.light_blue,
        R.color.yellow,
        R.color.pink,
        R.color.dark_green
    )

    fun getColor(num: Int): Int {
        return if (num < colors.size) {
            colors[num]
        } else {
            R.color.defaultPointerColor
        }
    }

    fun imageToBitmap(context: Context, image: Image, rotationDegrees: Float): Bitmap {
        val bm = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        val yuvToRgbConverter = YuvToRgbConverter(context)
        yuvToRgbConverter.yuvToRgb(image, bm)
        return rotateAndFlipXBitmap(bm, rotationDegrees)
    }

    private fun rotateAndFlipXBitmap(bm: Bitmap, rotationDegrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees)
        matrix.postScale(-1F, 1F, bm.width / 2F, bm.height / 2F)
        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
    }

    private fun flipX(bm: Bitmap): Bitmap {
        val matrix = Matrix().apply { postScale(-1F, 1F, bm.width / 2F, bm.height / 2F) }
        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
    }

    fun rotateDegreesBitmap(bitmap: Bitmap, rotationDegrees: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees)
        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            bitmap.width, bitmap.height, true
        )
        return Bitmap.createBitmap(
            scaledBitmap, 0, 0,
            scaledBitmap.width, scaledBitmap.height, matrix, true
        )
    }

    fun getBitmapFromDrawable(drawable: Drawable?): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null)
            return drawable.bitmap

        if (drawable == null || drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0)
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        val bmp = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    fun getDrawableFromBitmap(context: Context, bitmap: Bitmap): Drawable {
        return BitmapDrawable(context.resources, bitmap)
    }

    private fun normalize(f: FloatArray): FloatArray {
        val normArray = FloatArray(f.size)
        var sum = 0f
        for (i in f.indices) {
            sum += f[i].pow(2)
        }
        for (i in f.indices) {
            normArray[i] = f[i] / sqrt(sum)
        }
        return normArray
    }

    fun euclideanDistance(pos1: Position3D, pos2: Position3D): Float {
        val xDiff = pos1.x - pos2.x
        val yDiff = pos1.y - pos2.y
        val zDiff = pos1.z - pos2.z
        return sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff)
    }

    fun midPoint(pos1: Position3D, pos2: Position3D): Position3D {
        return Position3D(
            (pos1.x + pos2.x) / 2,
            (pos1.y + pos2.y) / 2,
            (pos1.z + pos2.z) / 2
        )
    }

    fun encodeToBase64(bmp: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return java.util.Base64.getMimeEncoder().encodeToString(byteArrayOutputStream.toByteArray())
    }

    fun decodeFromBase64(encoded: String): Bitmap? {
        return try {
            val decodedBytes = java.util.Base64.getMimeDecoder().decode(encoded)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Area del poligono definito dai punti in points
    // La coordinata z viene ignorata
    fun areaBetweenPoints2D(points: List<Position3D>): Float {
        var area = 0F
        for (i in points.indices) {
            val point1 = points[i]
            val point2 = points[(i + 1) % points.size]
            area += point1.x * point2.y + point2.y * point1.x
        }
        return abs(area) / 2
    }

    fun centerOfGravity(points: List<Position3D>): Position3D {
        var xSum = 0F
        var ySum = 0F
        var zSum = 0F

        for (point in points) {
            xSum += point.x
            ySum += point.y
            zSum += point.z
        }

        return Position3D(
            xSum / points.size,
            ySum / points.size,
            zSum / points.size
        )
    }

    fun angleBetweenSegmentAndY(point1: Position3D, point2: Position3D): Float {
        return atan2(
            point1.x - point2.x, point1.y - point2.y
        )
    }
}