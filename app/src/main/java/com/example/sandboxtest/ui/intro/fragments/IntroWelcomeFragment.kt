package com.example.sandboxtest.ui.intro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sandboxtest.databinding.FragmentIntroWelcomeBinding

class IntroWelcomeFragment : Fragment() {

    private val binding: FragmentIntroWelcomeBinding by lazy {
        FragmentIntroWelcomeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }
}