package com.kshitijpatil.crowdcounter

import com.kshitijpatil.crowdcounter.data.JsonApi
import com.kshitijpatil.crowdcounter.data.RetrofitService

class Repository {
    val client by lazy {
        RetrofitService.createService(JsonApi::class.java)
    }


    suspend fun getPrediction(image: String) = client.getPrediction(image)

}