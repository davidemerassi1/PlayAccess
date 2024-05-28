package com.example.sandboxtest.ui.intro.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.sandboxtest.MyApplication
import com.example.sandboxtest.R
import com.example.sandboxtest.databinding.AlertDialogPermissionsNeededBinding
import com.example.sandboxtest.databinding.FragmentIntroOverlayPermissionBinding
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.storage.INTRO_REQUIRED
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager

class IntroOverlayPermissionFragment : Fragment() {
    private val binding: FragmentIntroOverlayPermissionBinding by lazy {
        FragmentIntroOverlayPermissionBinding.inflate(layoutInflater)
    }
    private var settingsOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private fun setUi() {
        binding.optionsButton.setOnClickListener {
            openOverlaySettings()
            settingsOpened = true
        }

        binding.text.text = "Per continuare, concedi a ${MainModel.getInstance().sandboxName} il permesso di sovrapporsi ad altre app"
    }

    private fun openOverlaySettings() {
        val settingsIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireActivity().packageName)
        )
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //startActivity(settingsIntent)
    }

    private fun checkOverlayPermission(): Boolean {
        //TODO: da verificare
        return Settings.canDrawOverlays(context) /* {
            button.setOnClickListener(View.OnClickListener { v: View? ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                )
                startActivityForResult(intent, PermissionCheckerActivity.overlayRequestCode)
            })
        } else {
            checkUsageStatsPermission()
        }*/
    }

    override fun onResume() {
        super.onResume()

        if (checkOverlayPermission()) {
            //navigateToIntroUsageStatsPermission()
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

        dialogBinding.msg.text = "Questa app non può funzionare senza il permesso richiesto. Assicurati di aver concesso il permesso a ${MainModel.getInstance().sandboxName} e non a PlayAccess"

        dialogBinding.okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}