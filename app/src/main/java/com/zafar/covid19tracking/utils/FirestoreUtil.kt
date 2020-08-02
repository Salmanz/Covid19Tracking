package com.zafar.covid19tracking.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.zafar.covid19tracking.model.UserModel

object FirestoreUtil {
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}"
        )

    fun updateUser(userModel: UserModel, onComplete: (String) -> Unit) {

        val task = currentUserDocRef.set(userModel)

        task.continueWith {
            if (it.isSuccessful) {
                onComplete("success")
            }
        }.addOnFailureListener {
            onComplete("failure")
        }
    }
}