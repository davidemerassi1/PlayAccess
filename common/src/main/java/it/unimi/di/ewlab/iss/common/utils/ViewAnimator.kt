package it.unimi.di.ewlab.iss.common.utils

import android.animation.*
import android.content.res.ColorStateList
import android.graphics.*
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.animation.ArgbEvaluatorCompat
import it.unimi.di.ewlab.common.R

object ViewAnimator {

    private const val ZOOM_ANIMATION_DURATION = 300L

    // Permette l'ingrandimento della ImageView smallerView a tutto schermo
    // con un'animazione di zoom al tocco dell'immagine. biggerView deve essere
    // una ImageView con visibility View.GONE, con una View padre e con stessa bitmap di
    // smallerView.
    fun animateImageViewZoom(smallerView: ImageView, biggerView: ImageView) {
        zoomIn(smallerView, biggerView)
    }

    private fun zoomIn(smallerView: ImageView, biggerView: ImageView) {
        biggerView.apply {
            visibility = View.VISIBLE
            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)

            // Calculate the starting and ending bounds for the zoomed-in image.
            val startBoundsInt = Rect()
            val finalBoundsInt = Rect()
            val globalOffset = Point()

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the
            // container view. Set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            smallerView.getGlobalVisibleRect(startBoundsInt)
            (biggerView.parent as View).getGlobalVisibleRect(finalBoundsInt, globalOffset)
            startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
            finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

            val startBounds = RectF(startBoundsInt)
            val finalBounds = RectF(finalBoundsInt)

            // Using the "center crop" technique, adjust the start bounds to be the
            // same aspect ratio as the final bounds. This prevents unwanted
            // stretching during the animation. Calculate the start scaling factor.
            // The end scaling factor is always 1.0.
            val startScale: Float
            if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
                // Extend start bounds horizontally.
                startScale = startBounds.height() / finalBounds.height()
                val startWidth: Float = startScale * finalBounds.width()
                val deltaWidth: Float = (startWidth - startBounds.width()) / 2
                startBounds.left -= deltaWidth.toInt()
                startBounds.right += deltaWidth.toInt()
            } else {
                // Extend start bounds vertically.
                startScale = startBounds.width() / finalBounds.width()
                val startHeight: Float = startScale * finalBounds.height()
                val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
                startBounds.top -= deltaHeight.toInt()
                startBounds.bottom += deltaHeight.toInt()
            }

            animateZoomToLargeImage(biggerView, startBounds, finalBounds, startScale)

            setDismissLargeImageAnimation(biggerView, startBounds, startScale)
        }
    }

    private fun animateZoomToLargeImage(
        biggerView: ImageView,
        startBounds: RectF,
        finalBounds: RectF,
        startScale: Float
    ) {
        biggerView.apply {
            // Set the pivot point for SCALE_X and SCALE_Y transformations to the
            // top-left corner of the zoomed-in view. The default is the center of
            // the view.
            pivotX = 0f
            pivotY = 0f

            val transparent = Color.TRANSPARENT
            val semitransparent = ContextCompat.getColor(biggerView.context, R.color.black_semitransparent)
            val black = ContextCompat.getColor(biggerView.context, R.color.black)

            setBackgroundColor(black)

            // Construct and run the parallel animation of the four translation and
            // scale properties: X, Y, SCALE_X, and SCALE_Y.
            AnimatorSet().apply {
                play(
                    ObjectAnimator.ofFloat(
                        biggerView,
                        View.X,
                        startBounds.left,
                        finalBounds.left)
                ).apply {
                    with(ObjectAnimator.ofFloat(biggerView, View.Y, startBounds.top, finalBounds.top))
                    with(ObjectAnimator.ofFloat(biggerView, View.SCALE_X, startScale, 1F))
                    with(ObjectAnimator.ofFloat(biggerView, View.SCALE_Y, startScale, 1F))

                    val backgroundAnimator = ValueAnimator.ofObject(
                        ArgbEvaluatorCompat(), transparent, semitransparent
                    )
                    backgroundAnimator.addUpdateListener { animation ->
                        biggerView.backgroundTintList = ColorStateList.valueOf(
                            animation.animatedValue as Int
                        )
                    }

                    with(backgroundAnimator)
                }
                duration = ZOOM_ANIMATION_DURATION
                interpolator = DecelerateInterpolator()
                start()
            }
        }
    }

    private fun setDismissLargeImageAnimation(
        biggerView: ImageView,
        startBounds: RectF,
        startScale: Float
    ) {
        // When the zoomed-in image is tapped, it zooms down to the original
        // bounds and shows the thumbnail instead of the expanded image.
        biggerView.setOnClickListener {
            biggerView.setBackgroundColor(Color.TRANSPARENT)

            // Animate the four positioning and sizing properties in parallel,
            // back to their original values.
            AnimatorSet().apply {
                play(
                    ObjectAnimator.ofFloat(biggerView, View.X, startBounds.left)
                ).apply {
                    with(ObjectAnimator.ofFloat(biggerView, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(biggerView, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(biggerView, View.SCALE_Y, startScale))
                }
                duration = ZOOM_ANIMATION_DURATION
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        biggerView.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        biggerView.visibility = View.GONE
                    }
                })
                start()
            }
        }
    }
}