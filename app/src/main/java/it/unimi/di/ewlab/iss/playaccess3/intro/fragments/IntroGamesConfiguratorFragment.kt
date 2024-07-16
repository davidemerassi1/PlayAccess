package it.unimi.di.ewlab.iss.playaccess3.intro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.unimi.di.ewlab.iss.playaccess3.databinding.FragmentIntroGamesConfiguratorBinding

class IntroGamesConfiguratorFragment : Fragment() {

    private val binding: FragmentIntroGamesConfiguratorBinding by lazy {
        FragmentIntroGamesConfiguratorBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }
}