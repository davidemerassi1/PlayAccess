package it.unimi.di.ewlab.iss.playaccess3.intro.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.playaccess3.R
import it.unimi.di.ewlab.iss.playaccess3.databinding.AlertDialogPermissionsNeededBinding
import it.unimi.di.ewlab.iss.playaccess3.databinding.FragmentIntroPermissionsBinding
import it.unimi.di.ewlab.iss.playaccess3.intro.IntroViewModel
import it.unimi.di.ewlab.iss.playaccess3.intro.PlayAccessIntroActivity
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
                navigateToNext()
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

    private fun navigateToNext() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.introAccessibilityServiceFragment
            )
    }

    private fun openDenyDialog() {
        val dialogBinding = AlertDialogPermissionsNeededBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.msg.text = getString(R.string.alert_permission_required)

        dialogBinding.okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}