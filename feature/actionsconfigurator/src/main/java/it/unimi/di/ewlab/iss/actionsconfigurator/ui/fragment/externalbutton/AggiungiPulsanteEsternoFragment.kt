package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.externalbutton

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentAggiungiAzionePulsanteEsternoBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.PulsanteEsternoActivity
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.AggiungiPulsanteEsternoViewModel
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler

class AggiungiPulsanteEsternoFragment : Fragment() {

    private lateinit var binding: FragmentAggiungiAzionePulsanteEsternoBinding
    val viewModel: AggiungiPulsanteEsternoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAggiungiAzionePulsanteEsternoBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Manager dei dispositivi connessi tramite usb
        val manager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager

        if (
            PermissionsHandler.checkAllBluetoothPermissions(requireContext()) && (
                    viewModel.checkBluetooth() ||
                            manager.deviceList.size > 1     // Il primo è sempre il dispositivo stesso
                    )
        )
            navigateToRilevaPulsanteEsternoFragment()

        setUi()
    }

    private fun setUi() {
        if (!PermissionsHandler.checkAllBluetoothPermissions(requireContext()))
            binding.impostazioniBluetooth.text =
                getString(R.string.externalbutton_bluetooth_permission)
        else if (!viewModel.checkBluetooth())
            binding.impostazioniBluetooth.text =
                getString(R.string.externalbutton_bluetooth_settings)

        binding.impostazioniBluetooth.root.setOnClickListener {
            if (!PermissionsHandler.checkAllBluetoothPermissions(requireContext()))
                askBluetoothPermissions()
            else if (!viewModel.checkBluetooth()) {
                openBluetoothSettings()
            }
        }
    }

    private fun askBluetoothPermissions() {
        PermissionsHandler.askBluetoothPermissions(
            requireActivity(),
            PulsanteEsternoActivity.BLUETOOTH_PERMISSION_REQUEST_CODE
        )
    }

    private fun openBluetoothSettings() {
        val bluetoothIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        bluetoothIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(bluetoothIntent)
    }

    private fun navigateToRilevaPulsanteEsternoFragment() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_aggiungiPulsanteEsternoFragment_to_rilevaPulsanteEsternoFragment
            )
    }
}