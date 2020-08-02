package com.zafar.covid19tracking.adapter

import android.view.View
import com.zafar.covid19tracking.model.CaseDetails

interface RecyclerViewClickListener {
    fun onRecyclerViewItemClicked(view: View, case: CaseDetails)
}