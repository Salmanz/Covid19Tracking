package com.zafar.covid19tracking.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zafar.covid19tracking.R
import com.zafar.covid19tracking.model.CaseDetails
import kotlinx.android.synthetic.main.activity_add_case.etActive
import kotlinx.android.synthetic.main.activity_add_case.etArea
import kotlinx.android.synthetic.main.activity_add_case.etDeath
import kotlinx.android.synthetic.main.activity_add_case.etRecovered
import kotlinx.android.synthetic.main.activity_edit_case.*


class EditCaseActivity : AppCompatActivity() {
    private lateinit var viewModel: CasesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_case)
        setSupportActionBar(findViewById(R.id.toolbar))
        //get the viewmodel from the CasesViewModels class
        viewModel = ViewModelProvider(this).get(CasesViewModel::class.java)

        val case : CaseDetails? = intent.getParcelableExtra("case")
        etArea.setText(case?.area)
        etActive.setText(case?.active)
        etRecovered.setText(case?.recovered)
        etDeath.setText(case?.death)

        setUpObservers()
        btnUpdateCase.setOnClickListener {
            val area = etArea.text.toString().trim()
            var active = etActive.text.toString().trim()
            var recovered = etRecovered.text.toString().trim()
            var death = etDeath.text.toString().trim()

            if (area.isEmpty()) {
                Toast.makeText(this@EditCaseActivity, getString(R.string.error_field_required), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if(active.isEmpty()){
                active = "0"
            }
            if(recovered.isEmpty()){
                recovered = "0"
            }
            if(death.isEmpty()){
                death = "0"
            }
            case!!.area = area
            case.active = active
            case.recovered = recovered
            case.death = death
            viewModel.updateCase(case)
        }
    }


    private fun setUpObservers() {
        viewModel.result.observe(this, Observer {
            val message = if (it == null) {
                getString(R.string.case_updated)
            } else {
                getString(R.string.error, it.message)
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            finish()
        })
    }


//        countriesAssetsToArrayList()
//        val countriesSpinner: Spinner = this.spinner_country
//        val countriesAdapter = ArrayAdapter<String>(
//            this@AddCaseActivity,
//            android.R.layout.simple_spinner_dropdown_item,
//            countriesList
//        )
////        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        countriesSpinner.adapter = countriesAdapter
//        countriesSpinner.onItemSelectedListener = this
//
//        val stateSpinner: Spinner = this.spinner_state
//        statesList.add("Select State")
//        val statesAdapter = ArrayAdapter<String>(
//            this@AddCaseActivity,
//            android.R.layout.simple_spinner_dropdown_item,
//            statesList
//        )
//        stateSpinner.adapter = statesAdapter
//        stateSpinner.onItemSelectedListener = this
//
//        val citySpinner: Spinner = this.spinner_city
//        cityList.add("Select City")
//        val cityAdapter = ArrayAdapter<String>(
//            this@AddCaseActivity,
//            android.R.layout.simple_spinner_dropdown_item,
//            cityList
//        )
//        citySpinner.adapter = cityAdapter
//        citySpinner.onItemSelectedListener = this
////
////        val json = loadJsonObjectFromAsset("ref.json")
////        val refList: MutableList<String> = ArrayList()
////        try {
////            val refArray = json!!.getJSONArray("countries")
////            for (i in 0 until refArray.length()) {
////                val ref = refArray.getJSONObject(i).getString("name")
////                refList.add(ref)
////            }
////        } catch (e: JSONException) {
////            e.printStackTrace()
////        }
//    }
//
//    private fun countriesAssetsToArrayList() {
//        countriesList.add("Select Country")
//        try {
//            val obj = JSONObject(loadCountriesJSONFromAsset()!!)
//            val countriesArray = obj.getJSONArray("countries")
//            for (i in 0 until countriesArray.length()) {
//                val country = countriesArray.getJSONObject(i)
//                Log.d("Country : ", country.getString("name"))
//                countriesHashmap[country.getString("id")] = country.getString("name")
//                countriesList.add(country.getString("name"))
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//    }
//
//    private fun statesAssetsToArrayList(selectedCountry: String?) {
//        try {
//            if (selectedCountry != null && selectedCountry != "Select Country") {
//                val selectedCountryId = getKey(countriesHashmap, selectedCountry.toString())
//                statesList.clear()
//                statesHashmap.clear()
//                statesList.add("Select State")
//                val obj = JSONObject(loadStatesJSONFromAsset()!!)
//                val statesArray = obj.getJSONArray("states")
//                for (i in 0 until statesArray.length()) {
//                    val state = statesArray.getJSONObject(i)
////                    Log.d("State : ", state.getString("name"))
//                    if (selectedCountryId.equals(state.getString("country_id"))) {
//                        statesHashmap[state.getString("id")] = state.getString("name")
//                        statesList.add(state.getString("name"))
//                    }
//                }
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//    }
//
//    private fun cityAssetsToArrayList(selectedState: String?) {
//        try {
//            if (selectedState != null && selectedState != "Select State") {
//                val selectedStateId = getKey(statesHashmap, selectedState.toString())
//                cityList.clear()
//                cityList.add("Select City")
//                val obj = JSONObject(loadCityJSONFromAsset()!!)
//                val citiesArray = obj.getJSONArray("cities")
//                for (i in 0 until citiesArray.length()) {
//                    val city = citiesArray.getJSONObject(i)
////                    Log.d("State : ", state.getString("name"))
//                    if (selectedStateId.equals(city.getString("state_id"))) {
//                        cityList.add(city.getString("name"))
//                    }
//                }
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//    }
//
//
//    private fun loadCountriesJSONFromAsset(): String? {
//        var json: String? = null
//        json = try {
//            val `is`: InputStream = this@AddCaseActivity.assets.open("countries.json")
//            val size = `is`.available()
//            val buffer = ByteArray(size)
//            `is`.read(buffer)
//            `is`.close()
//            String(buffer, Charsets.UTF_8)
//        } catch (ex: IOException) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }
//
//    private fun loadStatesJSONFromAsset(): String? {
//        var json: String? = null
//        json = try {
//            val `is`: InputStream = this@AddCaseActivity.assets.open("states.json")
//            val size = `is`.available()
//            val buffer = ByteArray(size)
//            `is`.read(buffer)
//            `is`.close()
//            String(buffer, Charsets.UTF_8)
//        } catch (ex: IOException) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }
//
//    private fun loadCityJSONFromAsset(): String? {
//        var json: String? = null
//        json = try {
//            val `is`: InputStream = this@AddCaseActivity.assets.open("cities.json")
//            val size = `is`.available()
//            val buffer = ByteArray(size)
//            `is`.read(buffer)
//            `is`.close()
//            String(buffer, Charsets.UTF_8)
//        } catch (ex: IOException) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }
//
//    override fun onNothingSelected(p0: AdapterView<*>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        if (p0!!.id == this.spinner_country.id) {
//            val selectedCountry: String? = p0.getItemAtPosition(p2).toString()
//            statesAssetsToArrayList(selectedCountry)
//        } else if (p0.id == this.spinner_state.id) {
//            val selectedState: String? = p0.getItemAtPosition(p2).toString()
//            cityAssetsToArrayList(selectedState)
//        }
//
//    }
//
//    fun <K, V> getKey(map: Map<K, V>, target: V): K? {
//        for ((key, value) in map) {
//            if (target == value) {
//                return key
//            }
//        }
//        return null
//    }
//

}