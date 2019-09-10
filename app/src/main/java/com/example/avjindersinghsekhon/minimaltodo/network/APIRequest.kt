package com.example.avjindersinghsekhon.minimaltodo.network

import com.example.avjindersinghsekhon.minimaltodo.model.SumUpReceiptResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIRequest {
    @GET( "https://receipts-ng.sumup.com/v0.1/receipts/{txCode}")
    fun receipt(@Path("txCode") transactionCode: String,
                @Query("mid") merchantCode: String): Call<SumUpReceiptResponse>
}