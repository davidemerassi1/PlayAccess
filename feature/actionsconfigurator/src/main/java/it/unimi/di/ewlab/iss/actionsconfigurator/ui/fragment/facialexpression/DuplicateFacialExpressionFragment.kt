package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.facialexpression

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.ortiz.touchview.TouchImageView
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentDuplicateFacialExpressionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.info.InfoFacialExpressionActionFragment
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.FacialExpressionViewModel
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.ui.utils.ViewAnimator

class DuplicateFacialExpressionFragment : Fragment() {

    companion object {
        const val RERECORDING_KEY = "reRecorded"
        private const val TAG = "DuplicateFacialExpressionFragment"
    }

    private lateinit var binding: FragmentDuplicateFacialExpressionBinding
    private val viewModel: FacialExpressionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDuplicateFacialExpressionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        })

        setUi()
    }

    private fun setUi() {
        if (viewModel.duplicateAction == null) {
            Log.e(TAG, "No duplicate facial expression action set")
            throw IllegalStateException("No duplicate facial expression action set")
        }

        binding.duplicateExpression.setImageBitmap(viewModel.duplicateAction!!.frames[0].bitmap)
        binding.acquiredExpression.setImageBitmap(viewModel.frames[0].bitmap)

        binding.duplicateExpression.setOnClickListener {
            binding.zoomedFrame.setImageBitmap(viewModel.duplicateAction!!.frames[0].bitmap)
            ViewAnimator.animateImageViewZoom(binding.duplicateExpression, binding.zoomedFrame)
        }
        binding.acquiredExpression.setOnClickListener {
            binding.zoomedFrame.setImageBitmap(viewModel.frames[0].bitmap)
            ViewAnimator.animateImageViewZoom(binding.acquiredExpression, binding.zoomedFrame)
        }

        binding.retryButton.root.setOnClickListener {
            navigateBack()
        }

        binding.confirmButton.root.setOnClickListener {
            if (arguments?.getBoolean(RERECORDING_KEY) == true) {
                returnFramesToInfoFacialExpressionFragment()
            } else {
                navigateToDefineFacialExpression()
            }
        }
    }

    private fun returnFramesToInfoFacialExpressionFragment() {
        MainModel.getInstance().tempFacialExpressionActionFrames = viewModel.frames
        requireActivity().setResult(InfoFacialExpressionActionFragment.RESULT_CODE)
        requireActivity().finishAndRemoveTask()
    }

    private fun navigateToDefineFacialExpression() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_duplicateFacialExpressionFragment_to_defineFacialExpressionFragment
            )
    }

    private fun navigateBack() {
        viewModel.clear()
        Navigation.findNavController(requireView()).popBackStack()
    }
}