package it.unimi.di.ewlab.iss.common.configuratorsmodels

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import it.unimi.di.ewlab.common.R
import it.unimi.di.ewlab.common.databinding.RecyclerviewDialogBinding
import android.view.WindowManager

open class RecyclerViewDialog : Dialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener)

    private var binding: RecyclerviewDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.recyclerview_dialog, null, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(binding.root)
        val window: Window? = this.window
        if (null != window) {

            //Flag per triggherare eventi on touch al di fuori dello schermo
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                              WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)

            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            //Set width to 90%
            val displayMetrics = context.resources.displayMetrics
            val displayHeigth = displayMetrics.heightPixels
            val displayWidth = displayMetrics.widthPixels
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            layoutParams.height = (displayHeigth * 0.90f).toInt()
            layoutParams.width = (displayWidth * 0.90f).toInt()
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            window.attributes = layoutParams
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //Se premo al di fuori dello schermo chiudo il dialog
        if(event.action == MotionEvent.ACTION_OUTSIDE){
            this.dismiss()
        }
        return false
    }

    var icon: Int = 0
        set(value) {
            binding.icon = value
            field = value
        }

    var titleText: Int? = null
        set(value) {
            binding.title = value?.let { context.getText(it) }
            field = value
        }

    var titleTextWithValue: String? = null
        set(value) {
            binding.title = value
            field = value
        }

    var subTitleText: Int? = null
        set(value) {
            binding.text = value?.let { context.getText(it) }
            field = value
        }

    var subTitleTextWithValue: String? = null
        set(value) {
            binding.text = value
            field = value
        }

    var exitButton: Boolean? = false
        set(value) {
            binding.isExitEnable = value
            field = value
        }

    var adapter: RecyclerView.Adapter<*>? = null
        set(value) {
            if(value != null) {
                binding.emptyState = false
                binding.actionEventList.adapter = value
                field = value
            }else
                binding.emptyState = true
        }

    val emptyState: Boolean
        get() = binding.emptyState ?: false

    var listener: RecyclerViewListener? = null
        set(value) {
            binding.listener = value
            field = value
        }

    interface RecyclerViewListener {
        fun onLinkClicked()
        fun onPrimaryButtonClicked()
        fun onSecondaryButtonClicked()
    }

    fun setTitleString(value: String) {
        binding.title = value
    }

    fun setSubTitleString(value: String) {
        binding.text = value
    }

    fun setAdapterForRecyclerView(value: RecyclerView.Adapter<*>?) {
        if(value != null) {
            binding.actionEventList.adapter = value
            binding.emptyState = false
        }else
            binding.emptyState = true
    }

    companion object {
        @JvmStatic
        fun createAndShowInfoDialog(
            context: Context,
            title: String,
            description: String,
            adapter: RecyclerView.Adapter<*>?
        ) {
            var dialog = RecyclerViewDialog(context).apply {
                setTitleString(title)
                setSubTitleString(description)
                setAdapterForRecyclerView(adapter)
            }
            dialog.listener = object : RecyclerViewListener {
                override fun onLinkClicked() {
                    dialog.dismiss()
                }

                override fun onPrimaryButtonClicked() {
                    dialog.dismiss()
                }

                override fun onSecondaryButtonClicked() {
                    dialog.dismiss()
                }

            }

            dialog.show()
        }
    }
}