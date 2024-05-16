package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.facialexpression

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentNeutralFacialExpressionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.FacialExpressionAdapter
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.FacialExpressionViewModel
import it.unimi.di.ewlab.iss.common.model.MainModel

class NeutralFacialExpressionFragment : Fragment() {

    companion object {
        private const val TAG = "NeutralFacialExpressionFragment"
    }

    private lateinit var binding: FragmentNeutralFacialExpressionBinding
    private val viewModel: FacialExpressionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (MainModel.getInstance().neutralFacialExpressionAction != null) {
            Log.e(TAG, "Neutral facial expression already defined")
            throw IllegalStateException("Neutral facial expression already defined")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNeutralFacialExpressionBinding.inflate(layoutInflater)
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
        binding.facialExpressionRecordAgain.root.setOnClickListener {
            navigateBack()
        }

        binding.facialExpressionConfirm.root.setOnClickListener {
            Log.d(TAG, "saveAction")
            viewModel.saveNeutralFacialExpressionAction()
            viewModel.clear()
            showActionDefinedMessage()
            navigateToRecordFacialExpressionFragment()
        }

        binding.facialExpressionFramesList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.facialExpressionFramesList.adapter = FacialExpressionAdapter(requireContext(), viewModel.frames, binding.zoomedFrame)
    }

    private fun showActionDefinedMessage() {
        Toast.makeText(
            requireContext(),
            R.string.feraction_neutral_action_defined,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateBack() {
        viewModel.clear()
        Navigation.findNavController(requireView()).popBackStack()
    }

    private fun navigateToRecordFacialExpressionFragment() {
        Navigation.findNavController(requireView())
            .popBackStack(
                R.id.recordFacialExpressionFragment, false
            )
    }
}