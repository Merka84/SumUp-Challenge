package com.example.avjindersinghsekhon.minimaltodo.network

import com.example.avjindersinghsekhon.minimaltodo.model.SumUpReceiptResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface APIRequest {
    @GET( "https://receipts-ng.sumup.com/v0.1/receipts/{code}?mid=MVVLAQ7E")
    fun receipt(@Path("code") transactionCode: String): Call<SumUpReceiptResponse>
}