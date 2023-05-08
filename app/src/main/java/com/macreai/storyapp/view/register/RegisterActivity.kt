package com.macreai.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.macreai.storyapp.R
import com.macreai.storyapp.databinding.ActivityRegisterBinding
import com.macreai.storyapp.model.remote.api.ApiConfig
import com.macreai.storyapp.model.remote.response.PublicResponse
import com.macreai.storyapp.view.customview.EmailEditText
import com.macreai.storyapp.view.customview.NameEditText
import com.macreai.storyapp.view.customview.PasswordEditText
import com.macreai.storyapp.view.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var name: NameEditText
    private lateinit var email: EmailEditText
    private lateinit var password: PasswordEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        name = binding.nameText
        email = binding.emailText
        password = binding.passwordText

        binding.button.isEnabled = false

        buttonEnable()
        buttonClick()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView3, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.nameText, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailText, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordText, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.button, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(name, email, password, register)
            startDelay = 500
        }.start()
    }

    private fun buttonClick() {
        binding.button.setOnClickListener {
            showLoading(true)
            ApiConfig.getApiService().registerUser(
                name.text.toString(),
                email.text.toString(),
                password.text.toString()
            ).enqueue(object : Callback<PublicResponse> {
                override fun onResponse(
                    call: Call<PublicResponse>,
                    response: Response<PublicResponse>
                ) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null){
                        if (responseBody.error) {
                            showLoading(false)
                            Toast.makeText(this@RegisterActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                        } else {
                            startLogin()
                            Toast.makeText(this@RegisterActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        showLoading(false)
                        Log.d(TAG, "onResponse: ${response.message()}")
                        Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PublicResponse>, t: Throwable) {
                    showLoading(false)
                    Log.d(TAG, "onResponse: ${t.message}")
                    Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })

        }

    }

    private fun startLogin() {
        val intentToLogin = Intent(this, LoginActivity::class.java)
        intentToLogin.apply {
            putExtra("email", email.text.toString())
            putExtra("password", password.text.toString())
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intentToLogin)
        showLoading(false)
        finish()
    }

    private fun buttonEnable() {

        binding.nameText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.emailText.text.toString().trim()
                val password = binding.passwordText.text.toString().trim()
                val name = s.toString().trim()

                binding.button.isEnabled = Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 8 && name.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.emailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                binding.button.isEnabled = isValidEmail && binding.passwordText.text.toString().trim().length > 7
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.passwordText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.emailText.text.toString().trim()
                val password = s.toString().trim()

                binding.button.isEnabled = Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 7
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    companion object{
        private const val TAG = "RegisterActivity"
    }
}