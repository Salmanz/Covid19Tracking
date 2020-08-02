package com.zafar.covid19tracking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zafar.covid19tracking.R
import com.zafar.covid19tracking.model.CaseDetails
import kotlinx.android.synthetic.main.item_case.view.*

class CaseAdapter : RecyclerView.Adapter<CaseAdapter.CaseViewModel>() {
    private var cases = mutableListOf<CaseDetails>()
    var listener: RecyclerViewClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CaseViewModel(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_case, parent, false)
        )

    override fun getItemCount() = cases.size

    override fun onBindViewHolder(holder: CaseAdapter.CaseViewModel, position: Int) {
        holder.view.tvArea.text = cases[position].area
        holder.view.tvActive.text = cases[position].active
        holder.view.tvRecovered.text = cases[position].recovered
        holder.view.tvDeath.text = cases[position].death

        holder.view.edit_case.setOnClickListener {
            listener?.onRecyclerViewItemClicked(it, cases[position])
        }
        holder.view.delete_case.setOnClickListener {
            listener?.onRecyclerViewItemClicked(it, cases[position])
        }
    }

    fun setCases(cases: List<CaseDetails>) {
        this.cases = cases as MutableList<CaseDetails>
        notifyDataSetChanged()
    }

    fun addCase(case: CaseDetails) {
        // to add data
        if (!cases.contains(case)){
            cases.add(case)
        } else { // to update the data
            // create index for deleted or edited data
            val index = cases.indexOf(case)
            if (case.isDeleted) {
                cases.removeAt(index)
            } else {
                cases[index] = case
            }
        }
        notifyDataSetChanged()
    }

    class CaseViewModel(val view: View) : RecyclerView.ViewHolder(view)
}