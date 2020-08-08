package com.zafar.covid19tracking.ui.activity

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
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
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.zafar.covid19tracking.R
import com.zafar.covid19tracking.adapter.CoronaRecyclerViewAdapter
import com.zafar.covid19tracking.model.CoronaEntity
import com.zafar.covid19tracking.model.CountryInfo
import com.zafar.covid19tracking.services.DataService
import com.zafar.covid19tracking.utils.OnTextChangedListener
import kotlinx.android.synthetic.main.activity_maps.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var BaseUrl = "https://coronadatascraper.com/"
    private var progressBar: ProgressBar? = null
    var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val toolbar = customToolbar
        setSupportActionBar(toolbar)
        progressBar = findViewById<ProgressBar>(R.id.progressbar)
        val view = LayoutInflater.from(this@MapsActivity).inflate(R.layout.marker_view, null)
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
                            if (it.active == 0) {
                                active = "N/A"
                            }
                            if (it.deaths == 0) {
                                deaths = "N/A"
                            }
                            if (it.recovered == 0) {
                                recover = "N/A"
                            }
                            if (it.name == "United States") {
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
            R.id.action_download_file -> {
                initListenerForPermission()
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

    private fun initListenerForPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
//                Toast.makeText(this@MapsActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                downloadCSVFile()
                saveToDownloadFolder()
            }

            override fun onPermissionDenied(deniedPermissions: List<String?>) {
                Toast.makeText(
                    this@MapsActivity,
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check();
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

    private fun downloadCSVFile() {
        val csvFilePath =
            File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator.toString() + "latest.csv")
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(DataService::class.java)
        val call = service.downloadFileWithFixedUrl()
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    val writtenToDisk: Boolean =
                        writeResponseBodyToDisk(response.body(), csvFilePath)
                    Log.d("file dwnldd successfuly", writtenToDisk.toString())
                    Toast.makeText(
                        this@MapsActivity,
                        "CSV file saved to " + csvFilePath.absolutePath +" and download folder",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
            }
        })

    }

    private fun writeResponseBodyToDisk(body: ResponseBody?, path: File): Boolean {
        return try {
            // todo change the file location/name according to your needs
//            val csvFile =
//                File(getExternalFilesDir(null).toString() + File.separator.toString() + "latest.csv")

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body?.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body?.byteStream()
                outputStream = FileOutputStream(path)
                while (true) {
                    val read: Int = inputStream!!.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d(
                        "File Download: ",
                        "$fileSizeDownloaded of $fileSize"
                    )
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    private fun saveToDownloadFolder() {
        val request: DownloadManager.Request =
            DownloadManager.Request("https://coronadatascraper.com/latest.csv".toUri())
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "latest.csv"
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // to notify when download is complete
        val manager: DownloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)

        val q = DownloadManager.Query()
        q.setFilterById(id)
        val cursor: Cursor = manager.query(q)
        cursor.moveToFirst()
        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        cursor.close()
    }
}
