package it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.ActivityFacialExpressionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.FacialExpressionViewModel
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler
import kotlin.properties.Delegates

class FacialExpressionActivity: AppCompatActivity() {

    companion object {
        const val ARG_ID_ACTION = "FacialExpressionActivity_ARG_ID_ACTION"
        const val CAMERA_PERMISSION_REQUEST_CODE = 20
    }

    private val binding: ActivityFacialExpressionBinding by lazy {
        ActivityFacialExpressionBinding.inflate(layoutInflater)
    }

    private val viewModel: FacialExpressionViewModel by viewModels()

    private lateinit var navController: NavController

    private var actionId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // actionId == -1 -> nuova espressione
        // actionId > 0 -> ri-registrazione di un'espressione
        actionId = intent.getIntExtra(ARG_ID_ACTION, -1)

        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        setNavController()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setCameraPermission(
            PermissionsHandler.checkCameraPermission(this)
        )
    }

    private fun setNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.facialExpressionNavHost.id) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(
            R.navigation.facial_expression_nav_graph,
            if (actionId > 0) bundleOf(ARG_ID_ACTION to actionId) else null
        )

        binding.backBtn.visibility = View.VISIBLE

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //Set toolbar title in base al fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->

            var titleRes: Int? = null
            var iconInfoVisibility: Int? = null

            when (destination.id) {

                // Fragment registrazione espressione
                R.id.recordFacialExpressionFragment -> {
                    titleRes = R.string.feraction_record_facial_expression_title
                    iconInfoVisibility = View.VISIBLE
                }

                // Fragment definizione espressione
                // Fragment espressione neutrale
                R.id.defineFacialExpressionFragment,
                R.id.neutralFacialExpressionFragment,
                -> {
                    titleRes = R.string.feraction_define_facial_expression_title
                    iconInfoVisibility = View.GONE
                }

                // Fragment espressione duplicata
                R.id.duplicateFacialExpressionFragment -> {
                    titleRes = R.string.feraction_duplicate_facial_expression_title
                    iconInfoVisibility = View.GONE
                }
            }

            binding.toolbarTitle.text = getString(titleRes!!)
            binding.iconInfo.visibility = iconInfoVisibility!!

            if (iconInfoVisibility == View.VISIBLE) {
                binding.iconInfo.setOnClickListener {
                    val fragment = navHostFragment.childFragmentManager.fragments[0]
                    if (fragment is InfoFragment)
                        fragment.showInfo()
                }
            } else binding.iconInfo.setOnClickListener(null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode != CAMERA_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        for (permission in grantResults) {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    R.string.feraction_no_camera_permission,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

        viewModel.setCameraPermission(true)
    }
}