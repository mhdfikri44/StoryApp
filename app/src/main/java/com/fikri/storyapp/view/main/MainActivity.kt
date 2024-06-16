package com.fikri.storyapp.view.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fikri.storyapp.R
import com.fikri.storyapp.databinding.ActivityMainBinding
import com.fikri.storyapp.view.map.MapsActivity
import com.fikri.storyapp.view.StoryAdapter
import com.fikri.storyapp.view.ViewModelFactory
import com.fikri.storyapp.view.add.AddActivity
import com.fikri.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()

            } else {
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                @Suppress("DEPRECATION") val activeNetworkInfo =
                    connectivityManager.activeNetworkInfo
                @Suppress("DEPRECATION") val isConnected =
                    activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting

                viewModel.getSession().observe(this) {
                    if (!isConnected) {
                        showToast("Tidak ada koneksi internet")
                    } else {

                        val adapter = StoryAdapter()
                        binding.rvStory.layoutManager = LinearLayoutManager(this)
                        binding.rvStory.adapter = adapter
                        viewModel.story.observe(this) {
                            adapter.submitData(lifecycle, it)
                        }
                    }
                }

                binding.addFab.setOnClickListener {
                    startActivity(Intent(this, AddActivity::class.java))
                }
            }
        }

        setupView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Konfirmasi!")
                    setMessage("Yakin ingin logout?")
                    setPositiveButton("Iya") { _, _ ->
                        viewModel.logout()
                    }
                    setNegativeButton("Batal") { _, _ -> }
                    setCancelable(false)
                    create()
                    show()
                }
                true
            }

            R.id.action_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }

            else -> false
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}