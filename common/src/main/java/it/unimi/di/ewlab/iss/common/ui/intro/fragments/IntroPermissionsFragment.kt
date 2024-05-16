package it.unimi.di.ewlab.iss.common.ui.intro.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import it.unimi.di.ewlab.common.R
import it.unimi.di.ewlab.common.databinding.AlertDialogPermissionsNeededBinding
import it.unimi.di.ewlab.common.databinding.FragmentIntroPermissionsBinding
import it.unimi.di.ewlab.iss.common.model.Configuration
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.network.AccessibilityServiceAction
import it.unimi.di.ewlab.iss.common.network.AccessibilityServiceKeys
import it.unimi.di.ewlab.iss.common.storage.ModuleDestination
import it.unimi.di.ewlab.iss.common.ui.intro.IntroViewModel
import it.unimi.di.ewlab.iss.common.ui.intro.PlayAccessIntroActivity
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler

class IntroPermissionsFragment : Fragment() {

    private val binding: FragmentIntroPermissionsBinding by lazy {
        FragmentIntroPermissionsBinding.inflate(layoutInflater)
    }

    private val viewModel: IntroViewModel by activityViewModels()

    private val destination: String?
        get() = requireActivity().intent.extras?.getString(
            PlayAccessIntroActivity.DESTINATION_KEY,
            ""
        )
    private var conf: Configuration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameBundleId =
            requireActivity().intent.extras?.getString(PlayAccessIntroActivity.GAME_KEY, "")
        val game = MainModel.getInstance().getGameByBundleId(gameBundleId)
        val configurationName = requireActivity().intent.extras?.getString(
            PlayAccessIntroActivity.CONFIGURATION_KEY,
            ""
        )
        conf = game?.getConfiguration(configurationName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })

        setUi()
        setObservers()
    }

    override fun onResume() {
        super.onResume()

        if (
            (conf == null && PermissionsHandler.checkAllPermissions(requireContext())) ||
            (conf != null && PermissionsHandler.checkConfigPermissions(conf!!, requireContext()))
        )
            navigateToDestination()
    }

    private fun setObservers() {
        viewModel.permissions.observe(requireActivity()) {
            if (it) {
                broadcastNotifications()
                navigateToDestination()
            }
        }
    }

    private fun setUi() {
        if (destination == ModuleDestination.ACCESSIBILITYSERVICE.name) {
            conf?.let {
                if (it.facialExpressionActions.isEmpty())
                    binding.cameraPermission.visibility = View.GONE
                if (it.buttonActions.isEmpty())
                    binding.bluetoothPermission.visibility = View.GONE
                binding.grantButton.setOnClickListener { _ ->
                    askConfigurationPermissions(it)
                }
            }
        } else {
            binding.grantButton.setOnClickListener {
                askAllPermissions()
            }
        }

        binding.denyButton.setOnClickListener {
            openDenyDialog()
        }
    }

    private fun askConfigurationPermissions(configuration: Configuration) {
        PermissionsHandler.askConfigurationPermissions(
            configuration,
            requireActivity(),
            PlayAccessIntroActivity.PERMISSIONS_REQUEST_CODE
        )
    }

    private fun askAllPermissions() {
        PermissionsHandler.askAllPermissions(
            requireActivity(),
            PlayAccessIntroActivity.PERMISSIONS_REQUEST_CODE
        )
    }

    private fun navigateToDestination() {
        when (destination) {
            ModuleDestination.ACTIONSCONFIGURATOR.name -> navigateToActionConfigurator()
            ModuleDestination.GAMESCONFIGURATOR.name -> navigateToGamesConfigurator()
            ModuleDestination.ACCESSIBILITYSERVICE.name -> navigateToGame()
            else -> navigateToActionConfigurator()
        }
    }

    private fun navigateToGame() {
        val configurationSelected = Intent(AccessibilityServiceAction)
        configurationSelected.putExtra(AccessibilityServiceKeys.StartRecognizers.name, "")
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(configurationSelected)
        requireActivity().finishAndRemoveTask()
    }

    private fun navigateToActionConfigurator() {
        try {
            val intent = Intent(
                context,
                Class.forName("it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.MainActivityConfAzioni")
            )
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun navigateToGamesConfigurator() {
        try {
            val intent = Intent(
                context,
                Class.forName("it.unimi.di.ewlab.iss.gamesconfigurator.ui.LoginSSO")
            )
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun broadcastNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&    // Nelle versioni precedenti non è necessario il permesso e le notifiche si attivano all'avvio dell'accessibility service
            PermissionsHandler.checkPostNotifications(requireContext())
        ) {
            val intent = Intent(AccessibilityServiceAction)
            intent.putExtra(AccessibilityServiceKeys.EnableNotifications.name, "")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }
    }

    private fun openDenyDialog() {
        val dialogBinding = AlertDialogPermissionsNeededBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.okButton.setOnClickListener {
            dialog.dismiss()
            showPermissionsRequiredToast()
            requireActivity().finishAndRemoveTask()
        }
        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun showPermissionsRequiredToast() {
        Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_LONG).show()
    }
}