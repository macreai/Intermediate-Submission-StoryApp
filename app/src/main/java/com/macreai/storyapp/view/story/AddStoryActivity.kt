package com.macreai.storyapp.view.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.macreai.storyapp.R
import com.macreai.storyapp.databinding.ActivityAddStoryBinding
import com.macreai.storyapp.feature.Camera
import com.macreai.storyapp.model.remote.api.ApiConfig
import com.macreai.storyapp.model.remote.response.SendStoryResponse
import com.macreai.storyapp.util.*
import com.macreai.storyapp.view.main.MainActivity
import com.macreai.storyapp.viewmodel.AddStoryViewModel
import com.macreai.storyapp.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel

    private var getFile: File? = null

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == CAMERA_X_RESULT){
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let {
                rotateFile(it, isBackCamera)
                getFile = it
                binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(it.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == RESULT_OK){
            val selectedImg = result.data?.data as Uri

            selectedImg.let {
                val myFile = uriToFile(it, this@AddStoryActivity)
                getFile = myFile
                binding.imagePreview.setImageURI(it)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AddStoryViewModel::class.java]

        binding.upload.isEnabled = false

        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.upload.setOnClickListener { uploadFile() }

        validation()
    }

    private fun validation() {

        binding.description.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val description = s.toString()
                
                if (description.isNotEmpty()){
                    binding.upload.isEnabled = true
                } else {
                    binding.upload.isEnabled = false

                    if (description.isEmpty()){
                        binding.description.error = getString(R.string.required_field)
                    } else {
                        binding.description.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun uploadFile() {
        if (getFile != null){
            val file = reduceFileImage(getFile as File)
            val description = binding.description.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            addStoryViewModel.getUser().observe(this, Observer { user: String ->
                val apiService = ApiConfig.getApiService()
                val uploadImageRequest = apiService.postPicture(
                    "Bearer $user", imageMultipart, description
                )
                showLoading(true)
                uploadImageRequest.enqueue(object : Callback<SendStoryResponse>{
                    override fun onResponse(
                        call: Call<SendStoryResponse>,
                        response: Response<SendStoryResponse>
                    ) {
                        if (response.isSuccessful){
                            showLoading(false)
                            val responseBody = response.body()
                            if (responseBody != null && responseBody.error){
                                Toast.makeText(this@AddStoryActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                            }
                            startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
                        } else{
                            showLoading(false)
                            Toast.makeText(this@AddStoryActivity, response.message(), Toast.LENGTH_SHORT).show()

                        }
                    }

                    override fun onFailure(call: Call<SendStoryResponse>, t: Throwable) {
                        showLoading(false)
                        Toast.makeText(this@AddStoryActivity, t.message, Toast.LENGTH_SHORT).show()
                    }

                })
            })

        } else {
            Toast.makeText(this@AddStoryActivity, getString(R.string.pick_picture_first), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intentToCameraX = Intent(this, Camera::class.java)
        launcherIntentCameraX.launch(intentToCameraX)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION){
            if (!allPermissionGranted()){
                Toast.makeText(this, getString(R.string.not_having_permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    companion object{
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}