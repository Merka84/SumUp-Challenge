package com.example.avjindersinghsekhon.minimaltodo.network

import com.example.avjindersinghsekhon.minimaltodo.model.SumUpReceiptResponse
import okhttp3.OkHttpClient
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class APICallRepository {

    fun getReceipt(transactionCode: String, callBack: Callback<SumUpReceiptResponse>) {
        val client = OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://receipts-ng.sumup.com")
                .client(client)
                .build()

         retrofit
                .create(APIRequest::class.java)
                .receipt(transactionCode)
                .enqueue(callBack)

    }

}