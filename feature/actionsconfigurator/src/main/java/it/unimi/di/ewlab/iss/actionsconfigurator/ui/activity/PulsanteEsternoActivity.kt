package it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.ActivityPulsanteEsternoBinding
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler

class PulsanteEsternoActivity: AppCompatActivity() {

    companion object {
        const val BLUETOOTH_PERMISSION_REQUEST_CODE = 10
    }

    private var baseActivityBinding: ActivityPulsanteEsternoBinding? = null
    private lateinit var binding: ActivityPulsanteEsternoBinding
    private lateinit var navController: NavController
    private val mainModel = MainModel.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseActivityBinding = ActivityPulsanteEsternoBinding.inflate(layoutInflater)
        binding = baseActivityBinding as ActivityPulsanteEsternoBinding
        setContentView(binding.root)

        setNavController()

        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
    }

    override fun onResume() {
        super.onResume()

        /*if (!PermissionsHandler.isAccessibilityServiceEnabled(this)) {
            showAccessibilityServiceRequiredToast()
            startActivity(Intent(this, MainActivityConfAzioni::class.java))
        }*/
    }

    /*private fun showAccessibilityServiceRequiredToast() {
        Toast.makeText(
            this,
            R.string.externalbutton_accessibility_service_required,
            Toast.LENGTH_SHORT
        ).show()
    }*/

    private fun setNavController() {
        navController = findNavController(R.id.nav_host_fragment_pulsante_esterno)
        navController.setGraph(
            R.navigation.pulsante_esterno_nav_graph
        )

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.iconInfo.visibility = View.GONE

        //Set toolbar title in base al fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                //Fragment Aggiungi Pulsante Esterno
                R.id.aggiungiPulsanteEsternoFragment -> {
                    binding.toolbarTitle.text =
                        getString(R.string.actiondetails_button_action_info_title)
                }

                //Fragment Rileva Pulsante Esterno
                R.id.rilevaPulsanteEsternoFragment -> {
                    binding.toolbarTitle.text =
                        getString(R.string.actiondetails_button_action_info_title)
                }

                //Fragment Configura Pulsante Esterno
                R.id.configurazionePulsanteEsternoFragment -> {
                    binding.toolbarTitle.text =
                        getString(R.string.actiondetails_button_action_info_title)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode != BLUETOOTH_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        for (permission in grantResults) {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    R.string.externalbutton_no_bluetooth_permission,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    /*
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val action: ButtonAction
        if (KeyEvent.keyCodeToString(keyCode).startsWith("KEYCODE_DPAD")) {
            //TODO: da verificare il codice: 19 corrisponde a KEYCODE_DPAD_UP
            action = ButtonAction(mainModel.nextActionId, KeyEvent.keyCodeToString(keyCode), event!!.source.toString(), 19.toString())
            action.setIs2d(true)
        } else
            action = ButtonAction(mainModel.nextActionId, KeyEvent.keyCodeToString(keyCode), event!!.source.toString(), keyCode.toString())
        mainModel.setTempButtonAction(action)
        return true
    }
    */
}