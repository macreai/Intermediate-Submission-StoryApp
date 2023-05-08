package com.macreai.storyapp.view.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.macreai.storyapp.R
import com.macreai.storyapp.databinding.ActivityDetailStoryBinding
import com.macreai.storyapp.model.remote.response.ListStoryItem
import com.macreai.storyapp.view.map.MapsActivity

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val story = intent.getParcelableExtra<ListStoryItem>("Story") as ListStoryItem
        Glide.with(applicationContext)
            .load(story.photoUrl)
            .into(binding.storyDetailPhoto)
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
    }
}