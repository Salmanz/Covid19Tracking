package com.zafar.covid19tracking.ui.activity

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
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
import com.zafar.covid19tracking.R
import com.zafar.covid19tracking.adapter.CoronaRecyclerViewAdapter
import com.zafar.covid19tracking.model.CoronaEntity
import com.zafar.covid19tracking.model.CountryInfo
import com.zafar.covid19tracking.services.DataService
import com.zafar.covid19tracking.utils.OnTextChangedListener
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
    var bitmap : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val toolbar = customToolbar
        setSupportActionBar(toolbar)
        progressBar = findViewById<ProgressBar>(R.id.progressbar)
        val view = LayoutInflater.from(this@MapsActivity).inflate(R.layout.marker_view,null)
        bitmap = getBitmapFromView(view)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupRvData()
        initListener()
    }

    companion object {
        private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
        private lateinit var dataAdapter: CoronaRecyclerViewAdapter
    }

    fun getBitmapFromView(view: View): Bitmap? {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
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
                            var active = it.active.toString()
                            var deaths = it.deaths.toString()
                            var recover = it.recovered.toString()
                            var lat = it.coordinates[0]
                            var lng = it.coordinates[1]
                            if(it.active == 0){
                                active = "N/A"
                            }
                            if(it.deaths == 0){
                                deaths = "N/A"
                            }
                            if(it.recovered == 0){
                                recover = "N/A"
                            }
                            if(it.name == "United States"){
                                lat = -95.7129
                                lng = 37.0902

                            }
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(lng, lat))
//                                    .position(LatLng(it.coordinates[1], it.coordinates[0]))
//                            .anchor(0.5f, 0.5f)
                                    .title(it.name)
                                    .snippet("Active:" + active + "  Deaths:" + deaths + "  Recovered:" + recover)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            )
                            coronaEntityInfo = CoronaEntity.DataInfo()
                            coronaEntityInfo.country = it.name
                            coronaEntityInfo.case_confirms = it.active.toLong()
                            coronaEntityInfo.case_deaths = it.deaths.toLong()
                            coronaEntityInfo.case_recovered = it.recovered.toLong()
                            coronaEntityInfo.case_total = it.cases.toLong()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_covid19_cases -> {
                startActivity(Intent(this, CasesListActivity::class.java))
                true
            }
            R.id.action_admin_panel -> {
                startActivity(Intent(this, HostActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initListener() {

        edt_search.OnTextChangedListener { text ->
            dataAdapter.filter.filter(text)
        }

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

        rvData.adapter =
            dataAdapter
        rvData.adapter?.notifyDataSetChanged()
    }
}
