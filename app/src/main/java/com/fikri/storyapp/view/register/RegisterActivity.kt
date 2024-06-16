package com.fikri.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fikri.storyapp.databinding.ActivityRegisterBinding
import com.fikri.storyapp.view.ViewModelFactory
import com.fikri.storyapp.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        toLoginActivity()
        setupView()
        setupAction()
        nameValidation()
        emailValidation()

        viewModel.registerResponse.observe(this) {
            if (it.error) {
                showToast(it.message)
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle("Register Akun!")
                    setMessage(it.message)
                    setCancelable(false)
                    val alertDialog = create()
                    alertDialog.show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
        }
        viewModel.error.observe(this) {
            showToast(it)
        }
        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val name =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val register = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(200)
        val info =
            ObjectAnimator.ofFloat(binding.haveAccountTextView, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(title, name, email, password, register, info)
            start()
        }
    }

    private fun toLoginActivity() {
        val text = "Sudah punya akun? Masuk"
        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#0000FF"))

        val startIndex = text.indexOf("Masuk")
        val endIndex = startIndex + "Masuk".length

        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            foregroundColorSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.haveAccountTextView.text = spannableString
        binding.haveAccountTextView.movementMethod = LinkMovementMethod.getInstance()
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
        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditTextLayout.text.toString()

            val isNameValid = binding.nameEditTextLayout.error
            val isEmailValid = binding.emailEditTextLayout.error
            val isPasswordValid = binding.passwordEditTextLayout.error

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showToast("Silahkan isi semua data")
            } else if (isNameValid != null || isEmailValid != null || isPasswordValid != null) {
                showToast("Lengkapi data yang anda isi")
            } else {
                viewModel.registerUser(name, email, password)
            }
        }
    }

    private fun nameValidation() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = s.toString()

                binding.nameEditTextLayout.error = when {
                    name.isEmpty() -> {
                        "Nama tidak boleh kosong"
                    }

                    name.length < 5 -> {
                        "Nama minimal 5 karakter"
                    }

                    else -> {
                        ""
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun emailValidation() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()

                binding.emailEditTextLayout.error = when {
                    email.isEmpty() -> {
                        "Email tidak boleh kosong"
                    }

                    !email.contains("@") || !email.contains(".") -> {
                        "Email tidak valid"
                    }

                    else -> {
                        ""
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
