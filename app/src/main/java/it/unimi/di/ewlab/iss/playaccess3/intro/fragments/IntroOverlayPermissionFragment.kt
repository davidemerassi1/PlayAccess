package it.unimi.di.ewlab.iss.playaccess3.intro.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.playaccess3.R
import it.unimi.di.ewlab.iss.playaccess3.databinding.AlertDialogPermissionsNeededBinding
import it.unimi.di.ewlab.iss.playaccess3.databinding.FragmentIntroOverlayPermissionBinding
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler

class IntroOverlayPermissionFragment : Fragment() {
    private val binding: FragmentIntroOverlayPermissionBinding by lazy {
        FragmentIntroOverlayPermissionBinding.inflate(layoutInflater)
    }
    private var settingsOpened = false

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
    }

    private fun setUi() {
        binding.optionsButton.setOnClickListener {
            openOverlaySettings()
            settingsOpened = true
        }
    }

    private fun openOverlaySettings() {
        val settingsIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireActivity().packageName)
        )
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settingsIntent)
    }



    override fun onResume() {
        super.onResume()
        if (PermissionsHandler.checkOverlayPermission(requireContext())) {
            navigateToNext()
        } else if (settingsOpened) {
            openDenyDialog()
            settingsOpened = false
        }
    }

    private fun openDenyDialog() {
        val dialogBinding = AlertDialogPermissionsNeededBinding.inflate(layoutInflater)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.msg.text = getString(R.string.alert_permission_required)

        dialogBinding.okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigateToNext() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.introPermissionsFragment
            )
    }
}