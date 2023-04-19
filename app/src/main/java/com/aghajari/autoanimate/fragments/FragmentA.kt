package com.aghajari.autoanimate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aghajari.autoanimate.databinding.FragmentABinding
import com.aghajari.autoanimate.startFragment

class FragmentA : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentABinding.inflate(inflater)
        binding.card.setOnClickListener {
            startFragment(
                FragmentB(),
                binding.card,
                binding.title,
                binding.image,
                binding.text,
                binding.footer,
                binding.footerTitle
            )
        }
        return binding.root
    }
}