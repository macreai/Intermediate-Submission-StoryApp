package com.macreai.storyapp.view.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.macreai.storyapp.R
import com.macreai.storyapp.databinding.ActivityMapsBinding
import com.macreai.storyapp.model.remote.response.ListStoryItem
import com.macreai.storyapp.util.UserPreferences
import com.macreai.storyapp.viewmodel.MapsViewModel
import com.macreai.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var arrayListStoryItem: ArrayList<ListStoryItem>
    private lateinit var mapsViewModel: MapsViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted: Boolean ->
            if (isGranted){
                getMyLocation()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        val pref = UserPreferences.getInstance(dataStore)
        mapsViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MapsViewModel::class.java]

        arrayListStoryItem = ArrayList()

        mapsViewModel.getUser().observe(this, Observer {
            mapsViewModel.getStoriesLocation(it)
        })

        mapsViewModel.listStories.observe(this, Observer {story ->
            story?.forEach { storyData ->
                storyData.lat.let { latitude ->
                    storyData.lon.let {longitude ->
                        val latLng = LatLng(latitude, longitude)
                        mMap.addMarker(MarkerOptions().position(latLng).title(storyData.name).snippet(storyData.description))
                    }
                }
            }
        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        getMyLocation()

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val jakarta = LatLng(-6.2088, 106.8456)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta))
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}