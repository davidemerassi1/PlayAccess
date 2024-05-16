package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.externalbutton

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentConfigurazionePulsanteEsternoBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.MainActivityConfAzioni
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.ConfigurazionePulsanteEsternoViewModel
import it.unimi.di.ewlab.iss.common.model.MainModel

class ConfigurazionePulsanteEsternoFragment : Fragment() {

    companion object {
        private const val TAG = "ConfigurazionePulsanteEsternoFragment"
    }

    private lateinit var binding: FragmentConfigurazionePulsanteEsternoBinding

    val viewModel: ConfigurazionePulsanteEsternoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentConfigurazionePulsanteEsternoBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })
    }

    private fun navigateBack() {
        MainModel.getInstance().setTempButtonAction(null)
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_configurazionePulsanteEsternoFragment_to_aggiungiPulsanteEsternoFragment
            )
    }

    private fun setListeners() {
        binding.salvaConfigurazione.root.setOnClickListener {
            saveAction()
        }

        binding.nomeAzioneBottone.setOnEditorActionListener { _, _, _ ->
            saveAction()
            return@setOnEditorActionListener true
        }
    }

    private fun saveAction() {
        val newName = binding.nomeAzioneBottone.text.toString()

        if (newName.isEmpty()) {
            showEmptyNameToast()
        } else if (!MainModel.getInstance().isValidActionName(newName)) {
            showNameUnavailableToast(newName)
        } else {
            val action = MainModel.getInstance().tempButtonAction.value
            action!!.name = newName
            MainModel.getInstance().addAction(action)
            MainModel.getInstance().writeActionsJson()

            showActionDefinedMessage()
            startActivity(Intent(context, MainActivityConfAzioni::class.java))
        }
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