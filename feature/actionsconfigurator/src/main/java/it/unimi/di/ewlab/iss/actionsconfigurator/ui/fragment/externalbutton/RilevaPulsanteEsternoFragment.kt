package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.externalbutton

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentRilevaPulsanteEsternoBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.RilevaPulsanteEsternoViewModel
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.network.AccessibilityServiceAction
import it.unimi.di.ewlab.iss.common.network.AccessibilityServiceKeys

class RilevaPulsanteEsternoFragment : Fragment() {

    companion object {
        private const val TAG = "RilevaPulsanteEsternoFragment"
    }

    private lateinit var binding: FragmentRilevaPulsanteEsternoBinding

    val viewModel: RilevaPulsanteEsternoViewModel by viewModels()

    private var scanning = false;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRilevaPulsanteEsternoBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startScanning()
        setObservers()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })
    }

    private fun navigateBack() {
        stopScanning()
        requireActivity().finish()
    }

    private fun setObservers() {
        MainModel.getInstance().tempButtonAction.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            if (MainModel.getInstance().hasButtonAction(it)) {
                onButtonAlreadyRegistered()
            } else {
                onButtonAction()
            }
        }
    }

    private fun onButtonAlreadyRegistered() {
        Log.d(TAG, "Button already registered")

        Toast.makeText(
            requireContext(),
            R.string.externalbutton_already_registered_button,
            Toast.LENGTH_SHORT
        ).show()

        startScanning()
    }

    private fun onButtonAction() {
        Log.d(TAG, "Button found")

        stopScanning()

        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_rilevaPulsanteEsternoFragment_to_configurazionePulsanteEsternoFragment
            )
    }

    private fun startScanning() {
        /*val startScanningIntent = Intent(AccessibilityServiceAction)
        startScanningIntent.putExtra(AccessibilityServiceKeys.RecordExternalButtonAction.name, "")
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(startScanningIntent)*/
        scanning = true
    }

    private fun stopScanning() {
        /*val stopScanningIntent = Intent(AccessibilityServiceAction)
        stopScanningIntent.putExtra(AccessibilityServiceKeys.WaitForGame.name, "")
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(stopScanningIntent)*/
        scanning = false

    }


}