package com.zafar.covid19tracking

import CountryInfo
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.zafar.covid19tracking.adapter.CoronaRecyclerViewAdapter
import com.zafar.covid19tracking.model.CoronaEntity
import com.zafar.covid19tracking.services.DataService
import kotlinx.android.synthetic.main.activity_maps.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var BaseUrl = "https://coronadatascraper.com/"
    private var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        progressBar = findViewById<ProgressBar>(R.id.progressbar)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupRvData()
    }

    companion object {
        private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
        private lateinit var dataAdapter: CoronaRecyclerViewAdapter
    }


    private fun getData() {
        progressBar?.visibility = View.VISIBLE
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client =
            OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(DataService::class.java)
        val call = service.getJsonData()
        call.enqueue(object : Callback<List<CountryInfo>> {
            override fun onResponse(
                call: Call<List<CountryInfo>>,
                response: Response<List<CountryInfo>>
            ) {
                if (response.code() == 200) {
                    val responses = response.body()!!
//                    val res = listOf<CoronaEntity>()
                    var coronaEntity: CoronaEntity
                    var coronaEntityInfo: CoronaEntity.DataInfo
                    var res: MutableList<CoronaEntity> = mutableListOf<CoronaEntity>()
                    var i = 0;
                    responses.forEach {
                        Log.v("name", it.name);
                        if (it.level == "country") {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(it.coordinates[0], it.coordinates[1]))
//                            .anchor(0.5f, 0.5f)
                                    .title(it.name)
                                    .snippet("Active:" + it.active + " " + "Death:" + it.deaths + " " + "Recovered:" + it.recovered)
//                                    .icon(BitmapDescriptorFactory.fromResource(R.layout.marker_view))
                            )
                            coronaEntityInfo = CoronaEntity.DataInfo()
                            coronaEntityInfo.country = it.name
                            coronaEntityInfo.case_confirms = it.active.toLong()
                            coronaEntityInfo.case_deaths = it.deaths.toLong()
                            coronaEntityInfo.case_recovered = it.recovered.toLong()
                            coronaEntity = CoronaEntity("", "", coronaEntityInfo)
                            res.add(coronaEntity)
                            i++
                        }

//

//                            .snippet(markerData.snippet)
//
                    }
                    dataAdapter.setEntity(res)
                    progressBar?.visibility = View.GONE

                }
            }

            override fun onFailure(call: Call<List<CountryInfo>>, t: Throwable) {
                t.localizedMessage
                Toast.makeText(this@MapsActivity, "Network call failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_json
                )
            )
            if (!success) {
                Log.e("FragmentActivity", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("FragmentActivity", "Can't find style. Error: ", e)
        }

        mMap = googleMap
        val egypt = LatLng(26.8206, 30.8025)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(egypt))

        getData();
    }

    private fun setupRvData() {

        dataAdapter =
            CoronaRecyclerViewAdapter(
                this
            )

        rvData.apply {
            layoutManager = LinearLayoutManager(this@MapsActivity)
            setHasFixedSize(true)
        }

        rvData.adapter = dataAdapter
        rvData.adapter?.notifyDataSetChanged()
    }
}
