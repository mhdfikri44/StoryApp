package com.fikri.storyapp.view.detail

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fikri.storyapp.databinding.ActivityDetailBinding
import com.fikri.storyapp.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        supportActionBar?.apply {
            title = "Story Detail"
            setDisplayHomeAsUpEnabled(true)
        }

        val storyId = intent.getStringExtra("storyId")
        if (storyId != null) {
            viewModel.getStory(storyId)
        }

        viewModel.story.observe(this) { story ->
            binding.apply {
                detailName.text = story.name
                detailDescription.text = story.description
                Glide.with(root)
                    .load(story.photoUrl)
                    .into(detailImage)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}