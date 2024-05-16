package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.info

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentInfoButtonActionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.ActionsListFragment
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.InfoButtonActionViewModel
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction

class InfoButtonActionFragment : Fragment() {

    companion object {
        private const val TAG = "InfoButtonActionFragment"
    }

    private lateinit var binding: FragmentInfoButtonActionBinding

    val viewModel: InfoButtonActionViewModel by viewModels()
    private var actionId: Int? = null
    private var action: Action? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInfoButtonActionBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionId = requireArguments().getInt(ActionsListFragment.ARG_ID_ACTION)
        action = MainModel.getInstance().getActionById(actionId!!)
        if (action == null || action !is ButtonAction) {
            Log.e(TAG, "No button action found with actionId $actionId")
            throw IllegalStateException("No button action found with actionId $actionId")
        }
        setUpListeners()
    }

    private fun setUpListeners() {
        action?.let {
            binding.editName.setText(it.name)

            binding.editName.setOnEditorActionListener { _, _, _ ->
                saveAction()
                return@setOnEditorActionListener true
            }

            binding.modificaAzione.root.setOnClickListener { _ ->
                saveAction()
            }
        }
    }

    private fun saveAction() {
        val newName = binding.editName.text.toString()
        if (newName != action!!.name && !MainModel.getInstance().isValidActionName(newName)) {
            showNameUnavailableToast(newName)
        } else if (newName.isEmpty()) {
            showEmptyNameToast()
        } else {
            if (newName == action!!.name) {
                showNoChangeMessage()
            } else {
                action!!.name = newName
                MainModel.getInstance().writeActionsJson()
            }
            closeKeyboard()
            showActionSavedToast()
            navigateBack()
        }
    }

    private fun closeKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun navigateBack() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_infoButtonActionFragment_to_directoriesFragment
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
            getString(R.string.actionmanager_empty_name),
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