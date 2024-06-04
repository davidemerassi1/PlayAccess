package com.example.sandboxtest.ui.intro.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.sandboxtest.R
import com.example.sandboxtest.databinding.AlertDialogPermissionsNeededBinding
import com.example.sandboxtest.databinding.FragmentIntroPermissionsBinding
import it.unimi.di.ewlab.iss.common.storage.INTRO_REQUIRED
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager
import com.example.sandboxtest.ui.intro.IntroViewModel
import com.example.sandboxtest.ui.intro.PlayAccessIntroActivity
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.MainActivityConfAzioni
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler

class IntroPermissionsFragment : Fragment() {
    private var init = false;

    private val binding: FragmentIntroPermissionsBinding by lazy {
        FragmentIntroPermissionsBinding.inflate(layoutInflater)
    }

    private val viewModel: IntroViewModel by activityViewModels()

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

    private fun setObservers() {
        viewModel.permissions.observe(requireActivity()) {
            if (it) {
                //broadcastNotifications()
                openConfiguration()
            } else if (init)
                openDenyDialog()
            else
                init = true
        }
    }

    private fun setUi() {
        binding.grantButton.setOnClickListener {
            askAllPermissions()
        }
    }

    private fun askAllPermissions() {
        PermissionsHandler.askAllPermissions(
            requireActivity(),
            PlayAccessIntroActivity.PERMISSIONS_REQUEST_CODE
        )
    }

    private fun openConfiguration() {
        val intent = Intent(requireContext(), MainActivityConfAzioni::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun openDenyDialog() {
        val dialogBinding = AlertDialogPermissionsNeededBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.msg.text = "Questa app non può funzionare senza i permessi richiesti"

        dialogBinding.okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}