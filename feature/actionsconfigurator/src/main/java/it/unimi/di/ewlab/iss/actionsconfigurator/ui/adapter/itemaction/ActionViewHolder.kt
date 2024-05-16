package it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.itemaction

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.ActionItemBinding
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action

class ActionViewHolder(
    view: ActionItemBinding
) : RecyclerView.ViewHolder(view.root){

    private val binding: ActionItemBinding = view

    fun bind(item: ActionItem){
        binding.viewmodel = item

        val context = binding.actionItem.context
        var layoutBtn: Drawable? = null
        var iconBtn: Drawable? = null
        var deleteBtnVisibility = View.VISIBLE
        when (item.actionType){
            Action.ActionType.BUTTON -> {
                //Spezializzazione button
                layoutBtn = ContextCompat.getDrawable(context, R.drawable.blue_button_border)
                iconBtn = ContextCompat.getDrawable(context, R.drawable.controller_blue)
            }
            Action.ActionType.VOCAL -> {
                // Spezializzazione vocal
                layoutBtn = ContextCompat.getDrawable(context, R.drawable.green_button_border)
                iconBtn = ContextCompat.getDrawable(context, R.drawable.vocal)
            }
            Action.ActionType.SCREEN_GESTURE -> {
                // Spezializzazione screengesture
                layoutBtn = ContextCompat.getDrawable(context, R.drawable.orange_button_border)
                iconBtn = ContextCompat.getDrawable(context, R.drawable.screen_gesture)
                deleteBtnVisibility = View.INVISIBLE
            }
            Action.ActionType.FACIAL_EXPRESSION -> {
                // Spezializzazione facial expressions
                layoutBtn = ContextCompat.getDrawable(context, R.drawable.red_button_border)
                iconBtn = ContextCompat.getDrawable(context, R.drawable.facial_expression_white)
                if (item.name == MainModel.getInstance().neutralFacialExpressionAction?.name)
                    deleteBtnVisibility = View.INVISIBLE
            }
            else -> {}
        }

        binding.actionItem.background= layoutBtn
        binding.iconInfo.setImageDrawable(iconBtn)
        binding.actionName.text = item.name
        binding.deleteAction.visibility = deleteBtnVisibility
        binding.actionItem.setOnClickListener {
            item.onClick.invoke(item)
        }
        binding.deleteAction.setOnClickListener {
            item.onDelete.invoke(item)
        }

    }
}