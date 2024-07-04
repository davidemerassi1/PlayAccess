package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.facialexpression

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentDefineFacialExpressionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.MainActivityConfAzioni
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.FacialExpressionAdapter
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.FacialExpressionViewModel
import it.unimi.di.ewlab.iss.common.model.MainModel

class DefineFacialExpressionFragment : Fragment() {

    companion object {
        private const val TAG = "DefineFacialExpressionFragment"
    }

    private lateinit var binding: FragmentDefineFacialExpressionBinding
    private val viewModel: FacialExpressionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefineFacialExpressionBinding.inflate(layoutInflater)
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
        binding.facialExpressionName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setName(p0.toString())
            }

        })

        binding.facialExpressionName.setOnEditorActionListener { _, _, _ ->
            saveAction()
            return@setOnEditorActionListener true
        }

        binding.facialExpressionRecordAgain.root.setOnClickListener {
            navigateBack()
        }

        binding.facialExpressionConfirm.root.setOnClickListener {
            saveAction()
        }

        binding.facialExpressionFramesList.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.facialExpressionFramesList.adapter = FacialExpressionAdapter(requireContext(), viewModel.frames, binding.zoomedFrame)
    }

    private fun saveAction() {
        Log.d(TAG, "saveAction")

        if (viewModel.name.isEmpty()) {
            showEmptyNameToast()
        } else if (!MainModel.getInstance().isValidActionName(viewModel.name)) {
            showNameUnavailableToast(viewModel.name)
        } else {
            Thread {
                viewModel.saveAction()
            }.start()
            closeKeyboard()
            //showActionDefinedMessage()
            startActivity(Intent(context, MainActivityConfAzioni::class.java))
        }
    }

    private fun closeKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun navigateBack() {
        viewModel.clear()
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_defineFacialExpressionFragment_to_recordFacialExpressionFragment
            )
    }

    private fun showActionDefinedMessage() {
        Toast.makeText(
            requireContext(),
            R.string.actionmanager_action_defined,
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
}