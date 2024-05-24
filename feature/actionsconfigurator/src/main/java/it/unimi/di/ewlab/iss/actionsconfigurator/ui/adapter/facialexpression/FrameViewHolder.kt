package it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.facialexpression

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FacialExpressionFrameBinding
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame
import it.unimi.di.ewlab.iss.common.utils.ViewAnimator


class FrameViewHolder(view: View, private val zoomView: ImageView?) : ViewHolder(view) {

    private val binding = FacialExpressionFrameBinding.bind(view)


    fun updateContent(frame: Frame) {
        binding.framePreview.setImageBitmap(frame.bitmap)
        binding.framePreview.setOnClickListener {
            zoomView?.let {
                it.setImageBitmap(frame.bitmap)
                ViewAnimator.animateImageViewZoom(binding.framePreview, it)
            }
        }
    }
}