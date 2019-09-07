package com.example.avjindersinghsekhon.minimaltodo.network

import android.util.Log
import com.example.avjindersinghsekhon.minimaltodo.model.SumUpReceiptResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class APICallRepository {

    fun getRreceipt(transactionCode: String) {
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
                .enqueue(object : Callback<SumUpReceiptResponse> {
                    override fun onResponse(call: Call<SumUpReceiptResponse>,
                                            response: Response<SumUpReceiptResponse>) {
                        Log.d("sumup-", response.body().toString())
                    }

                    override fun onFailure(call: Call<SumUpReceiptResponse>, t: Throwable) {
                        Log.d("sumup-", t.message)
                    }

                })

    }

}