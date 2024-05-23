package it.unimi.di.ewlab.iss.common.ui.intro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.unimi.di.ewlab.common.databinding.FragmentIntroActionsConfiguratorBinding

class IntroActionsConfiguratorFragment : Fragment() {

    private val binding: FragmentIntroActionsConfiguratorBinding by lazy {
        FragmentIntroActionsConfiguratorBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }
}