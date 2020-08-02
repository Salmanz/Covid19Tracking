package com.zafar.covid19tracking.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.zafar.covid19tracking.R
import com.zafar.covid19tracking.adapter.CaseAdapter
import com.zafar.covid19tracking.adapter.RecyclerViewClickListener
import com.zafar.covid19tracking.model.CaseDetails
import com.zafar.covid19tracking.utils.MyApplication.Companion.currentUser
import kotlinx.android.synthetic.main.activity_cases.*
import kotlinx.android.synthetic.main.content_cases.*

class CasesListActivity : AppCompatActivity(),
    RecyclerViewClickListener {
    private lateinit var viewModel: CasesViewModel
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val adapter = CaseAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cases)
        setSupportActionBar(findViewById(R.id.toolbar))
        viewModel = ViewModelProvider(this).get(CasesViewModel::class.java)
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            if (!currentUser?.email.equals("admin@admin.com")) {
                AlertDialog.Builder(this@CasesListActivity).also {
                    it.setTitle(getString(R.string.only_admin_can))
                    it.setNegativeButton(getString(R.string.ok)) { dialog, which ->
                        dialog.dismiss()
                    }
                }.create().show()
            } else {
                startActivity(Intent(this, AddCaseActivity::class.java))
            }
        }

        adapter.listener = this
        this.rvCases.adapter = adapter

        // call the view model to fetch data
        viewModel.fetchCases()

        viewModel.getRealtimeUpdates()

        viewModel.cases.observe(this, Observer {
            adapter.setCases(it)
//            Snackbar.make(this.requireViewById(), "Covid-19 cases fetched!", Snackbar.LENGTH_SHORT).show()
            Toast.makeText(this@CasesListActivity, "Covid-19 cases fetched!", Toast.LENGTH_LONG)
                .show()
            this.progressBar.visibility = View.GONE
        })

        viewModel.singleCase.observe(this, Observer {
            adapter.addCase(it)
        })


    }


    override fun onRecyclerViewItemClicked(view: View, case: CaseDetails) {
        when (view.id) {
            R.id.edit_case -> {
                if (!currentUser?.email.equals("admin@admin.com")) {
                    AlertDialog.Builder(this@CasesListActivity).also {
                        it.setTitle(getString(R.string.only_admin_can))
                        it.setNegativeButton(getString(R.string.ok)) { dialog, which ->
                            dialog.dismiss()
                        }
                    }.create().show()
                } else {
                    val intent = Intent(this@CasesListActivity, EditCaseActivity::class.java)
                    intent.putExtra("case", case)
                    startActivity(intent)
                }
            }
            R.id.delete_case -> {
                if (!currentUser?.email.equals("admin@admin.com")) {
                    AlertDialog.Builder(this@CasesListActivity).also {
                        it.setTitle(getString(R.string.only_admin_can))
                        it.setNegativeButton(getString(R.string.ok)) { dialog, which ->
                            dialog.dismiss()
                        }
                    }.create().show()
                } else {
                    AlertDialog.Builder(this@CasesListActivity).also {
                        it.setTitle(getString(R.string.delete_confirmation))
                        it.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                            viewModel.deleteACase(case)
                        }
                        it.setNegativeButton(getString(R.string.no)) { dialog, which ->
                            dialog.dismiss()
                        }
                    }.create().show()
                }

            }
        }
    }
}