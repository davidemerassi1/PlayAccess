package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.info

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentInfoFacialExpressionActionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.FacialExpressionActivity
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.ActionsListFragment
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame
import it.unimi.di.ewlab.iss.common.utils.ViewAnimator

class InfoFacialExpressionActionFragment : Fragment() {
    companion object {
        const val RESULT_CODE = 394
        private const val TAG = "InfoFacialExpressionActionFragment"
    }

    private lateinit var binding: FragmentInfoFacialExpressionActionBinding

    private var actionId: Int? = null
    private var action: Action? = null
    private var newFrames: List<Frame>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInfoFacialExpressionActionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionId = requireArguments().getInt(ActionsListFragment.ARG_ID_ACTION)
        action = MainModel.getInstance().getActionById(actionId!!)
        if (action == null || action !is FacialExpressionAction) {
            Log.e(TAG, "No facial expression action found with actionId $actionId")
            throw IllegalStateException("No facial expression action found with actionId $actionId")
        }
    }

    override fun onResume() {
        super.onResume()
        setUi()
    }

    private fun setUi() {
        action?.let {
            binding.facialExpressionPreview.setImageBitmap(
                (it as FacialExpressionAction).frames[0].bitmap
            )
            binding.zoomedFramePreview.setImageBitmap(
                it.frames[0].bitmap
            )
            binding.facialExpressionPreview.setOnClickListener {
                ViewAnimator.animateImageViewZoom(binding.facialExpressionPreview, binding.zoomedFramePreview)
            }

            if (it == MainModel.getInstance().neutralFacialExpressionAction) {
                binding.facialExpressionNewName.isClickable = false
                binding.facialExpressionNewName.isFocusable = false
                binding.facialExpressionNewName.isContextClickable = false
                binding.facialExpressionNewName.isCursorVisible = false
                binding.facialExpressionNewName.isSelected = false
            }

            binding.facialExpressionNewName.setText(it.name)

            binding.facialExpressionRecordAgain.root.setOnClickListener {
                startFacialExpressionActivity()
            }

            binding.facialExpressionSave.root.setOnClickListener { _ ->
                val newName = binding.facialExpressionNewName.text.toString()
                if (newName != it.name && !MainModel.getInstance().isValidActionName(newName)) {
                    showNameUnavailableToast(newName)
                } else if (newName.isEmpty()) {
                    showEmptyNameToast()
                } else {
                    if (newName == it.name && newFrames == null) {
                        showNoChangeMessage()
                    } else {
                        updateAction()
                    }
                    closeKeyboard()
                    showActionSavedToast()
                    navigateBack()
                }
            }

        }
    }

    private fun updateAction() {
        (action as FacialExpressionAction).let {
            val newName = binding.facialExpressionNewName.text.toString()
            var changed = false
            if (it.name != newName) {
                it.name = newName
                changed = true
            }
            if (newFrames != null) {
                it.frames = newFrames!!
                changed = true
            }
            if (changed)
                MainModel.getInstance().writeActionsJson()
        }
    }

    private val recordFacialExpressionFragmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "Result received with code: ${result.resultCode}")
        if (result.resultCode != RESULT_CODE)
            return@registerForActivityResult

        newFrames = MainModel.getInstance().tempFacialExpressionActionFrames
        binding.root.post {
            val bitmap = (newFrames ?: (action as FacialExpressionAction).frames)[0].bitmap
            binding.facialExpressionPreview.setImageBitmap(bitmap)
            binding.zoomedFramePreview.setImageBitmap(bitmap)
        }
    }

    private fun startFacialExpressionActivity() {
        val intent = Intent(context, FacialExpressionActivity::class.java)
        intent.putExtra(FacialExpressionActivity.ARG_ID_ACTION, action!!.actionId)
        recordFacialExpressionFragmentLauncher.launch(intent)
    }

    private fun closeKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun navigateBack() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_infoFacialExpressionActionFragment_to_directoriesFragment,
            )
    }

    private fun showNoChangeMessage() {
        Toast.makeText(
            activity,
            R.string.actiondetails_no_change,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showEmptyNameToast() {
        Toast.makeText(
            activity,
            R.string.actionmanager_empty_name,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showNameUnavailableToast(name: String) {
        Toast.makeText(
            activity,
            getString(R.string.actionmanager_unavailable_name, name),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showActionSavedToast() {
        Toast.makeText(
            activity,
            R.string.actionmanager_action_saved,
            Toast.LENGTH_SHORT
        ).show()
    }
}