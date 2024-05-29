package com.example.sandboxtest.ui.intro.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.sandboxtest.R
import com.example.sandboxtest.SandboxVerifier
import com.example.sandboxtest.databinding.AlertDialogPermissionsNeededBinding
import com.example.sandboxtest.databinding.FragmentIntroAccessibilityServiceBinding
import it.unimi.di.ewlab.iss.common.storage.INTRO_REQUIRED
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler

class IntroAccessibilityServiceFragment : Fragment() {
    private val binding: FragmentIntroAccessibilityServiceBinding by lazy {
        FragmentIntroAccessibilityServiceBinding.inflate(layoutInflater)
    }
    private var settingsOpened = false
    private lateinit var persistenceManager: PersistenceManager

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
    }

    private fun setUi() {
        binding.optionsButton.setOnClickListener {
            openAccessibilitySettings()
            settingsOpened = true
        }

        binding.text.text = "Per funzionare c'è bisogno del permesso di accessibilità"
    }

    private fun openAccessibilitySettings() {
        val settingsIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settingsIntent)
    }

    private fun checkAccessibilityServiceEnabled(): Boolean {
        return PermissionsHandler.isAccessibilityServiceEnabled(requireContext())
    }

    override fun onResume() {
        super.onResume()

        if (checkAccessibilityServiceEnabled()) {
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

        dialogBinding.msg.text = "Questa app non può funzionare senza il permesso richiesto"

        dialogBinding.okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigateToNext() {
        if (SandboxVerifier.getSandboxPackageName(requireContext()) == null)
            Navigation.findNavController(requireView())
                .navigate(
                    R.id.introPermissionsFragment
                )
        else
            Navigation.findNavController(requireView())
                .navigate(
                    R.id.introOverlayPermissionFragment
                )
    }
}