package com.zafar.covid19tracking.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.zafar.covid19tracking.model.CaseDetails

class CasesViewModel : ViewModel() {
    // make database
    private val dbCases = FirebaseDatabase.getInstance().getReference("Cases")

    // create list of covid-19 cases
    private val _cases = MutableLiveData<List<CaseDetails>>()
    val cases: LiveData<List<CaseDetails>>
        get() = _cases

    // create single covid-19 case
    private val _singleCase = MutableLiveData<CaseDetails>()
    val singleCase: LiveData<CaseDetails>
        get() = _singleCase

    // create result
    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    // function to add data to the firebase realtime database
    fun addCase(caseDetails: CaseDetails) {
        caseDetails.id = dbCases.push().key
        dbCases.child(caseDetails.id!!).setValue(caseDetails).addOnCompleteListener {
            if(it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    // create events for data changes for realtime updates
    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) { }

        //Automatic data updates when the data is edited
        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val case = snapshot.getValue(CaseDetails::class.java)
            case?.id = snapshot.key
            _singleCase.value = case
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val case = snapshot.getValue(CaseDetails::class.java)
            case?.id = snapshot.key
            _singleCase.value = case
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val case = snapshot.getValue(CaseDetails::class.java)
            case?.id = snapshot.key
            case?.isDeleted = true
            _singleCase.value = case
        }
    }

    fun getRealtimeUpdates() {
        dbCases.addChildEventListener(childEventListener)
    }

    //create events to display data on firebase with the fetching method
    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) { }

        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val cases = mutableListOf<CaseDetails>()
                for (authorSnapshot in snapshot.children) {
                    val case = authorSnapshot.getValue(CaseDetails::class.java)
                    case?.id = authorSnapshot.key
                    case?.let { cases.add(it) }
                }
                _cases.value = cases
            }
        }
    }

    // fetch case to display data on firebase
    fun fetchCases() {
        dbCases.addListenerForSingleValueEvent(valueEventListener)
    }

    fun updateCase(case: CaseDetails) {
        dbCases.child(case.id!!).setValue(case).addOnCompleteListener {
            if(it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    fun deleteACase(case: CaseDetails) {
        dbCases.child(case.id!!).setValue(null).addOnCompleteListener {
            if(it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dbCases.removeEventListener(childEventListener)
    }
}