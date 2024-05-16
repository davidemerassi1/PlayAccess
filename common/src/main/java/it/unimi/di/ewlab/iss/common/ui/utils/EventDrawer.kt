package it.unimi.di.ewlab.iss.common.ui.utils


import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import it.unimi.di.ewlab.common.R
import it.unimi.di.ewlab.iss.common.utils.Utils.getColor
import it.unimi.di.ewlab.iss.common.model.Configuration
import it.unimi.di.ewlab.iss.common.model.Event
import it.unimi.di.ewlab.iss.common.model.EventType
import it.unimi.di.ewlab.iss.common.model.Link
import it.unimi.di.ewlab.iss.common.model.actions.Action
import java.util.*


class EventDrawer(var context: Context, private var displayWidth : Int, private var displayHeight : Int) {

    private var viewAdded: Boolean = false
    private var paint = Paint()
    private var textPaint = Paint()
    private var backgroundTextPaint = Paint()
    private var eventsBitmaps = hashMapOf<Int, Bitmap>()
    private var view: View = LinearLayout(context)
    private val windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
    private val layoutParams = WindowManager.LayoutParams()
    private lateinit var gamePalette: Palette

    init {
        view.isClickable = false
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        view.isLongClickable = false
        view.keepScreenOn = false
        layoutParams.height = displayHeight
        layoutParams.width = displayWidth
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.windowAnimations = android.R.style.Animation_Toast
        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
    }

    private fun createPaletteSync(bitmap: Bitmap?): Palette {
        return Palette.from(bitmap!!).generate()
    }
    fun getAllLinksOnImage(screenImage: Bitmap, originalLinks: List<Link>): Bitmap {
        val links = ArrayList<Link>()
        links.addAll(originalLinks)
        val workingBitmap: Bitmap = Bitmap.createBitmap(screenImage)
        gamePalette = createPaletteSync(workingBitmap)

        val mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        for (link in links) {
            fixOverlapPosition(link, links)
            drawEventOnImage(canvas, link.event, (link.markerSize / 1.3F).toInt(), link.markerColor)
        }

        return mutableBitmap
    }

    fun getEventOnImage(
        bitmap: Bitmap,
        eventType: EventType,
        x: Double, y: Double,
        markerSize: Int,
        markerColor: Int,
    ): Bitmap {
        val workingBitmap: Bitmap = Bitmap.createBitmap(bitmap)
        gamePalette = createPaletteSync(workingBitmap)

        val mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        drawEventOnImage(
            canvas,
            Event("", eventType, x, y),
            (markerSize / 1.3F).toInt(),
            markerColor
        )

        return mutableBitmap
    }

    private fun fixOverlapPosition(currentLink: Link, links: List<Link>): Link? {

        for (link in links) {
            if (link.event.y == currentLink.event.y && link != currentLink) {
                currentLink.event.x = (currentLink.event.x * 1.02f)
                currentLink.event.y = (currentLink.event.y * 1.02f)
                return fixOverlapPosition(currentLink, links)
            }
        }
        return currentLink
    }

    //Method to draw actions on the given image
    private fun drawActionOnImage(canvas: Canvas, action: Action, posX: Float, posY: Float) {

        val canvasHeight = canvas.height
        val canvasWidth = canvas.width
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER
        val actionName = action.name
        val actionPosX1 = posX - (canvasWidth * 0.35F)
        val actionPosX2 = posX + (canvasWidth * 0.35F)
        val actionPosY1 = posY - (canvasHeight * 0.07F)
        val actionPosY2 = posY - (canvasHeight * 0.03F)
        //DRAW ACTION
        textPaint.textSize = 60F
        canvas.drawRoundRect(
            RectF(actionPosX1, actionPosY1, actionPosX2, actionPosY2),
            30F,
            30F,
            backgroundTextPaint
        )
        canvas.drawText(actionName, posX, actionPosY2 - 20F, textPaint)

    }


    //Method to draw Events on the given image
    private fun drawEventOnImage(canvas: Canvas, event: Event, markerSize : Int, markerColor: Int) {
        val canvasHeight = canvas.height
        val canvasWidth = canvas.width

        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER

        backgroundTextPaint.style = Paint.Style.FILL
        backgroundTextPaint.color = Color.parseColor("#6200EE")

        val posX = event.x.toFloat() * canvasWidth
        val posY = event.y.toFloat() * canvasHeight


        val icon: Drawable?

        //DRAW EVENT

        val iconPositionLeft = (posX - markerSize*1.2).toInt()
        val iconPositionUp = (posY - markerSize*1.2).toInt()
        val iconPositionRight = (posX + markerSize*1.2).toInt()
        val iconPositionDown = (posY + markerSize*1.2).toInt()

        when (event.type!!) {
            EventType.TAP -> {
                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 1.8F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 1.5f, paint)
                icon = ContextCompat.getDrawable(context, R.drawable.sg_tap_icon_white)
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)
            }
            EventType.LONG_TAP_ON_OFF -> {

                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 2.2F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 2F, paint)
                icon = ContextCompat.getDrawable(context, R.drawable.sg_long_tap_on_off)
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)
            }
            EventType.LONG_TAP_TIMED -> {

                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 2.2F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 2F, paint)
                icon =
                    ContextCompat.getDrawable(context, R.drawable.sg_long_tap_timed_icon_white)
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)

            }
            EventType.LONG_TAP_INPUT_LENGTH -> {

                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 2.2F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 2F, paint)

                icon = ContextCompat.getDrawable(
                    context,
                    R.drawable.sg_long_tap_input_length_icon_white
                )
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)

            }
            EventType.SWIPE_DOWN -> {

                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 2.2F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 2F, paint)
                icon = ContextCompat.getDrawable(context, R.drawable.arrow_down_white)
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)

            }
            EventType.SWIPE_LEFT -> {

                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 2.2F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 2F, paint)
                icon = ContextCompat.getDrawable(context, R.drawable.arrow_left_white)
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)

            }
            EventType.SWIPE_UP -> {

                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 2.2F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 2F, paint)
                icon = ContextCompat.getDrawable(context, R.drawable.arrow_up_white)
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)

            }
            EventType.SWIPE_RIGHT -> {

                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 2.2F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 2F, paint)
                icon = ContextCompat.getDrawable(context, R.drawable.arrow_right_white)
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)

            }
            EventType.MONODIMENSIONAL_SLIDING -> {
                paint.color = Color.WHITE
                canvas.drawCircle(posX, posY, markerSize * 2.2F, paint)
                paint.color = context.getColor(getColor(markerColor))
                canvas.drawCircle(posX, posY, markerSize * 2F, paint)
                icon = ContextCompat.getDrawable(context, R.drawable.arrow_double_white)
                icon!!.setBounds(
                    iconPositionLeft, iconPositionUp, iconPositionRight,
                    iconPositionDown
                )
                icon.draw(canvas)
            }
        }


    }

    fun initEventsToDraw(configuration: Configuration){
        val conf = Bitmap.Config.ARGB_8888
        for ((index, link) in configuration.links.withIndex()) {
            val bmp = Bitmap.createBitmap(displayWidth, displayHeight, conf, true)
            val canvas = Canvas(bmp)
            drawEventOnImage(canvas, link.event, link.markerSize, index)
            eventsBitmaps[link.actionId] = bmp
        }
    }

    fun showEventOnDisplay(service: AccessibilityService, actionId: Int, hideImmediately: Boolean) {
        removeView()
        val bitmapDrawable = BitmapDrawable(service.resources, eventsBitmaps[actionId])
        view.background = bitmapDrawable

        if (!viewAdded) {
            try {
                windowManager.addView(view, layoutParams)
                viewAdded = true
            } catch (_: Exception) {
            }       // Called fromm wrong thread exceptions
        }
        view.visibility = View.VISIBLE

        if (hideImmediately)
            hideEventOnDisplayTimed()
    }

    fun hideEventOnDisplay() {
        if(viewAdded)
            view.visibility = View.GONE
    }

    private fun hideEventOnDisplayTimed(){
        if (viewAdded)
            view.postDelayed({ view.visibility = View.GONE }, 1200)
    }

    fun removeView(){
        if(viewAdded) {
            windowManager.removeView(view)
            viewAdded = false
        }
    }


}