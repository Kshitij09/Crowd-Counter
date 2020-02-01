package com.kshitijpatil.crowdcounter.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JsonApi {

    @POST("/predict_img")
    suspend fun getPrediction(@Body file: String): Response<Result>
}

data class Result(private val count: String)