package it.unimi.di.ewlab.iss.common.ui.intro.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import it.unimi.di.ewlab.common.R
import it.unimi.di.ewlab.common.databinding.FragmentIntroAccessibilityServiceBinding
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler
import it.unimi.di.ewlab.iss.common.storage.INTRO_REQUIRED
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager

class IntroAccessibilityServiceFragment : Fragment() {

    private val binding: FragmentIntroAccessibilityServiceBinding by lazy {
        FragmentIntroAccessibilityServiceBinding.inflate(layoutInflater)
    }

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
        }
    }

    private fun openAccessibilitySettings() {
        val settingsIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settingsIntent)
    }

    override fun onResume() {
        super.onResume()

        if (PermissionsHandler.isAccessibilityServiceEnabled(requireContext())) {
            navigateToIntroPermissionsFragment()
        }
    }

    private fun navigateToIntroPermissionsFragment() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_introAccessibilityServiceFragment_to_introPermissionsFragment
            )
    }
}