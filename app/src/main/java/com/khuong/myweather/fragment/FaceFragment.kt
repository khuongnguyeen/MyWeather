package com.khuong.myweather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.khuong.myweather.databinding.PermistionBinding

class FaceFragment:Fragment() {
    private lateinit var binding:PermistionBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PermistionBinding.inflate(inflater, container,false)
        return binding.root
    }
}