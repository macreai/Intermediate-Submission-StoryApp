package com.macreai.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.macreai.storyapp.R
import com.macreai.storyapp.databinding.ActivityLoginBinding
import com.macreai.storyapp.model.remote.api.ApiConfig
import com.macreai.storyapp.model.remote.response.LoginResponse
import com.macreai.storyapp.model.remote.response.LoginResult
import com.macreai.storyapp.util.UserPreferences
import com.macreai.storyapp.view.customview.EmailEditText
import com.macreai.storyapp.view.customview.PasswordEditText
import com.macreai.storyapp.view.main.MainActivity
import com.macreai.storyapp.view.register.RegisterActivity
import com.macreai.storyapp.viewmodel.LoginViewModel
import com.macreai.storyapp.viewmodel.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: EmailEditText
    private lateinit var password: PasswordEditText
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()


        binding.loginBtn.isEnabled = false

        email = binding.emailText
        password = binding.passwordText

        val pref = UserPreferences.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]


        buttonEnable()
        buttonCLick()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding.emailText, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordText, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginBtn, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.register, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(email, password, login, register)
            startDelay = 500
        }.start()
    }

    private fun buttonCLick() {

        val ss = SpannableString(applicationContext.getText(R.string.not_yet_have_an_account))

        val clickableSpan: ClickableSpan = object: ClickableSpan(){
            override fun onClick(widget: View) {
                val intentToRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intentToRegister)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.WHITE
                ds.isUnderlineText = true
            }
        }

        ss.setSpan(clickableSpan, 25, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.register.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
        }

        binding.loginBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        showLoading(true)
        val client = ApiConfig.getApiService().loginUser(email.text.toString(), password.text.toString())
        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    if (responseBody.error){
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                    } else {
                        saveAuth(responseBody.loginResult)
                        Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showLoading(false)
                    Log.e(TAG, "onFailure: ${response.message()}")
                    Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun saveAuth(loginResult: LoginResult) {
        showLoading(false)
        loginViewModel.saveUser(loginResult.token as String)
        val intentToMain = Intent(this, MainActivity::class.java)
        intentToMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToMain)
        finish()
    }

    private fun buttonEnable() {

        binding.emailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                binding.loginBtn.isEnabled = isValidEmail && binding.passwordText.text.toString().trim().length > 7
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.passwordText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.emailText.text.toString().trim()
                val password = s.toString().trim()

                binding.loginBtn.isEnabled = Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 7
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun showLoading(isLoading: Boolean){
        if (isLoading) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    companion object{
        private const val TAG = "LoginActivity"
    }

}