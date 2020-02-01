package com.kshitijpatil.crowdcounter.ui.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kshitijpatil.crowdcounter.Repository
import com.kshitijpatil.crowdcounter.data.Result
import com.kshitijpatil.crowdcounter.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetailsViewModel : ViewModel() {
    private val repository: Repository = Repository()

    val result = MutableLiveData<Result>()

    fun sendRequest(imagePath: String) {
        Log.d("Base64", Utils.getBase64FromPath(imagePath))
        GlobalScope.launch {
            val retrieveData = repository.getPrediction(imagePath)
            result.postValue(retrieveData.body())
        }
    }
}
