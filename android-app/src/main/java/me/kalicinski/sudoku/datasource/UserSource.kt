package me.kalicinski.sudoku.datasource

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserSource : FirebaseAuth.AuthStateListener {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user:LiveData<FirebaseUser?> = _user

    init {
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        _user.postValue(auth.currentUser)
    }

    fun shutdown() = FirebaseAuth.getInstance().removeAuthStateListener(this)
}