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
import androidx.navigation.Navigation
import it.unimi.di.ewlab.common.R
import it.unimi.di.ewlab.common.databinding.AlertDialogPermissionsNeededBinding
import it.unimi.di.ewlab.common.databinding.FragmentIntroPermissionsBinding
import it.unimi.di.ewlab.iss.common.model.Configuration
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.storage.INTRO_REQUIRED
import it.unimi.di.ewlab.iss.common.storage.ModuleDestination
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager
import it.unimi.di.ewlab.iss.common.ui.intro.IntroViewModel
import it.unimi.di.ewlab.iss.common.ui.intro.PlayAccessIntroActivity
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler

class IntroPermissionsFragment : Fragment() {
    private lateinit var persistenceManager: PersistenceManager

    private val binding: FragmentIntroPermissionsBinding by lazy {
        FragmentIntroPermissionsBinding.inflate(layoutInflater)
    }

    private val viewModel: IntroViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        persistenceManager = PersistenceManager(requireContext())
        persistenceManager.setValue(INTRO_REQUIRED, false)
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

        if (PermissionsHandler.checkAllPermissions(requireContext()))
            navigateToDestination()
    }

    private fun setObservers() {
        viewModel.permissions.observe(requireActivity()) {
            if (it) {
                //broadcastNotifications()
                navigateToDestination()
            }
        }
    }

    private fun setUi() {
        binding.grantButton.setOnClickListener {
            askAllPermissions()
        }

        binding.denyButton.setOnClickListener {
            openDenyDialog()
        }
    }

    private fun askAllPermissions() {
        PermissionsHandler.askAllPermissions(
            requireActivity(),
            PlayAccessIntroActivity.PERMISSIONS_REQUEST_CODE
        )
    }

    private fun navigateToDestination() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_introPermissionsFragment_to_introOverlayPermissionFragment
            )
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