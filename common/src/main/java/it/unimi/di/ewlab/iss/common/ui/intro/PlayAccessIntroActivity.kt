package it.unimi.di.ewlab.iss.common.ui.intro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import it.unimi.di.ewlab.common.R
import it.unimi.di.ewlab.common.databinding.ActivityPlayAccessIntroBinding
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler
import it.unimi.di.ewlab.iss.common.storage.INTRO_REQUIRED
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager

class PlayAccessIntroActivity : AppCompatActivity() {

    companion object {
        const val DESTINATION_KEY = "PlayAccessIntroActivity_DESTINATION_KEY"
        const val GAME_KEY = "PlayAccessIntroActivity_GAME_KEY"
        const val CONFIGURATION_KEY = "PlayAccessIntroActivity_CONFIGURATION_KEY"
        const val PERMISSIONS_REQUEST_CODE = 30
    }

    private val binding: ActivityPlayAccessIntroBinding by lazy {
        ActivityPlayAccessIntroBinding.inflate(layoutInflater)
    }

    private val viewModel: IntroViewModel by viewModels()

    private lateinit var navController: NavController
    private var skipAction = R.id.action_introWelcomeFragment_to_introOverlayPermissionFragment
    private var nextAction = R.id.action_introWelcomeFragment_to_introActionConfiguratorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        viewModel.setPermissions(PermissionsHandler.checkAllPermissions(this))

        setNavController()
        setUi()

        val persistenceManager = PersistenceManager(this)
        if (!(persistenceManager.getValue(INTRO_REQUIRED, true) as Boolean)) {
            navController.navigate(R.id.introOverlayPermissionFragment)
        }
    }

    private fun setUi() {
        binding.skipButton.setOnClickListener {
            if (skipAction > 0)
                navController.navigate(R.id.introOverlayPermissionFragment)
        }

        binding.nextButton.setOnClickListener {
            if (nextAction > 0)
                navController.navigate(nextAction)
        }
    }

    private fun setNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(
            R.navigation.navigation_intro
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            var nextButtonVisibility = View.VISIBLE
            var skipButtonVisibility = View.VISIBLE

            when (destination.id) {
                R.id.introWelcomeFragment -> {
                    nextAction = R.id.action_introWelcomeFragment_to_introActionConfiguratorFragment
                    skipAction =
                        R.id.action_introWelcomeFragment_to_introOverlayPermissionFragment
                }
                R.id.introActionConfiguratorFragment -> {
                    nextAction =
                        R.id.action_introActionConfiguratorFragment_to_introGamesConfiguratorFragment
                    skipAction =
                        R.id.action_introActionConfiguratorFragment_to_introOverlayPermissionFragment
                }
                R.id.introGamesConfiguratorFragment -> {
                    nextAction =
                        R.id.action_introGamesConfiguratorFragment_to_introOverlayPermissionFragment
                    skipAction =
                        R.id.action_introGamesConfiguratorFragment_to_introOverlayPermissionFragment
                }
                R.id.introOverlayPermissionFragment,
                R.id.introPermissionsFragment,
                -> {
                    nextAction = -1
                    skipAction = -1
                    nextButtonVisibility = View.INVISIBLE
                    skipButtonVisibility = View.INVISIBLE
                }
            }

            binding.nextButton.visibility = nextButtonVisibility
            binding.skipButton.visibility = skipButtonVisibility
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            viewModel.setPermissions(grantResults.indices.all {
                grantResults[it] == PackageManager.PERMISSION_GRANTED ||
                        permissions[it] == Manifest.permission.POST_NOTIFICATIONS
            })
        } else {
            viewModel.setPermissions(grantResults.indices.all {
                grantResults[it] == PackageManager.PERMISSION_GRANTED
            })
        }
    }
}