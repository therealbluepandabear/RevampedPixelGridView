package com.therealbluepandabear.sizingtests

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.therealbluepandabear.sizingtests.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.zoomIn.setOnClickListener {
            binding.drawingView.zoomIn()
        }

        binding.zoomOut.setOnClickListener {
            binding.drawingView.zoomOut()
        }

        binding.zoomIn.setOnLongClickListener {
            binding.drawingView.toggleMoveMode()
            true
        }
    }
}