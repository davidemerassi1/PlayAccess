package it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.facialexpression.FrameViewHolder
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame

class FacialExpressionAdapter(context: Context, private val frames: List<Frame>, private val zoomView: ImageView?):
    RecyclerView.Adapter<FrameViewHolder>()
{
    private val inflater = LayoutInflater.from(java.util.Objects.requireNonNull(context))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
        val view = inflater.inflate(R.layout.facial_expression_frame, parent, false)
        return FrameViewHolder(view, zoomView)
    }

    override fun getItemCount(): Int {
        return frames.size
    }

    override fun onBindViewHolder(viewHolder: FrameViewHolder, index: Int) {
        viewHolder.updateContent(frames[index])
    }
}