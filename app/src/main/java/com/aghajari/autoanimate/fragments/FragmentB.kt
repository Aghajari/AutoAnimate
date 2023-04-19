package com.aghajari.autoanimate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aghajari.autoanimate.databinding.FragmentBBinding
import com.aghajari.autoanimate.popBackStack

class FragmentB : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBBinding.inflate(inflater)
        binding.card.setOnClickListener {
            popBackStack()
        }
        return binding.root
    }
}