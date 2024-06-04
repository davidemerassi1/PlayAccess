package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentDirectoriesBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.InfoFragment
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.DirectoriesViewModel
import it.unimi.di.ewlab.iss.common.configuratorsmodels.BaseDialog
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action

class DirectoriesFragment : Fragment(), InfoFragment {

    companion object {
        const val TAG = "DirectoriesFragment"
        const val ACTION_TYPE_KEY = "DirectoriesFragment_ACTION_TYPE_KEY"
    }

    private lateinit var binding: FragmentDirectoriesBinding
    val viewModel: DirectoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDirectoriesBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        setUi()
        setListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            })
    }

    private fun setUi() {
        if (MainModel.getInstance().facialExpressionActions.isEmpty())
            binding.facialExpressionsDirectory.root.visibility = View.GONE
        else binding.facialExpressionsDirectory.root.visibility = View.VISIBLE

        if (MainModel.getInstance().buttonActions.isEmpty())
            binding.externalButtonsDirectory.root.visibility = View.GONE
        else binding.externalButtonsDirectory.root.visibility = View.VISIBLE
    }

    private fun setListeners(){
        binding.addAction.setOnClickListener{
            Navigation.findNavController(requireView())
                .navigate(
                    R.id.action_listaAzioniFragment_to_aggiungiAzioneFragment
                )
        }

        binding.externalButtonsDirectory.root.setOnClickListener{
            navigateToActionsListFragment(Action.ActionType.BUTTON)
        }

        binding.facialExpressionsDirectory.root.setOnClickListener{
            navigateToActionsListFragment(Action.ActionType.FACIAL_EXPRESSION)
        }
    }

    private fun navigateToActionsListFragment(actionType: Action.ActionType) {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_directoriesFragment_to_actionsListFragment,
                bundleOf(ACTION_TYPE_KEY to actionType.name)
            )
    }

    override fun showInfo() {
        Log.d(TAG, "showInfo")

        BaseDialog.createAndShowInfoDialog(
            requireContext(),
            R.string.actionmanager_directories_info_title,
            R.string.actionmanager_directories_info_msg,
            BaseDialog.Color.GREEN
        )
    }
}