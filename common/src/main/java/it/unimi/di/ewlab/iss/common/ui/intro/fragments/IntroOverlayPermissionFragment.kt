package it.unimi.di.ewlab.iss.common.ui.intro.fragments

import android.content.DialogInterface
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
import it.unimi.di.ewlab.common.R
import it.unimi.di.ewlab.common.databinding.FragmentIntroOverlayPermissionBinding
import it.unimi.di.ewlab.iss.common.storage.INTRO_REQUIRED
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager

class IntroOverlayPermissionFragment : Fragment() {
    private val binding: FragmentIntroOverlayPermissionBinding by lazy {
        FragmentIntroOverlayPermissionBinding.inflate(layoutInflater)
    }
    private var settingsOpened = false
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Permission required")
            .setMessage("Please grant the permission to continue")
            .setPositiveButton(
                "OK"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .create()
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
        }

        binding.text.text = "Per continuare, concedi a ${requireActivity().intent.getStringExtra("sandboxName")} il permesso di sovrapporsi ad altre app"
    }

    private fun openOverlaySettings() {
        val settingsIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireActivity().intent.getStringExtra("sandboxPackageName"))
        )
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settingsIntent)
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
            navigateToIntroUsageStatsPermission()
        } else if (settingsOpened) {
            alertDialog.show()
            settingsOpened = false
        }
    }

    private fun navigateToIntroUsageStatsPermission() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_introOverlayPermissionFragment_to_introUsageStatsPermissionFragment
            )
    }
}