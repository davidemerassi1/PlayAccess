package it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity

import actionsConfigurator.OverlayManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.ActionConfiguratorActivityMainBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.DirectoriesFragment
import it.unimi.di.ewlab.iss.common.model.actions.Action.ActionType


class MainActivityConfAzioni : AppCompatActivity() {
    private val binding: ActionConfiguratorActivityMainBinding by lazy {
        ActionConfiguratorActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setNavController()
        OverlayManager.getInstance(this)
    }

    private fun setNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragmentMain.id) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(
            R.navigation.main_nav_graph
        )

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //Set toolbar title e visibility back button in base al fragment
        navController.addOnDestinationChangedListener { _, destination, arguments ->

            var titleRes: Int? = null
            var layoutColor: Int? = null
            var backBtnVisibility: Int? = null
            var iconInfoVisibility: Int? = null
            var barVisibility: Int? = null
            when (destination.id) {

                // Fragment Directories
                R.id.directoriesFragment -> {
                    titleRes = R.string.actionmanager_actions_configurator
                    backBtnVisibility = View.GONE
                    layoutColor = R.color.colorPrimary
                    iconInfoVisibility = View.VISIBLE
                    barVisibility = View.VISIBLE
                }

                // Fragment Aggiungi Azione
                R.id.aggiungiAzioneFragment -> {
                    titleRes = R.string.actionmanager_add_action_title
                    backBtnVisibility = View.GONE
                    layoutColor = R.color.colorPrimary
                    iconInfoVisibility = View.GONE
                    barVisibility = View.VISIBLE
                }

                // Fragment Actions List Schermo
                R.id.actionsListFragment -> {
                    val actionType = ActionType.valueOf(
                        arguments!!.getString(DirectoriesFragment.ACTION_TYPE_KEY)!!
                    )
                    titleRes = getTitle(actionType)
                    backBtnVisibility = View.VISIBLE
                    layoutColor = getLayoutColor(actionType)
                    iconInfoVisibility = View.GONE
                    barVisibility = View.VISIBLE
                }

                // Fragment Info Gesture Schermo
                R.id.infoScreenGestureFragment -> {
                    titleRes = R.string.actiondetails_screen_gesture_action_info_title
                    backBtnVisibility = View.VISIBLE
                    layoutColor = R.color.orange
                    iconInfoVisibility = View.GONE
                    barVisibility = View.VISIBLE
                }

                // Fragment Info Button Action
                R.id.infoButtonActionFragment -> {
                    titleRes = R.string.actiondetails_button_action_info_title
                    backBtnVisibility = View.VISIBLE
                    layoutColor = R.color.blue
                    iconInfoVisibility = View.GONE
                    barVisibility = View.VISIBLE
                }

                // Fragment Info Facial Expression Action
                R.id.infoFacialExpressionActionFragment -> {
                    titleRes = R.string.actiondetails_facial_expression_action_info_title
                    backBtnVisibility = View.VISIBLE
                    layoutColor = R.color.red
                    iconInfoVisibility = View.GONE
                    barVisibility = View.VISIBLE
                }
            }

            titleRes?.let { binding.toolbarTitle.text = getString(it) }
            binding.backBtn.visibility = backBtnVisibility!!
            binding.titleBar.background = ContextCompat.getDrawable(this, layoutColor!!)
            binding.iconInfo.visibility = iconInfoVisibility!!
            binding.titleBarDivider.visibility = barVisibility!!
            binding.titleBar.visibility = barVisibility
            window.statusBarColor = ContextCompat.getColor(this, layoutColor)

            if (iconInfoVisibility == View.VISIBLE) {
                binding.iconInfo.setOnClickListener {
                    val fragment = navHostFragment.childFragmentManager.fragments[0]
                    if (fragment is InfoFragment)
                        fragment.showInfo()
                }
            } else binding.iconInfo.setOnClickListener(null)
        }
    }

    private fun getTitle(actionType: ActionType): Int {
        return when (actionType) {
            ActionType.SCREEN_GESTURE -> R.string.actiondetails_screen_gesture_action_list_title
            ActionType.BUTTON -> R.string.actionmanager_button_action_list_title
            ActionType.FACIAL_EXPRESSION -> R.string.actionmanager_facial_expression_action_list_title
            else -> R.string.actionmanager_add_action_title   // Error
        }
    }

    private fun getLayoutColor(actionType: ActionType): Int {
        return when (actionType) {
            ActionType.SCREEN_GESTURE -> R.color.orange
            ActionType.BUTTON -> R.color.blue
            ActionType.FACIAL_EXPRESSION -> R.color.red
            else -> R.color.black   // Error
        }
    }
}