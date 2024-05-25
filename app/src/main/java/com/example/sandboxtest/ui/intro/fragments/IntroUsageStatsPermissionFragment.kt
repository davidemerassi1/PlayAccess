package com.example.sandboxtest.ui.intro.fragments

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.sandboxtest.databinding.AlertDialogPermissionsNeededBinding
import com.example.sandboxtest.databinding.FragmentIntroUsageStatsPermissionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.MainActivityConfAzioni
import it.unimi.di.ewlab.iss.common.model.MainModel

class IntroUsageStatsPermissionFragment : Fragment() {
    private val binding: FragmentIntroUsageStatsPermissionBinding by lazy {
        FragmentIntroUsageStatsPermissionBinding.inflate(layoutInflater)
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
            openUsageStatsSettings()
            settingsOpened = true
        }

        binding.text.text = "Ci siamo quasi! Per ultimo concedi a ${MainModel.getInstance().sandboxName} il permesso di conoscere quale processo è in esecuzione"
    }

    private fun openUsageStatsSettings() {
        val settingsIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settingsIntent.setData(Uri.fromParts("package", MainModel.getInstance().sandboxPackageName, null));
        }
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settingsIntent)
    }

    private fun checkUsageStatsPermission(): Boolean {
        val usageStatsManager =
            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            currentTime - 1000 * 60,
            currentTime
        )

        return usageStatsList != null && !usageStatsList.isEmpty()
    }

    override fun onResume() {
        super.onResume()

        if (checkUsageStatsPermission()) {
            openActionConfigurator()
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

    private fun openActionConfigurator() {
        val intent = Intent(requireContext(), MainActivityConfAzioni::class.java)
        startActivity(intent)
    }
}