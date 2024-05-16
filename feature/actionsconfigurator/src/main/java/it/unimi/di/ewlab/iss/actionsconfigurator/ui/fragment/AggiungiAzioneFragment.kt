package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentAggiungiAzioneBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.FacialExpressionActivity
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.PulsanteEsternoActivity
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.AggiungiAzioneViewModel

class AggiungiAzioneFragment : Fragment() {

    companion object {
        private const val TAG = "AggiungiAzioneFragment"
    }

    private lateinit var binding: FragmentAggiungiAzioneBinding

    val viewModel: AggiungiAzioneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAggiungiAzioneBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        binding.viewmodel = viewModel

        val activityManager =
            requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val configurationInfo = activityManager!!.deviceConfigurationInfo

        Log.d(TAG, "OpenGL ES version: ${configurationInfo.glEsVersion}")
        // Necessario per la libreria di estrazione dei landmark
        if (configurationInfo.reqGlEsVersion < 0x30001)
            binding.aggiungiEspressioneFacciale.visibility = View.GONE
        setListeners()

        return binding.root
    }

    private fun setListeners(){
        binding.backToListaAzioni.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(
                    R.id.action_aggiungiAzioneFragment_to_listaAzioniFragment
                )
        }

        binding.aggiungiPulsanteEsterno.setOnClickListener {
            startActivity(Intent(context, PulsanteEsternoActivity::class.java))
        }

        binding.aggiungiEspressioneFacciale.setOnClickListener {
            startActivity(Intent(context, FacialExpressionActivity::class.java))
        }
    }
}