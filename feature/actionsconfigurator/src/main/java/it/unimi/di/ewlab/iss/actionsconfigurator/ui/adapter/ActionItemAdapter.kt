package it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.itemaction.ActionItem
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.itemaction.ActionViewHolder

class ActionItemAdapter(private val eventList: List<ActionItem> = listOf()) : RecyclerView.Adapter<ActionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ActionViewHolder(
            DataBindingUtil.inflate(
                inflater,
                R.layout.action_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount() = eventList.size

}