package it.unimi.di.ewlab.iss.common.configuratorsmodels

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import it.unimi.di.ewlab.common.R
import it.unimi.di.ewlab.common.databinding.BaseDialogBinding
import it.unimi.di.ewlab.iss.common.model.actions.Action

open class BaseDialog : Dialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener)

    private val binding: BaseDialogBinding by lazy {
        BaseDialogBinding.inflate(layoutInflater)
    }


    interface BaseDialogListener {
        fun onPrimaryButtonClicked()
        fun onSecondaryButtonClicked()
        fun onCheckClicked(checked: Boolean)
    }

    enum class Color {
        GREEN, RED, ORANGE, BLUE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(binding.root)
        val window: Window? = this.window
        if (null != window) {
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            //Set width to 90%
            val displayMetrics = context.resources.displayMetrics
            val displayWidth = displayMetrics.widthPixels
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            layoutParams.width = (displayWidth * 0.9f).toInt()
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            window.attributes = layoutParams

            this.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    var errorDialog: Boolean = false
        set(value){
            if (value) {
                setColors(Color.RED)
                binding.confirmButtonContainer.setBackgroundResource(R.drawable.green_button_background)
                warnDialog = false
            }
            field = value
        }

    var warnDialog: Boolean = false
        set(value){
            if(value) {
                setColors(Color.ORANGE)
                errorDialog = false
            }
            field = value
        }

    @DrawableRes
    var icon: Int = 0
        set(value) {
            binding.icon = ContextCompat.getDrawable(context, value)
            field = value
        }

    @ColorInt
    var iconTint: Int = 0
        set(value) {
            binding.iconTint = value
            field = value
        }

    @StringRes
    var titleText: Int? = null
        set(value) {
            binding.title = value?.let { context.getText(it) }
            field = value
        }

    @ColorInt
    var textColor: Int = 0
        set(value) {
            binding.textColor = value
            field = value
        }

    @StringRes
    var subTitleText: Int? = null
        set(value) {
            binding.text = value?.let { context.getText(it) }
            field = value
        }

    var checkText: Int? = null
        set(value) {
            binding.checkText = value?.let { context.getText(it) }
            field = value
        }

    var textPrimaryButton: Int? = null
        set(value) {
            binding.textPrimaryButton = value?.let { context.getText(it) }
            field = value
        }

    var textSecondaryButton: Int? = null
        set(value) {
            binding.textSecondaryButton = value?.let { context.getString(it) }
            field = value
        }

    var hideCloseButton: Boolean = false
        set(value) {
            binding.hideCloseButton = value
            field = value
        }

    var checked: Boolean
        get() = binding.check ?: false
        set(value) {
            binding.checkModal.isChecked = value
        }

    var listener: BaseDialogListener? = null
        set(value) {
            binding.listener = value
            binding.checkModal.setOnCheckedChangeListener { _, isChecked ->
                value?.onCheckClicked(isChecked)
            }
            field = value
        }

    fun setTitleString(title: String?) {
        binding.title = title
    }

    fun setSubTitleString(subTitle: String?) {
        binding.text = subTitle
    }

    fun setIconDrawable(icon: Drawable?) {
        binding.icon = icon
    }

    fun setTextPrimaryButtonString(value: String?) {
        binding.textPrimaryButton = value
    }

    fun setTextSecondaryButtonString(value: String?) {
        binding.textSecondaryButton = value
    }

    fun setColorsByActionType(actionType: Action.ActionType) {
        when (actionType) {
            Action.ActionType.FACIAL_EXPRESSION -> {
                setColors(Color.RED)
            }
            Action.ActionType.BUTTON -> {
                setColors(Color.BLUE)
            }
            Action.ActionType.SCREEN_GESTURE -> {
                setColors(Color.ORANGE)
            }
            Action.ActionType.VOCAL -> {
                setColors(Color.GREEN)
            }
        }

        errorDialog = false
        warnDialog = false
    }

    fun setColors(color: Color) {
        val backgroundRes: Int
        val confirmBackgroundRes: Int
        val textColorRes: Int
        when (color) {
            Color.GREEN -> {
                backgroundRes = R.drawable.green_button_border
                confirmBackgroundRes = R.drawable.green_button_background
                textColorRes = R.color.emerald
            }
            Color.BLUE -> {
                backgroundRes = R.drawable.blue_button_border
                confirmBackgroundRes = R.drawable.blue_button_background
                textColorRes = R.color.blue
            }
            Color.ORANGE -> {
                backgroundRes = R.drawable.orange_button_border
                confirmBackgroundRes = R.drawable.orange_button_background
                textColorRes = R.color.dark_orange
            }
            Color.RED -> {
                backgroundRes = R.drawable.red_button_border
                confirmBackgroundRes = R.drawable.red_button_background
                textColorRes = R.color.red
            }
        }

        val background = ContextCompat.getDrawable(context, backgroundRes)
        val confirmBackground = ContextCompat.getDrawable(context, confirmBackgroundRes)
        binding.generalContainer.background = background
        binding.confirmButtonContainer.background = confirmBackground
        binding.textColor = ContextCompat.getColor(context, textColorRes)
    }

    companion object {
        @JvmStatic
        fun createAndShowInfoDialog(
            context: Context,
            title: String,
            description: String,
            color: Color = Color.GREEN
        ) {
            val dialog = BaseDialog(context).apply {
                setTitleString(title)
                setSubTitleString(description)
                setColors(color)
            }
            dialog.show()
        }

        @JvmStatic
        fun createAndShowInfoDialog(
            context: Context,
            @StringRes title: Int,
            @StringRes description: Int,
            color: Color = Color.GREEN
        ) {
            createAndShowInfoDialog(
                context,
                context.getString(title),
                context.getString(description),
                color
            )
        }
    }
}